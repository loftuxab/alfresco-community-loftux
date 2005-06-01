package org.alfresco.repo.dictionary;

import java.util.Date;

import org.alfresco.repo.ref.QName;


public interface ModelDefinition
{

    public QName getName();
    
    public String getDescription();
    
    public String getAuthor();
    
    public Date getPublishedDate();
    
    public String getVersion();

}
