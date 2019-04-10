package com.gigya.android.sdk.api;

import com.gigya.android.sdk.GigyaLoginCallback;
import com.gigya.android.sdk.providers.IProviderPermissionsCallback;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ApiObservable extends Observable implements IApiObservable {

    @Override
    public synchronized void send(String api, Map<String, Object> params, GigyaLoginCallback callback) {
        final ApiObservableData data = new ApiObservableData(api, params, callback, null);
        notifyObservers(data);
    }

    @Override
    public synchronized void send(String api, Map<String, Object> params, GigyaLoginCallback callback, Runnable completionHandler) {
        final ApiObservableData data = new ApiObservableData(api, params, callback, completionHandler);
        notifyObservers(data);
    }

    @Override
    public void send(String api, Map<String, Object> params, IProviderPermissionsCallback permissionsCallback) {
        final ApiObservableData data = new ApiObservableData(api, params, permissionsCallback, null);
        notifyObservers(data);
    }

    @Override
    public synchronized ApiObservable register(Observer observer) {
        addObserver(observer);
        return this;
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }

    @Override
    public void dispose() {
        deleteObservers();
    }

    //region DATA

    public static class ApiObservableData {

        final private String api;
        final private Map<String, Object> params;
        final private Runnable completionHandler;

        private GigyaLoginCallback loginCallback;
        private IProviderPermissionsCallback permissionsCallback;

        ApiObservableData(String api, Map<String, Object> params, GigyaLoginCallback loginCallback, Runnable completionHandler) {
            this.api = api;
            this.params = params;
            this.loginCallback = loginCallback;
            this.completionHandler = completionHandler;
        }

        ApiObservableData(String api, Map<String, Object> params, IProviderPermissionsCallback permissionsCallback, Runnable completionHandler) {
            this.api = api;
            this.params = params;
            this.permissionsCallback = permissionsCallback;
            this.completionHandler = completionHandler;
        }

        public String getApi() {
            return api;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public GigyaLoginCallback getLoginCallback() {
            return loginCallback;
        }

        public Runnable getCompletionHandler() {
            return completionHandler;
        }

        public IProviderPermissionsCallback getPermissionsCallback() {
            return permissionsCallback;
        }
    }

    //endregion
}
