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
 * Represents  information about a Document Workspace site and the lists it contains
 * 
 * @author AndreyAk
 *
 */
public class DwsData implements Serializable
{

    private static final long serialVersionUID = 7388705900532472455L;   

    private String title;
    
    private String lastUpdate;
    
    private UserBean user;
    
    private List<MemberBean> members;
    
    private List<AssigneeBean> assignees;
    
    private List<TaskBean> tasksList;
    
    private List<DocumentBean> documentsList;
    
    private List<LinkBean> linksList;
    
    private boolean minimal;



    /**
     * @param title
     * @param lastUpdate
     * @param user
     * @param members
     * @param assignees
     * @param tasksList
     * @param documentsList
     * @param linksList
     */
    public DwsData(String title, String lastUpdate, UserBean user, List<MemberBean> members, List<AssigneeBean> assignees, List<TaskBean> tasksList,
            List<DocumentBean> documentsList, List<LinkBean> linksList, boolean minimal)
    {
        super();
        this.title = title;
        this.lastUpdate = lastUpdate;
        this.user = user;
        this.members = members;
        this.assignees = assignees;
        this.tasksList = tasksList;
        this.documentsList = documentsList;
        this.linksList = linksList;
        this.minimal = minimal;
    }

    /**
     * Default constructor
     */
    public DwsData()
    {
        
    }
    
    public List<AssigneeBean> getAssignees()
    {
        return assignees;
    }

    public void setAssignees(List<AssigneeBean> assignees)
    {
        this.assignees = assignees;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public UserBean getUser()
    {
        return user;
    }

    public void setUser(UserBean user)
    {
        this.user = user;
    }

    public List<MemberBean> getMembers()
    {
        return members;
    }

    public void setMembers(List<MemberBean> members)
    {
        this.members = members;
    }

        
    /**
     * @return the tasksList
     */
    public List<TaskBean> getTasksList()
    {
        return tasksList;
    }

    /**
     * @param tasksList the tasksList to set
     */
    public void setTasksList(List<TaskBean> tasksList)
    {
        this.tasksList = tasksList;
    }

    /**
     * @return the documentsList
     */
    public List<DocumentBean> getDocumentsList()
    {
        return documentsList;
    }

    /**
     * @param documentsList the documentsList to set
     */
    public void setDocumentsList(List<DocumentBean> documentsList)
    {
        this.documentsList = documentsList;
    }

    /**
     * @return the linksList
     */
    public List<LinkBean> getLinksList()
    {
        return linksList;
    }

    /**
     * @param linksList the linksList to set
     */
    public void setLinksList(List<LinkBean> linksList)
    {
        this.linksList = linksList;
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        result.append(SoapUtils.startTag("Results"))
              .append(SoapUtils.proccesTag("Title", title))
              .append(SoapUtils.proccesTag("LastUpdate", lastUpdate))
              .append(user);
        
        result.append(SoapUtils.startTag("Members"));
        if (members != null)
        {
            for (MemberBean member : members)
            {
                result.append(member);
            }
        }
        result.append(SoapUtils.endTag("Members"));
        
        if(!minimal)
        {
            result.append(SoapUtils.startTag("Assignees"));
            if (assignees != null)
            {
                
                for (AssigneeBean assignee : assignees)
                {
                    result.append(assignee);
                }
            }
            result.append(SoapUtils.endTag("Assignees"));
                     
            
            Map<String, Object> docAttr = new HashMap<String, Object>();
            docAttr.put("Name", "Documents");
            result.append(SoapUtils.startTag("List", docAttr));
            result.append(SoapUtils.proccesTag("ID", ""));
            if (documentsList != null)
            {
                for (DocumentBean document : documentsList)
                {
                    result.append(document);
                }
            }
            result.append(SoapUtils.endTag("List"));            
            
        }
        
        result.append(SoapUtils.endTag("Results"));
        return result.toString();
    }

    /**
     * @return the minimal
     */
    public boolean isMinimal()
    {
        return minimal;
    }

    /**
     * @param minimal the minimal to set
     */
    public void setMinimal(boolean minimal)
    {
        this.minimal = minimal;
    }
}
