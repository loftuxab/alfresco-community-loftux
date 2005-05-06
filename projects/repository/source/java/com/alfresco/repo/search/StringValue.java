/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

/**
 * Dummy implementation for now
 * 
 * @author andyh
 * 
 */
public class StringValue implements Value
{
    private String value;

    public StringValue(String value)
    {
        this.value = value;
    }

    public String getString()
    {
        return value;
    }

    public String toString()
    {
        return getString();
    }
}
