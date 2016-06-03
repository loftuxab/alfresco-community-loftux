package org.alfresco.share.util;

/**
 * This enums used to describe Activity types in Site Dashboard page Activities dashlet.
 * 
 * @author cbairaajoni
 */
public enum ActivityType
{
    USER("User"),
    DOCUMENT("Document"),
    SITE("Site"),
    DESCRIPTION("Description");

    private String action;

    private ActivityType(String action)
    {
        this.action = action;
    }

    public String getAction()
    {
        return action;
    }

}