package com.ericsson.itte.slack.gerrit;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonymobile.tools.gerrit.gerritevents.GerritEventListener;
import com.sonymobile.tools.gerrit.gerritevents.dto.GerritEvent;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.PatchsetCreated;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackSession;

public class SlackGerritEventListener implements GerritEventListener {

    private SlackSession session;
    private SlackChannel channel;
    private String[] projects;
    private Logger logger = LoggerFactory.getLogger(SlackGerritEventListener.class);

    public SlackGerritEventListener(SlackChannel channel, SlackSession session, String[] projects) {
        this.channel = channel;
        this.session = session;
        this.projects = projects;
    }

    @Override
    public void gerritEvent(GerritEvent event) {
        logger.debug("receivedEvent: " + event);
    }

    public void gerritEvent(PatchsetCreated event) {
        String project = event.getModifiedProject();
        if (!watchedProject(project)) {
            logger.debug("project: " + project + " not interesting");
            return;
        }
        String url = event.getProvider().getUrl() + "r/" + event.getChange().getNumber();
        logger.info("received PatchsetCreated Event: " + event);
        String message = "> *New Patchset for Review*";
        SlackGerritChangeFormatter formatter = new SlackGerritChangeFormatter(session);
        SlackAttachment attachment = formatter.format(event);
        SlackMessageHandle response = session.sendMessage(channel, message, attachment);
    }

    private boolean watchedProject(String project) {
        ArrayIterator it = new ArrayIterator(projects);
        while (it.hasNext()) {
            String p = ((String) it.next()).trim();
            if (p.equals(project)) {
                return true;
            }
        }
        return false;
    }

}
