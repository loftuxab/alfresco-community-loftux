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
package org.alfresco.web.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents an instruction (within the forms config) to show or hide a
 * field. The instruction may only apply in certain modes. Also a show instruction
 * (but not a hide instruction) can be "forced", which means that the field should
 * always be returned by the form service, even if the relevant property/association
 * is not currently present on the node (in the case of form items that are NodeRefs).
 * 
 * @see Mode
 * 
 * @author Neil McErlean
 */
class FieldVisibilityInstruction
{
    private final Visibility showOrHide;
    private final String fieldId;
    private final List<Mode> forModes;
    private final boolean force;
    
    /**
     * 
     * @param showOrHide
     * @param fieldId
     * @param modesString
     * @param forceString indicates that the field should always be returned
     * by the form service even if it is not currently present on the form item (node).
     * A value of forceString.equalsIgnoreCase("true") for this parameter only makes
     * sense for and will only be applied for show instructions.
     */
    public FieldVisibilityInstruction(String showOrHide, String fieldId, String modesString, String forceString)
    {
        this.showOrHide = Visibility.visibilityFromString(showOrHide);
        this.fieldId = fieldId;
        if (modesString == null || modesString.length() == 0)
        {
            this.forModes = Arrays.asList(Mode.values());
        }
        else
        {
            this.forModes = Mode.modesFromString(modesString);
        }
        
        boolean isForced = forceString != null && forceString.equalsIgnoreCase("true");
        // A force value of 'true' will only be applied for 'show' not 'hide' instructions.
    	boolean forceShow = isForced && this.showOrHide.equals(Visibility.SHOW);
        this.force = forceShow;
    }

    public Visibility getShowOrHide()
    {
        return showOrHide;
    }
    
    public String getFieldId()
    {
        return fieldId;
    }
    
    public List<Mode> getModes()
    {
        return Collections.unmodifiableList(forModes);
    }

    /**
     * This method returns true if the field should be 'forced' to be shown.
     * @return
     */
    public boolean isForced()
    {
    	return this.force;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(this.showOrHide)
            .append(" ")
            .append(fieldId)
            .append(" ")
            .append(forModes)
            .append(" force=")
            .append(force);
        return result.toString();
    }
}