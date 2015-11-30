package com.ericsson.itte.slack.gerrit;

import java.util.Date;

import com.google.common.html.HtmlEscapers;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.PatchsetCreated;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;

public class SlackGerritChangeFormatter {

    private SlackSession session;

    public SlackGerritChangeFormatter(SlackSession session) {
        this.session = session;
    }

    public SlackAttachment format(PatchsetCreated event) {
        String url = event.getProvider().getUrl();
        SlackAttachment attachment = new SlackAttachment();
        attachment.addMarkdownIn("fields");
        attachment.addMarkdownIn("pretext");
        String subject = HtmlEscapers.htmlEscaper().escape(event.getChange().getSubject());
        String changeId = event.getChange().getNumber();
        attachment.pretext = "*<" + url + changeId + "/" + "|" + subject + " (#" + changeId + ")>* *owner :* " + displayUser(session, event.getChange().getOwner().getEmail(), event.getChange().getOwner().getName());
        attachment.addField(null, formatProject(event) + " " + formatLastUpdatedInfo(event), false);

        return attachment;
    }
    
    private String formatLastUpdatedInfo(PatchsetCreated event) {
        return "*Updated:* _" + formatUpdatedField(event.getPatchSet().getCreatedOn()) + "_";
    }

    private String formatProject(PatchsetCreated event) {
        return "*Project:* " + event.getModifiedProject() + ":" + "`" + event.getChange().getBranch() + "`";    }

    protected String displayUser(SlackUser user)
    {
        String realName = user.getUserName();
        if (realName == null || realName.isEmpty())
        {
            return "<@" + user.getId() + ">";
        }
        return realName + " (<@" + user.getId() + ">)";
    }

    protected String displayUser(SlackSession session, String email, String name)
    {
        SlackUser user = session.findUserByEmail(email);
        if (user == null)
        {
            return name;
        }
        return displayUser(user);
    }
    
    protected String formatUpdatedField(Date updateDate)
    {
        Date now = new Date();
        long nbDaysFromNow = (now.getTime() - updateDate.getTime()) / (1000l * 60 * 60 * 24);
        if (nbDaysFromNow <= 0)
        {
            long nbHoursFromNow = (now.getTime() - updateDate.getTime()) / (1000l * 60 * 60);
            if (nbHoursFromNow <= 0)
            {
                long nbMinutesFromNow = (now.getTime() - updateDate.getTime()) / (1000l * 60);
                if (nbMinutesFromNow <= 0)
                {
                    return "Less than a minute ago";
                }
                else
                {
                    if (nbMinutesFromNow == 1)
                    {
                        return "One minute ago";
                    }
                    return nbMinutesFromNow + " minutes ago";
                }
            }
            else
            {
                if (nbHoursFromNow == 1)
                {
                    return "One hour ago";
                }
                return nbHoursFromNow + " hours ago";
            }
        }
        else
        {
            if (nbDaysFromNow == 1)
            {
                return "One day ago";
            }
            return nbDaysFromNow + " days ago";
        }
    }
    
    
}
