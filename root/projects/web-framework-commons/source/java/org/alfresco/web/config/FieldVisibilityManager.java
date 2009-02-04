/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.web.config.FormConfigElement.Mode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is responsible for implementing the algorithm that defines field
 * visibility. This is governed by the particular formulation of &lt;show&gt; and
 * &lt;hide&gt; tags in the field-visibility tag.
 * <P/>
 * The algorithm for determining visibility works as follows
 * <ul>
 *   <li>If there is no field-visibility configuration (show or hide tags) then
 *       all fields are visible in all modes.</li>
 *   <li>If there are one or more hide tags then the specified fields will be hidden
 *       in the specified modes. All other fields remain visible as before.</li>
 *   <li>However, as soon as a single show tag appears in the config xml, this is
 *       taken as a signal that all field visibility is to be manually configured.
 *       At that point, all fields default to hidden and only those explicitly
 *       configured to be shown (with a show tag) will actually be shown.</li>
 *   <li>Show and hide rules will be applied in sequence with later rules
 *       invalidating previous rules.</li>
 *   <li>Show or hide rules which only apply for specified modes have an implicit
 *       element. e.g. <show id="name" for-mode="view"/> would show the name field
 *       in view mode and by implication, hide it in other modes.</li>
 * </ul>
 */
class FieldVisibilityManager
{
    private static Log logger = LogFactory.getLog(FieldVisibilityManager.class);
    
    public enum Instruction
    {
        SHOW, HIDE;
        public static Instruction instructionFromString(String modeString)
        {
            if (modeString.equalsIgnoreCase("show")) {
                return Instruction.SHOW;
            }
            else if (modeString.equalsIgnoreCase("hide"))
            {
                return Instruction.HIDE;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * This Map of Sets - one for each of the Modes - manages the visibility status
     * for each field, as specified in the config.xml.
     * Following the algorithm described above, the list is initially empty. Then as 'hide'
     * elements are added to the xml, the fieldIDs are added to the appropriate Set
     * within this Map.
     * As soon as the first 'show' element appears, the Sets are all cleared and are then
     * reused to manage the fieldIds of those fields that should be shown.
     * In other words the meaning of this Map is inverted by the addition of the first
     * 'show' instruction.
     */
    private Map<Mode, Set<String>> instructions = new HashMap<Mode, Set<String>>();
    private boolean managingHiddenFields = true;
    
    public FieldVisibilityManager()
    {
        // Initialise the data sets.
        instructions.put(Mode.CREATE, new LinkedHashSet<String>());
        instructions.put(Mode.EDIT, new LinkedHashSet<String>());
        instructions.put(Mode.VIEW, new LinkedHashSet<String>());
    }
    
    void addInstruction(String showOrHide, String fieldId, String modesString)
    {
        boolean show = showOrHide.equals("show") ? true : false;
        List<Mode> modes = Mode.modesFromString(modesString);
        if (modes.isEmpty())
        {
            // If there is no modesString, this means 'all modes'
            modes = Arrays.asList(Mode.values());
        }
        for (Mode m : modes)
        {
            if (show)
            {
                if (managingHiddenFields)
                {
                    // As soon as the first 'show' instruction is encountered, everything
                    // becomes hidden by default and we need only track what is to be shown.
                    // So we will clear all the previous instructions-to-hide and will
                    // now track instructions-to-show.
                    this.managingHiddenFields = false;
                    for (Mode m2 : Arrays.asList(Mode.values()))
                    {
                        instructions.get(m2).clear();
                    }
                }
                instructions.get(m).add(fieldId);
            }
            else
            {
                // It is an instruction-to-hide, which only has meaning if there was a
                // previous instruction-to-show.
                if (managingHiddenFields)
                {
                    instructions.get(m).add(fieldId);
                }
                else
                {
                    instructions.get(m).remove(fieldId);
                }
            }
        }
    }
    
    public FieldVisibilityManager combine(FieldVisibilityManager otherFVM)
    {
        if (otherFVM == this)
        {
            return this;
        }
        FieldVisibilityManager result = new FieldVisibilityManager();
        
        // Copy in this sets of strings.
        for (Mode m : this.instructions.keySet())
        {
            Set<String> fieldIdsFromThis = this.instructions.get(m);
            String showOrHide = managingHiddenFields ? "hide" : "show";
            for (String f : fieldIdsFromThis)
            {
                result.addInstruction(showOrHide, f, m.toString());
            }
        }
        
        // merge in the second set.
        for (Mode m : otherFVM.instructions.keySet())
        {
            Set<String> fieldIdsFromThat = otherFVM.instructions.get(m);
            String showOrHide = otherFVM.managingHiddenFields ? "hide" : "show";
            for (String f : fieldIdsFromThat)
            {
                result.addInstruction(showOrHide, f, m.toString());
            }
        }

        return result;
    }
    
    /**
     * This method checks whether the specified field is visible in the specified mode.
     * @param fieldId
     * @param m
     * @return
     */
    public boolean isFieldVisible(String fieldId, Mode m)
    {
        Set<String> instructionsForSpecifiedMode = instructions.get(m);
        
        if (this.isManagingHiddenFields() )
        {
            return !instructionsForSpecifiedMode.contains(fieldId);
        }
        else
        {
            return instructionsForSpecifiedMode.contains(fieldId);
        }
    }

    /**
     * Returns true if managing hidden fields, false if managing shown fields.
     * @return
     */
    public boolean isManagingHiddenFields()
    {
        return this.managingHiddenFields;
    }

    /**
     * This method attempts to return a List of the fieldIDs of the fields which are
     * visible in the specified Mode. Such a request only makes sense if this
     * class is managing 'shown' fields, in other words, if there has been at least
     * one show tag.
     * @param mode the Mode.
     * @return the list of fields visible in the specified mode if this is knowable,
     * else null.
     */
    public List<String> getFieldNamesVisibleInMode(Mode mode)
    {
        if (managingHiddenFields)
        {
            // Visible fields for any mode are not knowable
            return null;
        }
        else
        {
            List<String> result = new ArrayList<String>();
            for (Iterator<String> iter = instructions.get(mode).iterator(); iter.hasNext(); )
            {
                result.add(iter.next());
            }
            return result;
        }
    }
}