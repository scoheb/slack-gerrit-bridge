package com.ericsson.itte.slack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.itte.slack.gerrit.SlackGerritConnectionConfig;
import com.ericsson.itte.slack.gerrit.SlackGerritConnectionListener;
import com.ericsson.itte.slack.gerrit.SlackGerritEventListener;
import com.sonymobile.tools.gerrit.gerritevents.GerritConnection;
import com.sonymobile.tools.gerrit.gerritevents.GerritEventListener;
import com.sonymobile.tools.gerrit.gerritevents.GerritHandler;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

/**
 * Hello world!
 *
 */
public class App
{
    private static final String DEFAULT_PROPERTIES_FILE = "slack-gerrit-bridge.properties";
    private static final String SLACK_TOKENID_PROPERTY = "SLACK_TOKENID";
    private static final String SLACK_CHANNEL_NAME_PROPERTY = "SLACK_CHANNEL_NAME";
    private static final String GERRIT_SSH_KEY_PATH_PROPERTY = "GERRIT_SSH_KEY_PATH";
    private static final String GERRIT_HOSTNAME_PROPERTY = "GERRIT_HOSTNAME";
    private static final String GERRIT_USERNAME_PROPERTY = "GERRIT_USERNAME";
    private static final String GERRIT_FRONTENDURL_PROPERTY = "GERRIT_FRONTENDURL";
    private static final String GERRIT_PROJECTSTOWATCH_PROPERTY = "GERRIT_PROJECTSTOWATCH";

    private static Logger logger = LoggerFactory.getLogger(App.class);
    
    public static void main(String[] args) throws IOException
    {

        File propertiesFile = null;
        if (args.length == 0) {
            propertiesFile = new File(System.getProperty("user.dir") + "/" + DEFAULT_PROPERTIES_FILE);
        } else {
            propertiesFile = new File(args[0]);
        }
        logger.info("Loading " + propertiesFile.getAbsolutePath());
        XProperties parameters = new XProperties();
        parameters.load(new FileReader(propertiesFile));

        //get token and channel name
        String tokenID = getProperty(parameters, SLACK_TOKENID_PROPERTY);
        String channelName = getProperty(parameters, SLACK_CHANNEL_NAME_PROPERTY);

        SlackSession session = SlackSessionFactory.
                createWebSocketSlackSession(tokenID);
        session.connect();
        Collection<SlackChannel> channels = session.getChannels();
        SlackChannel generalChannel = null;
        for (SlackChannel c : channels) {
            if (c.getName().equals(channelName)) {
                generalChannel = c;
                break;
            }
        }
        if (generalChannel == null) {
            throw new RuntimeException("Could not find channel " + channelName);
        }

        createAndStartGerritConnection(parameters, generalChannel, session);

    }

    private static String getProperty(XProperties parameters, String propertyName) {
        String value = parameters.getProperty(propertyName);
        if (value == null) {
            throw new RuntimeException("Property " + propertyName + " not defined in " + DEFAULT_PROPERTIES_FILE);
        }
        return value;
    }

    private static void createAndStartGerritConnection(XProperties parameters, SlackChannel generalChannel,
            SlackSession session) {
        SlackGerritConnectionListener gerritConnectionListener = new SlackGerritConnectionListener();

        String gerritSshKeyPath = getProperty(parameters, GERRIT_SSH_KEY_PATH_PROPERTY);
        String gerritHostname = getProperty(parameters, GERRIT_HOSTNAME_PROPERTY);
        String gerritUsername = getProperty(parameters, GERRIT_USERNAME_PROPERTY);
        String gerritFrontendUrl = getProperty(parameters, GERRIT_FRONTENDURL_PROPERTY);
        String gerritProjectsToWatch = getProperty(parameters, GERRIT_PROJECTSTOWATCH_PROPERTY);
        String[] projectsToWatch = gerritProjectsToWatch.split(",");

        String name = "defaultServer";
        SlackGerritConnectionConfig config = new SlackGerritConnectionConfig();
        config.setAuthKeyFile(new File(gerritSshKeyPath));
        config.setHostName(gerritHostname);
        config.setUserName(gerritUsername);
        config.setFrontEndUrl(gerritFrontendUrl);
        config.setWatchDogTimeoutMinutes(1);

        GerritConnection connection = new GerritConnection(name, config);
        GerritHandler handler = new GerritHandler();

        handler.setIgnoreEMail(name, config.getGerritEMail());
        GerritEventListener listener = new SlackGerritEventListener(generalChannel, session, projectsToWatch);
        handler.addListener(listener);
        connection.setHandler(handler);
        connection.addListener(gerritConnectionListener);
        connection.start();
    }
}
