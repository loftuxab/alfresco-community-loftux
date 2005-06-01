/*
 * Created on 26-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.dictionary.impl;

import java.util.Date;

import org.alfresco.repo.dictionary.ModelDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;

public class M2ModelDefinition implements ModelDefinition
{

    private QName name;
    private M2Model model;
    
    
    public static ModelDefinition create(M2Model model, NamespacePrefixResolver resolver)
    {
        return new M2ModelDefinition(model, resolver);
    }
    
    
    /*package*/ M2ModelDefinition(M2Model model, NamespacePrefixResolver resolver)
    {
        this.name = QName.createQName(model.getName(), resolver);
        this.model = model;
    }

    
    public QName getName()
    {
        return name;
    }


    public String getDescription()
    {
        return model.getDescription();
    }


    public String getAuthor()
    {
        return model.getAuthor();
    }


    public Date getPublishedDate()
    {
        return model.getPublishedDate();
    }


    public String getVersion()
    {
        return model.getVersion();
    }
    
}
