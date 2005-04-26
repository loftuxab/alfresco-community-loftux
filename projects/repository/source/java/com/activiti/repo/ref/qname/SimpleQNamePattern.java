package com.activiti.repo.ref.qname;

import org.springframework.util.Assert;

import com.activiti.repo.ref.QName;

/**
 * Provides a basic implementation of the qname pattern matcher that
 * performs a direct comparison between {@link com.activiti.repo.ref.QName qnames}.
 * 
 * @author Derek Hulley
 */
public class SimpleQNamePattern implements QNamePattern
{
    private QName qname;

    /**
     * @see QName#createQName(String)
     */
    public SimpleQNamePattern(QName qname)
    {
        this.qname = qname;
    }
    
    /**
     * @see QName#createQName(String, String)
     */
    public SimpleQNamePattern(String namespaceUri, String localName)
    {
        this.qname = QName.createQName(namespaceUri, localName);
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append("SimpleQNamePattern[")
          .append(" qname=").append(qname)
          .append(" ]");
        return sb.toString();
    }

    /**
     * Performs a direct comparison between qnames.
     * 
     * @see QName#equals(Object)
     */
    public boolean isMatch(QName qname)
    {
        Assert.notNull(qname);
        return this.qname.equals(qname);
    }

    /**
     * @return Returns the qname that is used when checking for a match
     */
    public QName getQname()
    {
        return qname;
    }
}
