package org.alfresco.repo.i18n;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Utility class providing methods to access the Locale of the current thread and to get
 * Localised strings.
 * 
 * @author Roy Wetherall
 */
public class I18NUtil
{
    /**
     * Thread local containing the Local for the current thread
     */
    private static ThreadLocal<Locale> currentLocale = new ThreadLocal<Locale>();
    
    private static Set<String> resouceBundleBaseNames = new HashSet<String>();
    
    private static Map<Locale, Set<String>> loadedResourceBundles = new HashMap<Locale, Set<String>>();
    
    private static Map<Locale, Map<String, String>> cachedMessages = new HashMap<Locale, Map<String, String>>();
    
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static Lock readLock = lock.readLock();
    private static Lock writeLock = lock.writeLock();
    
    /**
     * Set the locale for the current thread.
     * 
     * @param locale    the locale
     */
    public static void setLocale(Locale locale)
    {
        currentLocale.set(locale);
    }
     
    /**
     * Get the local for the current thread, will revert to the default locale if none 
     * specified for this thread.
     * 
     * @return  the Locale
     */
    public static Locale getLocale()
    {
        Locale locale = currentLocale.get(); 
        if (locale == null)
        {
            // Get the default locale
            locale = Locale.getDefault();
        }
        return locale;
    }
    
    /**
     * 
     * @param bundleBaseName
     */
    public static void registerResourceBundle(String bundleBaseName)
    {
        try
        {
            writeLock.lock();
            resouceBundleBaseNames.add(bundleBaseName);
        }
        finally
        {
            writeLock.unlock();
        }
    }
    
    /**
     * 
     * @param messageKey
     * @return
     */
    public static String getMessage(String messageKey)
    {
        return getMessage(messageKey, getLocale());
    }
    
    /**
     * Get a localised message string
     * 
     * @param messageKey        the message key
     * @return                  the localised message string
     */
    public static String getMessage(String messageKey, Locale locale)
    {
        String message = null;
        Map<String, String> props = getLocaleProperties(locale);
        if (props != null)
        {
            message = props.get(messageKey);
        }                
        return message;
    }
    
    /**
     * 
     * @param messageKey
     * @param params
     * @return
     */
    public static String getMessage(String messageKey, Object[] params)
    {
        return getMessage(messageKey, params, getLocale());
    }
    
    /**
     * Get a localised message string formatted with the passed parameter values.
     * 
     * @param messageKey        the message key
     * @param params            the localised message string
     * @return
     */
    public static String getMessage(String messageKey, Object[] params, Locale locale)
    {
        return MessageFormat.format(getMessage(messageKey, locale), params);
    }
    
    /**
     * 
     * @param locale
     * @return
     */
    private static Map<String, String> getLocaleProperties(Locale locale)
    {
        Set<String> loadedBundles = null;
        Map<String, String> props = null;
        int loadedBundleCount = 0;
        try
        {
            readLock.lock();
            loadedBundles = loadedResourceBundles.get(locale);
            props = cachedMessages.get(locale);
            loadedBundleCount = resouceBundleBaseNames.size();
        }
        finally
        {
            readLock.unlock();
        }
        
        if (loadedBundles == null)
        {
            try
            {
                writeLock.lock();
                loadedBundles = new HashSet<String>();
                loadedResourceBundles.put(locale, loadedBundles);
            }
            finally
            {
                writeLock.unlock();
            }
        }
        
        if (props == null)
        {
            try
            {
                writeLock.lock();
                props = new HashMap<String, String>();
                cachedMessages.put(locale, props);
            }
            finally
            {
                writeLock.unlock();
            }
        }
                
        if (loadedBundles.size() != loadedBundleCount)
        {
            try
            {
                writeLock.lock();
                for (String resourceBundleBaseName : resouceBundleBaseNames)
                {
                    if (loadedBundles.contains(resourceBundleBaseName) == false)
                    {
                        ResourceBundle resourcebundle = ResourceBundle.getBundle(resourceBundleBaseName, locale);
                        Enumeration<String> enumKeys = resourcebundle.getKeys();
                        while (enumKeys.hasMoreElements() == true)
                        {
                            String key = enumKeys.nextElement();
                            props.put(key, resourcebundle.getString(key));
                        }
                        loadedBundles.add(resourceBundleBaseName);
                    }
                }
            }
            finally
            {
                writeLock.unlock();
            }
        }
        
        return props;
    }
}
