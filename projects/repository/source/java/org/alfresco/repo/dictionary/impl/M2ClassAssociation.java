package org.alfresco.repo.dictionary.impl;


public abstract class M2ClassAssociation
{
    private String name = null;
    private boolean isProtected = false;
    private String title = null;
    private String description = null;
    private String sourceRoleName = null;
    private boolean isSourceMandatory = true;
    private boolean isSourceMany = false;
    private String targetClassName = null;
    private String targetRoleName = null;
    private boolean isTargetMandatory = false;
    private boolean isTargetMany = true;
    
    
    /*package*/ M2ClassAssociation()
    {
    }
    
    /*package*/ M2ClassAssociation(String name)
    {
        this.name = name;
    }
    
    
    public boolean isChild()
    {
        return this instanceof M2ChildAssociation;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isProtected()
    {
        return isProtected;
    }
    
    public void setProtected(boolean isProtected)
    {
        this.isProtected = isProtected;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getSourceRoleName()
    {
        return sourceRoleName;
    }
    
    public void setSourceRoleName(String name)
    {
        this.sourceRoleName = name;
    }

    public boolean isSourceMandatory()
    {
        return isSourceMandatory;
    }
    
    public void setSourceMandatory(boolean isSourceMandatory)
    {
        this.isSourceMandatory = isSourceMandatory;
    }
    
    public boolean isSourceMany()
    {
        return isSourceMany;
    }
    
    public void setSourceMany(boolean isSourceMany)
    {
        this.isSourceMany = isSourceMany;
    }
    
    public String getTargetClassName()
    {
        return targetClassName;
    }
    
    public void setTargetClassName(String targetClassName)
    {
        this.targetClassName = targetClassName;
    }

    public String getTargetRoleName()
    {
        return targetRoleName; 
    }
    
    public void setTargetRoleName(String name)
    {
        this.targetRoleName = name;
    }

    public boolean isTargetMandatory()
    {
        return isTargetMandatory;
    }
    
    public void setTargetMandatory(boolean isTargetMandatory)
    {
        this.isTargetMandatory = isTargetMandatory;
    }
    
    public boolean isTargetMany()
    {
        return isTargetMany;
    }
    
    public void setTargetMany(boolean isTargetMany)
    {
        this.isTargetMany = isTargetMany;
    }
    
}
