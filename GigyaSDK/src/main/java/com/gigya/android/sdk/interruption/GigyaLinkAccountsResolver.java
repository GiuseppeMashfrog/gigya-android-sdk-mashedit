package com.gigya.android.sdk.interruption;

import android.content.Context;
import android.support.v4.util.Pair;

import com.gigya.android.sdk.Config;
import com.gigya.android.sdk.GigyaDefinitions;
import com.gigya.android.sdk.GigyaLogger;
import com.gigya.android.sdk.GigyaLoginCallback;
import com.gigya.android.sdk.api.IApiObservable;
import com.gigya.android.sdk.api.IApiService;
import com.gigya.android.sdk.model.account.ConflictingAccounts;
import com.gigya.android.sdk.model.account.GigyaAccount;
import com.gigya.android.sdk.network.GigyaApiRequest;
import com.gigya.android.sdk.network.GigyaApiResponse;
import com.gigya.android.sdk.network.adapter.RestAdapter;
import com.gigya.android.sdk.providers.IProviderFactory;
import com.gigya.android.sdk.session.ISessionService;
import com.gigya.android.sdk.utils.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class GigyaLinkAccountsResolver<A extends GigyaAccount> extends GigyaResolver<A> {

    private static final String LOG_TAG = "GigyaLinkAccountsResolver";

    private ConflictingAccounts conflictingAccounts;
    private IProviderFactory _providerFactory;

    public GigyaLinkAccountsResolver(Config config, ISessionService sessionService, IProviderFactory providerFactory,
                                     IApiService apiService, IApiObservable observable, GigyaApiResponse originalResponse, GigyaLoginCallback<A> loginCallback) {
        super(config, sessionService, apiService, observable, originalResponse, loginCallback);
        _providerFactory = providerFactory;
    }

    public ConflictingAccounts getConflictingAccounts() {
        return conflictingAccounts;
    }

    @Override
    public void start() {
        GigyaLogger.debug(LOG_TAG, "init: sending fetching conflicting accounts");
        // Get conflicting accounts.
        GigyaApiRequest request = GigyaApiRequest.newInstance(_config, _sessionService, GigyaDefinitions.API.API_GET_CONFLICTING_ACCOUNTS,
                ObjectUtils.mapOf(Collections.singletonList(new Pair<String, Object>("regToken", _regToken))), RestAdapter.POST);
//        _api.send(request, false, new ApiService.IApiServiceResponse<GigyaApiResponse>() {
//            @Override
//            public void onApiAdapterSuccess(GigyaApiResponse apiResponse) {
//                GigyaLinkAccountsResolver.this.conflictingAccounts = apiResponse.getField("conflictingAccount", ConflictingAccounts.class);
//                if (GigyaLinkAccountsResolver.this.conflictingAccounts == null) {
//                    forwardError(GigyaError.generalError());
//                } else {
//                    if (isAttached()) {
//                        _loginCallback.get().onConflictingAccounts(_originalResponse, GigyaLinkAccountsResolver.this);
//                    }
//                }
//            }
//
//            @Override
//            public void onApiError(GigyaError gigyaError) {
//                forwardError(gigyaError);
//            }
//        });
    }

    @Override
    public void clear() {
        _regToken = null;
        this.conflictingAccounts = null;
        if (isAttached()) {
            _loginCallback.clear();
        }
    }

    public void resolveForSite(String loginID, String password) {
        if (isAttached()) {
            final Map<String, Object> params = ObjectUtils.mapOf(Arrays.asList(
                    new Pair<String, Object>("loginID", loginID),
                    new Pair<String, Object>("password", password),
                    new Pair<String, Object>("loginMode", "link"),
                    new Pair<String, Object>("regToken", _regToken)));
            final String api = GigyaDefinitions.API.API_LOGIN;
            //_rqObservable.send(api, params, _loginCallback.get());
        }
    }

    public void resolveForSocial(final Context context, String providerName) {
        if (isAttached()) {
//            Provider provider = _providerFactory.providerFor(providerName, _loginCallback.get());
//            provider.setRegToken(_regToken);
//            Map<String, Object> params = new HashMap<>();
//            params.put("provider", provider);
//            provider.login(context, params, "link");
        }
    }

    void finalizeFlow(String regToken) {
        if (isAttached()) {
            final Map<String, Object> params = ObjectUtils.mapOf(Arrays.asList(
                    new Pair<String, Object>("regToken", regToken),
                    new Pair<String, Object>("include", "profile,data,emails,subscriptions,preferences"),
                    new Pair<String, Object>("includeUserInfo", "true")));
            final String api = GigyaDefinitions.API.API_FINALIZE_REGISTRATION;
            //_rqObservable.send(api, params, _loginCallback.get());
        }
    }
}
