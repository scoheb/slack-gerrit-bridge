package com.ericsson.itte.slack.gerrit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonymobile.tools.gerrit.gerritevents.ConnectionListener;

public class SlackGerritConnectionListener implements ConnectionListener {

    private Logger logger = LoggerFactory.getLogger(SlackGerritConnectionListener.class);

    @Override
    public void connectionEstablished() {
        logger.info("gerrit connection up");
    }

    @Override
    public void connectionDown() {
        logger.info("gerrit connection down");
    }

}
