package org.alfresco.repo.transfer.fsr;
/**
 * When receiving nodes first in the FileTransferPrimaryManifestProcessor
 * node are renamed to avoid names collisions while moving nodes.
 * This entity contains the nodeRef associated with the final name
 * that will be given to the node.
 * @author philippe
 *
 */
public class FileTransferNodeRenameEntity
{
    private long id;
    private String renamedNodeRef;
    private String transferId;
    private String newName;

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getRenamedNodeRef()
    {
        return renamedNodeRef;
    }
    public void setRenamedNodeRef(String renamedNodeRef)
    {
        this.renamedNodeRef = renamedNodeRef;
    }

    public String getNewName()
    {
        return newName;
    }
    public void setNewName(String newName)
    {
        this.newName = newName;
    }
    public String getTransferId()
    {
        return transferId;
    }
    public void setTransferId(String transferId)
    {
        this.transferId = transferId;
    }

}
