package com.activiti.repo.dictionary.metamodel;

import java.util.List;



public interface M2Type extends M2Class
{
    
    public List getDefaultAspects();

    public boolean getOrderedChildren();
    
    public void setOrderedChildren(boolean areChildrenOrdered);
       
}
