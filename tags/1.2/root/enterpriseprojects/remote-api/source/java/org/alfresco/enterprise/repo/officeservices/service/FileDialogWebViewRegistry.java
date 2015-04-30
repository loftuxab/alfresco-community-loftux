/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.util.LinkedList;
import java.util.List;

public class FileDialogWebViewRegistry
{

    protected List<FileDialogWebViewConfiguration> configurations = new LinkedList<FileDialogWebViewConfiguration>();

    public FileDialogWebViewConfiguration getConfiguration(String path)
    {
        FileDialogWebViewConfiguration lastHittingConfiguration = null;
        for(FileDialogWebViewConfiguration configuration : configurations)
        {
            if(configuration.appliesTo(path))
            {
                lastHittingConfiguration = configuration;
            }
        }
        return lastHittingConfiguration;
    }

    public void registerConfiguration(FileDialogWebViewConfiguration configuration)
    {
        if(configurations != null)
        {
            configurations.add(configuration);
        }
    }

}
