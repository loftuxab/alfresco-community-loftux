package org.alfresco.repo.dictionary;

import java.util.Date;

import org.alfresco.repo.ref.QName;


/**
 * Read-only definition of a Model.
 * 
 * @author David Caruana
 */
public interface ModelDefinition
{
    /**
     * @return the model name
     */
    public QName getName();
    
    /**
     * @return the model description
     */
    public String getDescription();
    
    /**
     * @return the model author
     */
    public String getAuthor();
    
    /**
     * @return the date when the model was published
     */
    public Date getPublishedDate();
    
    /**
     * @return the model version
     */
    public String getVersion();

}
