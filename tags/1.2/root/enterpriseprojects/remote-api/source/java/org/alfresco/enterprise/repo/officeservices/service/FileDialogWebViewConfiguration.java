package org.alfresco.enterprise.repo.officeservices.service;

public interface FileDialogWebViewConfiguration
{

    boolean appliesTo(String path);
    
    String getFreeMarkerTemplateLocation();
    
}
