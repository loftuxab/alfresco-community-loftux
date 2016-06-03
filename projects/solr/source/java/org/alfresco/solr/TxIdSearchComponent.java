/*
 * #%L
 * Alfresco Solr
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.solr;

import java.io.IOException;

import org.alfresco.solr.tracker.CoreTracker;
import org.alfresco.solr.tracker.Tracker;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;

public class TxIdSearchComponent extends SearchComponent
{
    @Override
    public void prepare(ResponseBuilder rb) throws IOException
    {
        // No preparation required.
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException
    {
        SolrQueryRequest req = rb.req;
        AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler)
                    req.getCore().
                    getCoreDescriptor().
                    getCoreContainer().
                    getMultiCoreHandler();
        InformationServer infoSrv = adminHandler.getInformationServers().get(req.getCore().getName());
        if(infoSrv != null)
        {
            Long lastIndexedTx = infoSrv.getTrackerState().getLastIndexedTxId();
            rb.rsp.add("lastIndexedTx", lastIndexedTx);
        }
    }

    @Override
    public String getDescription()
    {
        return "Adds the last indexed transaction ID to the search results.";
    }

    @Override
    public String getSourceId()
    {
        return "";
    }

    @Override
    public String getSource()
    {
        return "http://www.alfresco.com";
    }

    @Override
    public String getVersion()
    {
        return "1.0";
    }

}
