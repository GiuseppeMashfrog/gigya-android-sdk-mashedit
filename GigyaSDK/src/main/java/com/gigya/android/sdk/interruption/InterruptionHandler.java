package com.gigya.android.sdk.interruption;

import com.gigya.android.sdk.Config;
import com.gigya.android.sdk.GigyaLogger;
import com.gigya.android.sdk.GigyaLoginCallback;
import com.gigya.android.sdk.api.IApiObservable;
import com.gigya.android.sdk.api.IApiService;
import com.gigya.android.sdk.interruption.tfa.GigyaTFARegistrationResolver;
import com.gigya.android.sdk.interruption.tfa.GigyaTFAVerificationResolver;
import com.gigya.android.sdk.network.GigyaApiResponse;
import com.gigya.android.sdk.network.GigyaError;
import com.gigya.android.sdk.session.ISessionService;

public class InterruptionHandler implements IInterruptionsHandler {

    public static final String LOG_TAG = "InterruptionHandler";

    //Dependencies
    final private Config _config;
    final private ISessionService _sessionService;
    final private IApiService _apiService;

    private boolean _enabled = true;

    public InterruptionHandler(Config config, ISessionService sessionService, IApiService apiService) {
        _config = config;
        _sessionService = sessionService;
        _apiService = apiService;
    }

    @Override
    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return _enabled;
    }

    @Override
    public void resolve(GigyaApiResponse apiResponse, IApiObservable observable, GigyaLoginCallback loginCallback) {
        if (_enabled) {
            final int errorCode = apiResponse.getErrorCode();
            GigyaLogger.debug(LOG_TAG, "resolve: with errorCode = " + errorCode);
            switch (errorCode) {
                case GigyaError.Codes.ERROR_ACCOUNT_PENDING_VERIFICATION:
                    loginCallback.onPendingRegistration(apiResponse, getRegToken(apiResponse));
                    break;
                case GigyaError.Codes.ERROR_ACCOUNT_PENDING_REGISTRATION:
                    loginCallback.onPendingVerification(apiResponse, getRegToken(apiResponse));
                    break;
                case GigyaError.Codes.ERROR_PENDING_PASSWORD_CHANGE:
                    loginCallback.onPendingPasswordChange(apiResponse);
                    break;
                case GigyaError.Codes.ERROR_LOGIN_IDENTIFIER_EXISTS:
                    // TODO: 10/04/2019  
                    break;
                case GigyaError.Codes.ERROR_PENDING_TWO_FACTOR_REGISTRATION:
                    @SuppressWarnings("unchecked")
                    GigyaTFARegistrationResolver registrationResolver = new GigyaTFARegistrationResolver(_config, _sessionService, _apiService,
                            observable, apiResponse, loginCallback);
                    registrationResolver.start();
                    break;
                case GigyaError.Codes.ERROR_PENDING_TWO_FACTOR_VERIFICATION:
                    @SuppressWarnings("unchecked")
                    GigyaTFAVerificationResolver verificationResolver = new GigyaTFAVerificationResolver(_config, _sessionService, _apiService,
                            observable, apiResponse, loginCallback);
                    verificationResolver.start();
                    break;
                case GigyaError.Codes.SUCCESS_ERROR_ACCOUNT_LINKED:
                    // TODO: 10/04/2019 Resolve.
                    break;
                default:
                    break;
            }
        }
    }

    private String getRegToken(GigyaApiResponse apiResponse) {
        return apiResponse.getField("regToken", String.class);
    }
}
