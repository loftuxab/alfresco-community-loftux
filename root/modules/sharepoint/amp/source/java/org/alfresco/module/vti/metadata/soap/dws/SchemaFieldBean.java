/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.metadata.soap.dws;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.metadata.soap.SoapUtils;

/**
 * @author AndreyAk
 *
 */
public class SchemaFieldBean implements Serializable
{

    private static final long serialVersionUID = 1387190605579465013L;
    
    private String name;
    private String type;
    private boolean required;
    private List<String> choices;
    
    /**
     * @param name
     * @param type
     * @param required
     * @param choices
     */
    public SchemaFieldBean(String name, String type, boolean required, List<String> choices)
    {
        super();
        this.name = name;
        this.type = type;
        this.required = required;
        this.choices = choices;
    }
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    /**
     * @return the required
     */
    public boolean isRequired()
    {
        return required;
    }
    /**
     * @param required the required to set
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }
    /**
     * @return the choices
     */
    public List<String> getChoices()
    {
        return choices;
    }
    /**
     * @param choices the choices to set
     */
    public void setChoices(List<String> choices)
    {
        this.choices = choices;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("Name", name);
        attributes.put("Type", type);
        attributes.put("Required", required);
        
        result.append(SoapUtils.startTag("Field", attributes));
        if (choices.size() > 0)
        {
            result.append(SoapUtils.startTag("Choices"));
            for (String choice : choices)
            {
                result.append(SoapUtils.proccesTag("Choice", choice));
            }
            result.append(SoapUtils.endTag("Choices"));
        }
        else
        {
            result.append(SoapUtils.singleTag("Choices"));
        }
        result.append(SoapUtils.endTag("Field"));
        
        return result.toString();
    }
}
