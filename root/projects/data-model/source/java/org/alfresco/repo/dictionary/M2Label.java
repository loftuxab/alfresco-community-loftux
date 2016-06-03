package org.alfresco.repo.dictionary;

import java.util.Locale;

import org.springframework.extensions.surf.util.I18NUtil;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.QName;
import org.springframework.util.StringUtils;


/**
 * Helper for obtaining display labels for data dictionary items
 * 
 * @author David Caruana
 */
public class M2Label
{

    /**
     * Get label for data dictionary item given specified locale
     * 
     * @param locale Locale
     * @param model ModelDefinition
     * @param messageLookup MessageLookup
     * @param type String
     * @param item QName
     * @param label String
     * @return String
     */
    public static String getLabel(Locale locale, ModelDefinition model, MessageLookup messageLookup, String type, QName item, String label)
    {
        if (messageLookup == null)
        {
            return null;
        }
        String key = model.getName().toPrefixString();
        if (type != null)
        {
            key += "." + type;
        }
        if (item != null)
        {
            key += "." + item.toPrefixString();
        }
        key += "." + label;
        key = StringUtils.replace(key, ":", "_");
        return messageLookup.getMessage(key, locale);
    }
    
    /**
     * Get label for data dictionary item
     * 
     * @param model ModelDefinition
     * @param messageLookup MessageLookup
     * @param type String
     * @param item QName
     * @param label String
     * @return String
     */
    public static String getLabel(ModelDefinition model, MessageLookup messageLookup, String type, QName item, String label)
    {
        return getLabel(I18NUtil.getLocale(), model, messageLookup, type, item, label);
    }
    
}
