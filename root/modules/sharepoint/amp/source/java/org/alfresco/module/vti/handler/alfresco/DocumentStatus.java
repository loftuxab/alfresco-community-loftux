
package org.alfresco.module.vti.handler.alfresco;

/**
 * Enum used to indicate document status
 *
 * @author Dmitry Lazurkin
 *
 */
public enum DocumentStatus
{
    /**
     * Document isn't checked out and readonly
     */
    NORMAL,

    /**
     * Document isn't checked out, but it is readonly
     */
    READONLY,

    /**
     * Document is short-term checked out and current user isn't checkout owner
     */
    SHORT_CHECKOUT,

    /**
     * Document is short-term checked out and current user is checkout owner
     */
    SHORT_CHECKOUT_OWNER,

    /**
     * Document is long-term checked out and current user isn't checkout owner
     */
    LONG_CHECKOUT,

    /**
     * Document is long-term checked out and current user is checkout owner
     */
    LONG_CHECKOUT_OWNER
}
