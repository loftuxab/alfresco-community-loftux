package org.alfresco.module.org_alfresco_module_cloud.analytics;

import java.util.Date;

import org.alfresco.events.types.Event;
import org.alfresco.events.types.EventImpl;

/**
 * A bridge between existing AnalyticsEvent class and this event.
 *
 * @author Gethin James
 */
public class CloudAnalyticsEvent extends EventImpl implements Event
{
    private static final long serialVersionUID = 9128697317736243399L;
    public static final String CLOUD_PREFIX = "cloud.";
    private final String tenant; // network/tenant
    private final String attributes;
    
    public CloudAnalyticsEvent(String type, String uid, String tenant, String attributes)
    {
    	// TODO include correct seqNo
        super(-1, type, new Date().getTime(), uid);
        this.tenant = tenant;
        this.attributes = attributes;
    }

    public String getTenant()
    {
        return this.tenant;
    }

    public String getAttributes()
    {
        return this.attributes;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("CloudAnalyticsEvent [id=").append(this.id).append(", type=")
                    .append(this.type).append(", username=").append(this.username)
                    .append(", timestamp=").append(this.timestamp).append(", tenant=")
                    .append(this.tenant).append(", attributes=").append(this.attributes)
                    .append("]");
        return builder.toString();
    }


}
