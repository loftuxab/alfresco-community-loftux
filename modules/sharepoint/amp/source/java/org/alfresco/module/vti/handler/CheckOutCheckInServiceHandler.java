
package org.alfresco.module.vti.handler;

import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionType;

/**
 * Interface for checkOut web service handler
 * 
 * @author DmitryVas
 */
public interface CheckOutCheckInServiceHandler
{
    /**
     * Check out provided document and creates write lock on working copy
     * 
     * @param fileName site relative url to the file
     * @param lockAfterSucess true if original node must be locked after operation
     * @return working copy or null if checkOut operation fails
     */
    NodeRef checkOutDocument(String fileName, boolean lockAfterSucess) throws FileNotFoundException;

    /**
     * Check in provided document and creates write lock on original document
     * 
     * @param fileName site relative url to the file
     * @param type major or minor checkin
     * @param comment checkIn comment
     * @return original node or null if checkIn operation fails
     * @param lockAfterSucess true if original node must be locked after operation
     */
    NodeRef checkInDocument(String fileName, VersionType type, String comment, boolean lockAfterSucess) throws FileNotFoundException;

    /**
     * Undo check out on provided document and creates write lock on original document
     * 
     * @param fileName site relative url to the file
     * @param lockAfterSucess true if original node must be locked after operation
     * @return original node or null if undo checkOut operation fails
     */
    NodeRef undoCheckOutDocument(String fileName, boolean lockAfterSucess) throws FileNotFoundException;
}
