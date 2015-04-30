/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * SyncNodeException - Unable to sync a node.   
 * 
 * Currently, all of the exception types are expressible without
 *  needing message parameters.
 * Because this exception is transported between repositories, if
 *  message parameters are needed in future then the transport 
 *  must be updated to send them.
 * 
 * @author Mark Rogers
 * @since 4.1
 */
public class SyncNodeException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 5281248091498804547L;
    private final SyncNodeExceptionType exceptionType;
    
    /**
     * The list of possible sync node related exceptions, along with
     *  their message key (used to look up a localised description
     *  of the problem for display to end users)
     */
    public enum SyncNodeExceptionType 
    {
        /**
         * A node already exists in the target folder with the same name
         */
         TARGET_FOLDER_NAME_CLASH("sync.folder.name_clash"),
        /**
          * Target folder could not be found
          */ 
         TARGET_FOLDER_NOT_FOUND("sync.folder.not_found"),
         /**
          * Content cannot be created, it is already synchronized from somewhere else
          */
         TARGET_NODE_ALREADY_SYNCED("sync.node.already_synced"),
         /**
          * Content has already been synchronized from somewhere else
          */
         TARGET_NODE_OTHER_SYNC_SET("sync.node.other_sync_set"),
         /**
          * Content no longer exists on the remote system
          */
         TARGET_NODE_NO_LONGER_EXISTS("sync.node.no_longer_exists"),
         /**
          * Access has been denied to the target node
          */
         TARGET_NODE_ACCESS_DENIED("sync.node.access_denied"),
         /**
          * Access has been denied to the source node
          */
         SOURCE_NODE_ACCESS_DENIED("sync.node.access_denied_source"),
         /**
          * Content Limit Violation (eg. exceeded max configured file size)
          */
         CONTENT_LIMIT_VIOLATION("sync.node.content_limit_violation"),
         /**
          * Quota Limit Violation (eg. change would push network beyond its quota)
          */
         QUOTA_LIMIT_VIOLATION("sync.node.quota_limit_violation"),
         /**
          * Authentication Error
          */
         AUTHENTICATION_ERROR("sync.node.authentication_error"),
         /**
          * Owner has been deleted
          */
         OWNER_NOT_FOUND("sync.node.owner_not_found"),
         /**
          * Owner has been deleted
          */
         DELETED_ON_CLOUD("sync.node.deleted_on_cloud"),
         /**
           * An unknown error occurred, and no details are available
           */		
         UNKNOWN("sync.node.unknown");
         
         private String messageId;
         private SyncNodeExceptionType(String messageId)
         {
             this.messageId = messageId;
         }
         public String getMessageId()
         {
             return messageId;
         }
         public String getMessage()
         {
             return I18NUtil.getMessage(messageId);
         }
         
         /**
          * Identifies the appropriate ExceptionType, based on the message ID
          */
         public static SyncNodeExceptionType fromMessageId(String messageId)
         {
             SyncNodeExceptionType[] values = SyncNodeExceptionType.values();
             for( SyncNodeExceptionType value : values)
             {
                 if(value.messageId.equalsIgnoreCase(messageId))
                 {
                     return value;
                 }
             }
             return SyncNodeExceptionType.UNKNOWN;
         }
    };
    
    public SyncNodeException(SyncNodeExceptionType type)
    {
        super(type.getMessageId());
        this.exceptionType = type;
    }
    private SyncNodeException(SyncNodeExceptionType type, Throwable cause)
    {
        super(type.getMessageId(), cause);
        this.exceptionType = type;
    }
    
    /**
     * Wraps an unhandled exception (normally a bug) as an Unknown SyncNodeException,
     * so it can be transported back to the other end.
     */
    public static SyncNodeException wrapUnhandledException(Throwable cause)
    {
        return new SyncNodeException(SyncNodeExceptionType.UNKNOWN, cause);
    }

    public SyncNodeExceptionType getExceptionType()
    {
        return exceptionType;
    }
}
