package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.repo.search.impl.querymodel.Constraint;

public abstract class BaseConstraint implements Constraint
{

    private Occur occur = Occur.DEFAULT;
    
    private float boost = 1.0f;
    
    public BaseConstraint()
    {
      
    }

    public Occur getOccur()
    {
      return occur;
    }
    
    public void setOccur(Occur occur)
    {
        this.occur = occur;
    }

    public float getBoost()
    {
        return boost;
    }

    public void setBoost(float boost)
    {
        this.boost = boost;
    }

}
