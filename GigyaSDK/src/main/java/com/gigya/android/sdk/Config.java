package com.gigya.android.sdk;

public class Config {

    private String apiKey;
    private String apiDomain;
    private String gmid;
    private String ucid;
    private int accountCacheTime;
    private boolean interruptionsEnabled = true;
    private int sessionVerificationInterval = 0;

    //region UPDATE

    public Config updateWith(String apiKey, String apiDomain) {
        this.apiKey = apiKey;
        this.apiDomain = apiDomain;
        return this;
    }

    public Config updateWith(String apiKey, String apiDomain, int accountCacheTime, int sessionVerificationInterval) {
        this.apiKey = apiKey;
        this.apiDomain = apiDomain;
        this.accountCacheTime = accountCacheTime;
        this.sessionVerificationInterval = sessionVerificationInterval;
        return this;
    }

    public Config updateWith(Config config) {
        updateWith(
                config.getApiKey(),
                config.getApiDomain(),
                config.getAccountCacheTime(),
                config.getSessionVerificationInterval()
        );
        if (config.getGmid() != null) {
            this.gmid = config.getGmid();
        }
        if (config.getUcid() != null) {
            this.ucid = config.getUcid();
        }
        return this;
    }

    //endregion

    //region GETTERS & SETTERS

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    public String getGmid() {
        return gmid;
    }

    public void setGmid(String gmid) {
        this.gmid = gmid;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public int getAccountCacheTime() {
        return accountCacheTime;
    }

    public void setAccountCacheTime(int accountCacheTime) {
        this.accountCacheTime = accountCacheTime;
    }

    public boolean isInterruptionsEnabled() {
        return interruptionsEnabled;
    }

    public void setInterruptionsEnabled(boolean interruptionsEnabled) {
        this.interruptionsEnabled = interruptionsEnabled;
    }

    public int getSessionVerificationInterval() {
        return sessionVerificationInterval;
    }

    public void setSessionVerificationInterval(int sessionVerificationInterval) {
        this.sessionVerificationInterval = sessionVerificationInterval;
    }

    //endregion
}
