package com.gigya.android.sdk;

import com.gigya.android.sdk.api.account_dep.FinalizeRegistrationApi;
import com.gigya.android.sdk.api.account_dep.NotifyLoginApi;
import com.gigya.android.sdk.model.Configuration;
import com.gigya.android.sdk.model.account.GigyaAccount;
import com.gigya.android.sdk.model.account.SessionInfo;
import com.gigya.android.sdk.network.GigyaApiResponse;
import com.gigya.android.sdk.network.GigyaError;
import com.gigya.android.sdk.network.GigyaInterceptionCallback;
import com.gigya.android.sdk.network.adapter.NetworkAdapter;
import com.gigya.android.sdk.providers.LoginProvider;
import com.gigya.android.sdk.utils.ObjectUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

@Deprecated
@SuppressWarnings("WeakerAccess")
public class ApiManager {

    public static final String LOG_TAG = "ApiManager";

    private NetworkAdapter _networkAdapter;
    private SessionManager _sessionManager;
    private AccountManager _accountManager;

    public ApiManager(NetworkAdapter networkAdapter,
                      SessionManager sessionManager, AccountManager accountManager) {
        _networkAdapter = networkAdapter;
        _sessionManager = sessionManager;
        _accountManager = accountManager;
    }

    public void loadConfig(final Runnable completionHandler) {
        final Configuration configuration = _sessionManager.getConfiguration();
//        new SdkConfigApi(_networkAdapter, _sessionManager).call(new GigyaCallback<SdkConfigApi.SdkConfig>() {
//            @Override
//            public void onSuccess(SdkConfigApi.SdkConfig obj) {
//                if (configuration.hasApiKey()) {
//                    configuration.setIDs(obj.getIds());
//                    configuration.setAppIds(obj.getAppIds());
//                    if (_sessionManager != null) {
//                        /* Trigger session save in order to keep track of GMID, UCID. */
//                        _sessionManager.save();
//                    }
//                    _networkAdapter.release();
//                }
//                if (completionHandler != null) {
//                    completionHandler.run();
//                }
//            }
//
//            @Override
//            public void onError(GigyaError error) {
//                GigyaLogger.error(LOG_TAG, "getSDKConfig error: " + error.toString());
//            }
//        });
    }

    public void sendAnonymous(String api, Map<String, Object> params, final GigyaCallback<GigyaApiResponse> callback) {
        final Configuration configuration = _sessionManager.getConfiguration();
        if (!configuration.hasApiKey()) {
            GigyaLogger.error(LOG_TAG, "Configuration invalid. Api-Key unavailable");
            return;
        }
        new AnonymousApi<GigyaApiResponse>(_networkAdapter, _sessionManager, _accountManager)
                .call(api, params, callback);
    }

    public <H> void sendAnonymous(String api, Map<String, Object> params, Class<H> clazz, GigyaCallback<H> callback) {
        final Configuration configuration = _sessionManager.getConfiguration();
        if (!configuration.hasApiKey()) {
            GigyaLogger.error(LOG_TAG, "Configuration invalid. Api-Key unavailable");
            return;
        }
        new AnonymousApi<>(_networkAdapter, _sessionManager, _accountManager, clazz)
                .call(api, params, callback);
    }

    public void logout() {
        new LogoutApi(_networkAdapter, _sessionManager).call();
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void login(Map<String, Object> params, GigyaLoginCallback callback) {
        _accountManager.invalidateAccount();
        new LoginApi<T>(_networkAdapter, _sessionManager, _accountManager).call(params, callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void getAccount(GigyaCallback callback) {
        if (_accountManager.getCachedAccount()) {
            /* Always return a deep copy. */
            callback.onSuccess(ObjectUtils.deepCopy(new Gson(), _accountManager.getAccount(),
                    _accountManager.getAccountClazz()));
            return;
        }
        _accountManager.nextAccountInvalidationTimestamp();
        new GetAccountApi<T>(_networkAdapter, _sessionManager, _accountManager).call(callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void setAccount(T account, GigyaCallback callback) {
        new SetAccountApi<>(_networkAdapter, _sessionManager, _accountManager, account, _accountManager.getAccount()).call(callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void register(Map<String, Object> params, RegisterApi.RegisterPolicy policy, boolean finalize, GigyaLoginCallback<T> callback) {
        _accountManager.invalidateAccount();
        new RegisterApi<T>(_networkAdapter, _sessionManager, _accountManager, policy, finalize).call(params, callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void finalizeRegistration(String regToken, GigyaLoginCallback<T> callback, Runnable completionHandler) {
        new FinalizeRegistrationApi<T>(_networkAdapter, _sessionManager, _accountManager)
                .call(regToken, callback, completionHandler);
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void notifyLogin(String providerSessions, GigyaLoginCallback<T> callback, final Runnable completionHandler) {
        new NotifyLoginApi<T>(_networkAdapter, _sessionManager, _accountManager)
                .call(providerSessions, callback, new GigyaInterceptionCallback<T>() {
                    @Override
                    public void intercept(T obj) {
                        if (completionHandler != null) {
                            completionHandler.run();
                        }
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T extends GigyaAccount> void notifyLogin(SessionInfo sessionInfo, final GigyaCallback<T> callback, final Runnable completionHandler) {
        new NotifyLoginApi<T>(_networkAdapter, _sessionManager, _accountManager)
                .call(sessionInfo, callback, new GigyaInterceptionCallback<T>() {
                    @Override
                    public void intercept(T obj) {
                        if (completionHandler != null) {
                            completionHandler.run();
                        }
                    }
                });
    }

    public void updateProviderSessions(String providerSession, final LoginProvider.LoginPermissionCallbacks permissionCallbacks) {
        new RefreshProviderSessionApi(_networkAdapter, _sessionManager)
                .call(providerSession, new GigyaCallback<GigyaApiResponse>() {
                    @Override
                    public void onSuccess(GigyaApiResponse obj) {
                        GigyaLogger.debug(LOG_TAG, "onProviderTrackingTokenChanges: Success - provider token updated");

                        if (permissionCallbacks != null) {
                            permissionCallbacks.granted();
                        }
                        /* Invalidate cached account. */
                        _accountManager.invalidateAccount();
                    }

                    @Override
                    public void onError(GigyaError error) {
                        GigyaLogger.debug(LOG_TAG, "onProviderTrackingTokenChanges: Error: " + error.getLocalizedMessage());

                        if (permissionCallbacks != null) {
                            permissionCallbacks.failed(error.getLocalizedMessage());
                        }
                    }
                });
    }

    public void forgotPassword(String email, GigyaCallback<GigyaApiResponse> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("loginID", email);
        new ResetPasswordApi(_networkAdapter, _sessionManager).call(params, callback);
    }
}
