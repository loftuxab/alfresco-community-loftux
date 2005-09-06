package org.alfresco.i18n;

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
    
    /**
     * List of registered bundles
     */
    private static Set<String> resouceBundleBaseNames = new HashSet<String>();
    
    /**
     * Map of loaded bundles by Locale
     */
    private static Map<Locale, Set<String>> loadedResourceBundles = new HashMap<Locale, Set<String>>();
    
    /**
     * Map of cached messaged by Locale
     */
    private static Map<Locale, Map<String, String>> cachedMessages = new HashMap<Locale, Map<String, String>>();
    
    /**
     * Lock objects
     */
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
     * Register a resource bundle.
     * <p>
     * This should be the bundle base name eg, alfresco.messages.errors
     * <p>
     * Once registered the messges will be available via getMessage
     * 
     * @param bundleBaseName    the bundle base name
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
     * Get message from registered resource bundle.
     * 
     * @param messageKey    message key
     * @return              localised message string, null if not found
     */
    public static String getMessage(String messageKey)
    {
        return getMessage(messageKey, getLocale());
    }
    
    /**
     * Get a localised message string
     * 
     * @param messageKey        the message key
     * @param locale            override the current locale
     * @return                  the localised message string, null if not found
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
     * Get a localised message string, parameterized using standard MessageFormatter.
     * 
     * @param messageKey    message key
     * @param params        format parameters
     * @return              the localised string, null if not found
     */
    public static String getMessage(String messageKey, Object[] params)
    {
        return getMessage(messageKey, params, getLocale());
    }
    
    /**
     * Get a localised message string, parameterized using standard MessageFormatter.
     * 
     * @param messageKey        the message key
     * @param params            the localised message string
     * @param locale            override current locale
     * @return                  the localaised string, null if not found
     */
    public static String getMessage(String messageKey, Object[] params, Locale locale)
    {
        String message = getMessage(messageKey, locale);
        if (message != null && params != null)
        {
            message = MessageFormat.format(message, params);
        }
        return message;
    }
    
    /**
     * Get the messages for a locale.
     * <p>
     * Will use cache where available otherwise will load into cache from bundles.
     * 
     * @param locale    the locale
     * @return          message map
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
