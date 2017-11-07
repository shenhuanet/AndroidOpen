package com.shenhua.libs.firupdater.bean;

/**
 * Created by Shenhua on 11/30/2016.
 * e-mail shenhuanet@126.com
 */
public class FirAppInfo {

    private String appName;
    private int appVersion;// 对应VersionCode
    private String appChangeLog;
    private String appTime;
    private String appVersionShort;// 对应VersionName
    private String appInstallUrl;
    private String appDirectInstallUrl;
    private String appSize;

    public FirAppInfo() {
    }

    public String getAppInstallUrl() {
        return appInstallUrl;
    }

    public void setAppInstallUrl(String appInstallUrl) {
        this.appInstallUrl = appInstallUrl;
    }

    public String getAppChangeLog() {
        return appChangeLog;
    }

    public void setAppChangeLog(String appChangeLog) {
        this.appChangeLog = appChangeLog;
    }

    public String getAppDirectInstallUrl() {
        return appDirectInstallUrl;
    }

    public void setAppDirectInstallUrl(String appDirectInstallUrl) {
        this.appDirectInstallUrl = appDirectInstallUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppTime() {
        return appTime;
    }

    public void setAppTime(String appTime) {
        this.appTime = appTime;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersionShort() {
        return appVersionShort;
    }

    public void setAppVersionShort(String appVersionShort) {
        this.appVersionShort = appVersionShort;
    }
}
