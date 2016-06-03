package org.alfresco.repo.search.impl.querymodel;

/**
 * A constraint
 * 
 * @author andyh
 *
 */
public interface Constraint
{
    public enum Occur
    {
        DEFAULT,
        MANDATORY,
        OPTIONAL,
        EXCLUDE
    }
    
    public boolean evaluate();
    
    public Occur getOccur();
    
    public void setOccur(Occur occur);
    
    public float getBoost();
    
    public void setBoost(float boost);
}
