package com.ericsson.itte.slack.gerrit;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.auth.Credentials;

import com.sonymobile.tools.gerrit.gerritevents.GerritConnectionConfig2;
import com.sonymobile.tools.gerrit.gerritevents.ssh.Authentication;
import com.sonymobile.tools.gerrit.gerritevents.watchdog.WatchTimeExceptionData;
import com.sonymobile.tools.gerrit.gerritevents.watchdog.WatchTimeExceptionData.TimeSpan;

public class SlackGerritConnectionConfig implements GerritConnectionConfig2 {

    private File authKeyFile;
    private String authKeyFilePassword;
    private String hostName;
    private String userName;
    private String email;
    private String frontEndUrl;
    private int watchDogTimeoutMinutes;

    @Override
    public File getGerritAuthKeyFile() {
        return authKeyFile;
    }

    @Override
    public String getGerritAuthKeyFilePassword() {
        return authKeyFilePassword;
    }

    @Override
    public String getGerritHostName() {
        return hostName;
    }

    @Override
    public int getGerritSshPort() {
        return 29418;
    }

    @Override
    public String getGerritUserName() {
        return userName;
    }

    @Override
    public Authentication getGerritAuthentication() {
        return new Authentication(authKeyFile, userName, getGerritAuthKeyFilePassword());
    }

    @Override
    public String getGerritProxy() {
        return null;
    }

    @Override
    public String getGerritEMail() {
        return email;
    }

    @Override
    public String getGerritFrontEndUrl() {
        return frontEndUrl;
    }

    public void setAuthKeyFile(File authKeyFile) {
        this.authKeyFile = authKeyFile;
    }

    public void setAuthKeyFilePassword(String authKeyFilePassword) {
        this.authKeyFilePassword = authKeyFilePassword;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFrontEndUrl(String frontEndUrl) {
        this.frontEndUrl = frontEndUrl;
    }

    @Override
    public Credentials getHttpCredentials() {
        return null;
    }

    @Override
    public int getWatchdogTimeoutMinutes() {
        return watchDogTimeoutMinutes;
    }

    @Override
    public int getWatchdogTimeoutSeconds() {
        return (int) TimeUnit.MINUTES.toSeconds(watchDogTimeoutMinutes);
    }

    public void setWatchDogTimeoutMinutes(int watchDogTimeoutMinutes) {
        this.watchDogTimeoutMinutes = watchDogTimeoutMinutes;
    }

    @Override
    public WatchTimeExceptionData getExceptionData() {
        int[] daysAsInt = new int[]{};
        List<TimeSpan> exceptionTimes = new LinkedList<TimeSpan>();
        return new WatchTimeExceptionData(daysAsInt, exceptionTimes);
    }

}
