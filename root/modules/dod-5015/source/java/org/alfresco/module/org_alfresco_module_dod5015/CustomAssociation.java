/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.service.ServiceRegistry;

/**
 * This simple data class is a convenient struct for custom association metadata.
 * 
 * @author Neil McErlean
 */
public final class CustomAssociation
{

    private String description;
    private String name;
    private String sourceRoleName;
    private final String targetClassName = "rma:record";
    private boolean sourceMandatory;
    private boolean sourceMany;
    private boolean targetMandatory;
    private boolean targetMandatoryEnforced;
    private boolean targetMany;
    private String targetRoleName;
    private String title;
    private boolean protected_;

//    // // Child Assoc adds:
//     private String requiredChildName;
//     private Boolean allowDuplicateChildName;
//     private Boolean propagateTimestamps;

    public CustomAssociation(String name)
    {
        this.name = name;
    }

    public static CustomAssociation createInstance(M2ClassAssociation m2Assoc,
            ServiceRegistry serviceRegistry)
    {
        CustomAssociation result = new CustomAssociation(m2Assoc.getName());

        result.setDescription(m2Assoc.getDescription());
        result.setSourceRoleName(m2Assoc.getSourceRoleName());
        result.setSourceMandatory(m2Assoc.isSourceMandatory());
        result.setSourceMany(m2Assoc.isSourceMany());
        result.setTargetMandatory(m2Assoc.isTargetMandatory());
        result.setTargetMandatoryEnforced(m2Assoc.isTargetMandatoryEnforced());
        result.setTargetMany(m2Assoc.isTargetMany());
        result.setTargetRoleName(m2Assoc.getTargetRoleName());
        result.setTitle(m2Assoc.getTitle());
        result.setProtected(m2Assoc.isProtected());
        
        return result;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSourceRoleName()
    {
        return sourceRoleName;
    }

    public void setSourceRoleName(String sourceRoleName)
    {
        this.sourceRoleName = sourceRoleName;
    }

    public String getTargetClassName()
    {
        return targetClassName;
    }

    public boolean isSourceMandatory()
    {
        return sourceMandatory;
    }

    public void setSourceMandatory(boolean sourceMandatory)
    {
        this.sourceMandatory = sourceMandatory;
    }

    public boolean isSourceMany()
    {
        return sourceMany;
    }

    public void setSourceMany(boolean sourceMany)
    {
        this.sourceMany = sourceMany;
    }

    public boolean isTargetMandatory()
    {
        return targetMandatory;
    }

    public void setTargetMandatory(boolean targetMandatory)
    {
        this.targetMandatory = targetMandatory;
    }

    public boolean isTargetMandatoryEnforced()
    {
        return targetMandatoryEnforced;
    }

    public void setTargetMandatoryEnforced(boolean targetMandatoryEnforced)
    {
        this.targetMandatoryEnforced = targetMandatoryEnforced;
    }

    public boolean isTargetMany()
    {
        return targetMany;
    }

    public void setTargetMany(boolean targetMany)
    {
        this.targetMany = targetMany;
    }

    public String getTargetRoleName()
    {
        return targetRoleName;
    }

    public void setTargetRoleName(String targetRoleName)
    {
        this.targetRoleName = targetRoleName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isProtected()
    {
        return protected_;
    }

    public void setProtected(boolean protected_)
    {
        this.protected_ = protected_;
    }
}
