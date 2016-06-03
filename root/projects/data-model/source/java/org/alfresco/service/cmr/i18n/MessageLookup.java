package org.alfresco.service.cmr.i18n;

import java.util.Locale;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.NotAuditable;

/**
 * An object providing basic message lookup facilities. May (or may not) be directly conntect to resource bundles.
 */
@AlfrescoPublicApi
public interface MessageLookup
{
    /**
     * Get message from registered resource bundle.
     * 
     * @param messageKey
     *            message key
     * @return localised message string, null if not found
     */
    @NotAuditable
    public String getMessage(String messageKey);

    /**
     * Get a localised message string
     * 
     * @param messageKey
     *            the message key
     * @param locale
     *            override the current locale
     * @return the localised message string, null if not found
     */
    @NotAuditable
    public String getMessage(final String messageKey, final Locale locale);

    /**
     * Get a localised message string, parameterized using standard MessageFormatter.
     * 
     * @param messageKey
     *            message key
     * @param params
     *            format parameters
     * @return the localised string, null if not found
     */
    @NotAuditable
    public String getMessage(String messageKey, Object... params);

    /**
     * Get a localised message string, parameterized using standard MessageFormatter.
     * 
     * @param messageKey
     *            the message key
     * @param locale
     *            override current locale
     * @param params
     *            the localised message string
     * @return the localised string, null if not found
     */
    @NotAuditable
    public String getMessage(String messageKey, Locale locale, Object... params);
}
