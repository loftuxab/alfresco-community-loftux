/*
 * Created on 06-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.query;

public abstract class AbstractStructuredFieldPosition implements StructuredFieldPosition
{
    private String termText;

    private boolean isTerminal;

    private boolean isAbsolute;

    private CachingTermPositions tps;

    public AbstractStructuredFieldPosition(String termText, boolean isTerminal, boolean isAbsolute)
    {
        super();
        this.termText = termText;
        this.isTerminal = isTerminal;
        this.isAbsolute = isAbsolute;
    }

    public boolean isTerminal()
    {
        return isTerminal;
    }

    protected void setTerminal(boolean isTerminal)
    {
        this.isTerminal = isTerminal;
    }

    public boolean isAbsolute()
    {
        return isAbsolute;
    }

    public boolean isRelative()
    {
        return !isAbsolute;
    }

    public String getTermText()
    {
        return termText;
    }

    public int getPosition()
    {
        return -1;
    }

    public void setCachingTermPositions(CachingTermPositions tps)
    {
        this.tps = tps;
    }

    public CachingTermPositions getCachingTermPositions()
    {
        return this.tps;
    }

    
    
    public boolean allowsLinkingBySelf()
    {
       return false;
    }

    public boolean allowslinkingByParent()
    {
        return true;
    }

    public boolean linkParent()
    {
        return true;
    }

    public boolean linkSelf()
    {
       return false;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(256);
        buffer.append(getDescription());
        buffer.append("<"+getTermText()+"> at "+getPosition());
        buffer.append(" Terminal = "+isTerminal());
        buffer.append(" Absolute = "+isAbsolute());
        return buffer.toString();
    }
    
    public abstract String getDescription();

    public boolean isDescendant()
    {
        return false;
    }

    
}
