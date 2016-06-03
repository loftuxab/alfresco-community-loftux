package org.alfresco.module.vti.metadata.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Custom type to storing list of the files and folders.
 * Used in dialogview method (FileOpen/Save) to retrieve all items from current folder.</p>
 * 
 * @author PavelYur
 */
public class DialogsMetaInfo implements Serializable
{
    private static final long serialVersionUID = 5024942474191917006L;

    private List<DialogMetaInfo> dialogMetaInfoList = new ArrayList<DialogMetaInfo>();    

    /**
     * @return the dialogMetaInfoList
     */
    public List<DialogMetaInfo> getDialogMetaInfoList()
    {
        return dialogMetaInfoList;
    }    
}
