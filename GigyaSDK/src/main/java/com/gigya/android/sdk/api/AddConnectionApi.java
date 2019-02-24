package com.gigya.android.sdk.api;

import com.gigya.android.sdk.GigyaLoginCallback;
import com.gigya.android.sdk.model.GigyaAccount;
import com.gigya.android.sdk.model.SessionInfo;
import com.gigya.android.sdk.network.GigyaError;
import com.gigya.android.sdk.network.GigyaRequest;
import com.gigya.android.sdk.network.GigyaRequestBuilder;
import com.gigya.android.sdk.network.GigyaResponse;
import com.gigya.android.sdk.network.adapter.INetworkCallbacks;
import com.gigya.android.sdk.network.adapter.NetworkAdapter;

import org.json.JSONObject;

import java.util.Map;

import static com.gigya.android.sdk.network.GigyaResponse.OK;

@SuppressWarnings("unchecked")
public class AddConnectionApi<T extends GigyaAccount> extends BaseLoginApi<T> {

    private static final String API = "socialize.addConnection";

    public AddConnectionApi(Class<T> clazz) {
        super(clazz);
    }

    public void call(final Map<String, Object> params, final GigyaLoginCallback<T> callback) {
        GigyaRequest gigyaRequest = new GigyaRequestBuilder(configuration)
                .api(API)
                .params(params)
                .httpMethod(NetworkAdapter.Method.GET)
                .sessionManager(sessionManager)
                .build();
        networkAdapter.send(gigyaRequest, new INetworkCallbacks() {
            @Override
            public void onResponse(String jsonResponse) {
                if (callback == null) {
                    return;
                }
                try {
                    final GigyaResponse response = new GigyaResponse(new JSONObject(jsonResponse));
                    final int statusCode = response.getStatusCode();
                    if (statusCode == OK) {
                        /* Update session info */
                        if (response.contains("sessionInfo") && sessionManager != null) {
                            SessionInfo session = response.getField("sessionInfo", SessionInfo.class);
                            sessionManager.setSession(session);
                        }
                        // To avoid writing a clone constructor.
                        T interception = (T) response.getGson().fromJson(jsonResponse, clazz != null ? clazz : GigyaAccount.class);
                        T parsed = (T) response.getGson().fromJson(jsonResponse, clazz != null ? clazz : GigyaAccount.class);
                        // Update account.
                        accountManager.setAccount(interception);
                        callback.onSuccess(parsed);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    callback.onError(GigyaError.generalError());
                }
            }

            @Override
            public void onError(GigyaError gigyaError) {
                if (callback != null) {
                    callback.onError(gigyaError);
                }
            }
        });
    }

}