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
package org.alfresco.ibatis;

import java.util.List;

/**
 * Entity bean to carry ID-style information
 *
 * @author Derek Hulley
 * @since 3.2
 */
public class IdsEntity
{
    private Long idOne;
    private Long idTwo;
    private Long idThree;
    private List<Long> ids;
    public Long getIdOne()
    {
        return idOne;
    }
    public void setIdOne(Long id)
    {
        this.idOne = id;
    }
    public Long getIdTwo()
    {
        return idTwo;
    }
    public void setIdTwo(Long id)
    {
        this.idTwo = id;
    }
    public Long getIdThree()
    {
        return idThree;
    }
    public void setIdThree(Long idThree)
    {
        this.idThree = idThree;
    }
    public List<Long> getIds()
    {
        return ids;
    }
    public void setIds(List<Long> ids)
    {
        this.ids = ids;
    }
}
