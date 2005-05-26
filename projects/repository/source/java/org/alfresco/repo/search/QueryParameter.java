/*
 * Created on Mar 24, 2005
 */
package org.alfresco.repo.search;

import java.io.Serializable;

import org.alfresco.repo.ref.QName;

/**
 * Encapsulates a query parameter
 * 
 * @author andyh
 * 
 */
public class QueryParameter
{
    private QName qName;

    private Serializable value;

    public QueryParameter(QName qName, Serializable value)
    {
        this.qName = qName;
        this.value = value;
    }

    public QName getQName()
    {
        return qName;
    }
    

    public Serializable getValue()
    {
        return value;
    }
    
    
    
    
}
