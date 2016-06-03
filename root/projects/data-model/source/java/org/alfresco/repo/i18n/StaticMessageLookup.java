package org.alfresco.repo.i18n;

import java.util.Locale;

import org.alfresco.service.NotAuditable;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * A {@link MessageLookup} that retrieves messages from a resource bundle in the classpath.
 */
public class StaticMessageLookup implements MessageLookup
{

    /*
     * (non-Javadoc)
     * @see org.alfresco.service.cmr.i18n.MessageLookup#getMessage(java.lang.String)
     */
    @Override
    @NotAuditable
    public String getMessage(String messageKey)
    {
        return I18NUtil.getMessage(messageKey);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.service.cmr.i18n.MessageLookup#getMessage(java.lang.String, java.util.Locale)
     */
    @Override
    @NotAuditable
    public String getMessage(String messageKey, Locale locale)
    {
        return I18NUtil.getMessage(messageKey, locale);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.service.cmr.i18n.MessageLookup#getMessage(java.lang.String, java.lang.Object[])
     */
    @Override
    @NotAuditable
    public String getMessage(String messageKey, Object... params)
    {
        return I18NUtil.getMessage(messageKey, params);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.service.cmr.i18n.MessageLookup#getMessage(java.lang.String, java.util.Locale,
     * java.lang.Object[])
     */
    @Override
    @NotAuditable
    public String getMessage(String messageKey, Locale locale, Object... params)
    {
        return I18NUtil.getMessage(messageKey, locale, params);
    }
}
