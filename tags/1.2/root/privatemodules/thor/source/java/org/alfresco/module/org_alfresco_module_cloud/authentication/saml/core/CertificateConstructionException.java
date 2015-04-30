/* 
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class CertificateConstructionException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 5462847103831474645L;

    /**
     * Constructor
     * 
     * @param msgId
     *            the message id
     */
    public CertificateConstructionException(String msgId)
    {
        super(msgId);
    }

    /**
     * Constructor
     * 
     * @param msgId
     *            the message id
     * @param cause
     *            the exception cause
     */
    public CertificateConstructionException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }
}
