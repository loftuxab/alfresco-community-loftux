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
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.module.vti.metadata.dialog;

/**
 * <p>Custom comparator to compare DialogMetaInfo beans.</p>
 * 
 * @author PavelYur
 */
import java.util.Comparator;

import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;

public class DialogMetaInfoComparator implements Comparator<DialogMetaInfo>
{
    //Default values for comparison
    private VtiSortField sortField = VtiSortField.TYPE;
    private VtiSort sort = VtiSort.ASC;
    
    /**
     * Constructor
     * 
     * @param sortField field that is used as a key in sorting
     * @param sort sorting type (ascending or descending)
     */
    public DialogMetaInfoComparator(VtiSortField sortField, VtiSort sort)
    {
        this.sortField = sortField;
        this.sort = sort;
    }
    
    /**
     * Sort DialogMetaInfo beans as MS clients do that.
     */
    public int compare(DialogMetaInfo o1, DialogMetaInfo o2)
    {         
        if (o1.isFolder() != o2.isFolder())
        {
            if (o1.isFolder())
            {
                if (sort.equals(VtiSort.ASC))                
                {
                    return -1;
                }
                if (sort.equals(VtiSort.DESC))                
                {
                    return 1;
                }
            }            
            else
            {
                if (sort.equals(VtiSort.ASC))                
                {
                    return 1;
                }
                if (sort.equals(VtiSort.DESC))                
                {
                    return -1;
                }
            }
        }
        else
        {
            if (sort.equals(VtiSort.ASC))
            {
                if (sortField.equals(VtiSortField.TYPE))
                {                    
                    int extIndex1 = o1.getName().lastIndexOf('.');
                    int extIndex2 = o2.getName().lastIndexOf('.');
                    String ext1, ext2;
                    if (extIndex1 != -1 && o1.getName().length() > extIndex1 + 1)
                    {
                        ext1 = o1.getName().substring(extIndex1 + 1);
                    }
                    else
                    {
                        ext1 = "";
                    }
                    if (extIndex2 != -1 && o2.getName().length() > extIndex2 + 1)
                    {
                        ext2 = o2.getName().substring(extIndex2 + 1);
                    }
                    else
                    {
                        ext2 = "";
                    }                    
                    return ext1.compareToIgnoreCase(ext2);                    
                }                
                if (sortField.equals(VtiSortField.NAME))
                {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
                if (sortField.equals(VtiSortField.MODIFIEDBY))
                {
                    return o1.getModifiedBy().compareToIgnoreCase(o2.getModifiedBy());
                }
                if (sortField.equals(VtiSortField.MODIFIED))
                {
                    return o1.getModifiedTime().compareToIgnoreCase(o2.getModifiedTime());
                }
                if (sortField.equals(VtiSortField.CHECKEDOUTTO))
                {
                    return o1.getCheckedOutTo().compareToIgnoreCase(o2.getCheckedOutTo());
                }
            }
            if (sort.equals(VtiSort.DESC))
            {
                if (sortField.equals(VtiSortField.TYPE))
                {                    
                    int extIndex1 = o1.getName().lastIndexOf('.');
                    int extIndex2 = o2.getName().lastIndexOf('.');
                    String ext1, ext2;
                    if (extIndex1 != -1 && o1.getName().length() > extIndex1 + 1)
                    {
                        ext1 = o1.getName().substring(extIndex1 + 1);
                    }
                    else
                    {
                        ext1 = "";
                    }
                    if (extIndex2 != -1 && o2.getName().length() > extIndex2 + 1)
                    {
                        ext2 = o2.getName().substring(extIndex2 + 1);
                    }
                    else
                    {
                        ext2 = "";
                    }                    
                    return -ext1.compareToIgnoreCase(ext2);                    
                }                
                if (sortField.equals(VtiSortField.NAME))
                {
                    return -o1.getName().compareToIgnoreCase(o2.getName());
                }
                if (sortField.equals(VtiSortField.MODIFIEDBY))
                {
                    return -o1.getModifiedBy().compareToIgnoreCase(o2.getModifiedBy());
                }
                if (sortField.equals(VtiSortField.MODIFIED))
                {
                    return -o1.getModifiedTime().compareToIgnoreCase(o2.getModifiedTime());
                }
                if (sortField.equals(VtiSortField.CHECKEDOUTTO))
                {
                    return -o1.getCheckedOutTo().compareToIgnoreCase(o2.getCheckedOutTo());
                }                
            }            
        }          
        return 0;
    }
}
