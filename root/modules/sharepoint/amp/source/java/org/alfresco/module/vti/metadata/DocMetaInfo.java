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
package org.alfresco.module.vti.metadata;

import java.util.Map;

import org.alfresco.module.vti.metadata.dic.VtiProperty;



/**
 * @author Michael Shavnev
 *
 */
public class DocMetaInfo
{
    private boolean folder;
    private String path;

    // FOLDER & FILES
    private String thicketdir;
    private String timecreated;
    private String timelastmodified;
    private String timelastwritten;

    // FOLDER
    private String dirlateststamp;
    private String hassubdirs;
    private String isbrowsable;
    private String ischildweb;
    private String isexecutable;
    private String isscriptable;
    private String listbasetype;

    // FILE
    private String title;
    private String filesize;
    private String metatags;
    private String sourcecontrolcheckedoutby;
    private String sourcecontroltimecheckedout;
    private String thicketsupportingfile;
    private String sourcecontrollockexpires;
    private String sourcecontrolcookie;
    private String sourcecontrolversion;

    public DocMetaInfo(boolean folder)
    {
        this.folder = folder;
    }

    public boolean isFolder()
    {
        return folder;
    }

    public String getThicketdir()
    {
        return thicketdir;
    }
    public void setThicketdir(String thicketdir)
    {
        this.thicketdir = thicketdir;
    }
    public String getTimecreated()
    {
        return timecreated;
    }
    public void setTimecreated(String timecreated)
    {
        this.timecreated = timecreated;
    }
    public String getTimelastmodified()
    {
        return timelastmodified;
    }
    public void setTimelastmodified(String timelastmodified)
    {
        this.timelastmodified = timelastmodified;
    }
    public String getTimelastwritten()
    {
        return timelastwritten;
    }
    public void setTimelastwritten(String timelastwritten)
    {
        this.timelastwritten = timelastwritten;
    }
    public String getDirlateststamp()
    {
        return dirlateststamp;
    }
    public void setDirlateststamp(String dirlateststamp)
    {
        this.dirlateststamp = dirlateststamp;
    }
    public String getHassubdirs()
    {
        return hassubdirs;
    }
    public void setHassubdirs(String hassubdirs)
    {
        this.hassubdirs = hassubdirs;
    }
    public String getIsbrowsable()
    {
        return isbrowsable;
    }
    public void setIsbrowsable(String isbrowsable)
    {
        this.isbrowsable = isbrowsable;
    }
    public String getIschildweb()
    {
        return ischildweb;
    }
    public void setIschildweb(String ischildweb)
    {
        this.ischildweb = ischildweb;
    }
    public String getIsexecutable()
    {
        return isexecutable;
    }
    public void setIsexecutable(String isexecutable)
    {
        this.isexecutable = isexecutable;
    }
    public String getIsscriptable()
    {
        return isscriptable;
    }
    public void setIsscriptable(String isscriptable)
    {
        this.isscriptable = isscriptable;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getFilesize()
    {
        return filesize;
    }
    public void setFilesize(String filesize)
    {
        this.filesize = filesize;
    }
    public String getMetatags()
    {
        return metatags;
    }
    public void setMetatags(String metatags)
    {
        this.metatags = metatags;
    }
    public String getSourcecontrolcheckedoutby()
    {
        return sourcecontrolcheckedoutby;
    }
    public void setSourcecontrolcheckedoutby(String sourcecontrolcheckedoutby)
    {
        this.sourcecontrolcheckedoutby = sourcecontrolcheckedoutby;
    }
    public String getSourcecontroltimecheckedout()
    {
        return sourcecontroltimecheckedout;
    }
    public void setSourcecontroltimecheckedout(String sourcecontroltimecheckedout)
    {
        this.sourcecontroltimecheckedout = sourcecontroltimecheckedout;
    }
    public String getThicketsupportingfile()
    {
        return thicketsupportingfile;
    }
    public void setThicketsupportingfile(String thicketsupportingfile)
    {
        this.thicketsupportingfile = thicketsupportingfile;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * @return the listbasetype
     */
    public String getListbasetype()
    {
        return listbasetype;
    }

    /**
     *
     * Specifies which of several supported base List types is used for the List
     * associated with this folder.
     *
     * @param listbasetype the listbasetype to set
     */
    public void setListbasetype(String listbasetype)
    {
        this.listbasetype = listbasetype;
    }

    public void setDocInfoProperties(Map<String, String> properties)
    {
        for (String key : properties.keySet())
        {
            // set properties common for FOLDER and FILE
            if (key.equals(VtiProperty.FILE_TIMELASTMODIFIED.toString()))
            {
                this.setTimelastmodified(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_TIMELASTWRITTEN.toString()))
            {
                this.setTimelastwritten(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_TIMECREATED.toString()))
            {
                this.setTimecreated(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_THICKETDIR.toString()))
            {
                this.setThicketdir(properties.get(key));
            }

            // set FOLDER properties
            if (this.isFolder())
            {
                if (key.equals(VtiProperty.FOLDER_DIRLATESTSTAMP.toString()))
                {
                    this.setDirlateststamp(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_HASSUBDIRS.toString()))
                {
                    this.setHassubdirs(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISBROWSABLE.toString()))
                {
                    this.setIsbrowsable(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISCHILDWEB.toString()))
                {
                    this.setIschildweb(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISEXECUTABLE.toString()))
                {
                    this.setIsexecutable(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISEXECUTABLE.toString()))
                {
                    this.setIsexecutable(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISSCRIPTABLE.toString()))
                {
                    this.setIsscriptable(properties.get(key));
                }
            }
            else
            {   // set FILE properties
                if (key.equals(VtiProperty.FILE_FILESIZE.toString()))
                {
                    this.setFilesize(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_METATAGS.toString()))
                {
                    this.setMetatags(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_SOURCECONTROLCHECKEDOUTBY.toString()))
                {
                    this.setSourcecontrolcheckedoutby(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_SOURCECONTROLTIMECHECKEDOUT.toString()))
                {
                    this.setSourcecontroltimecheckedout(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_TITLE.toString()))
                {
                    this.setTitle(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_THICKETSUPPORTINGFILE.toString()))
                {
                    this.setThicketsupportingfile(properties.get(key));
                }
            }
        }
    }

    public String getSourcecontrollockexpires()
    {
        return sourcecontrollockexpires;
    }

    public void setSourcecontrollockexpires(String sourcecontrollockexpires)
    {
        this.sourcecontrollockexpires = sourcecontrollockexpires;
    }

    public String getSourcecontrolcookie()
    {
        return sourcecontrolcookie;
    }

    public void setSourcecontrolcookie(String sourcecontrolcookie)
    {
        this.sourcecontrolcookie = sourcecontrolcookie;
    }

    public String getSourcecontrolversion()
    {
        return sourcecontrolversion;
    }

    public void setSourcecontrolversion(String sourcecontrolversion)
    {
        this.sourcecontrolversion = sourcecontrolversion;
    }
}
