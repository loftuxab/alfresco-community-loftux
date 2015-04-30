/*
 * Copyright 2012-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.subsystems;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.alfresco.repo.management.subsystems.PropertyBackedBeanWithMonitor;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.enterprise.repo.management.MBeanSupport;

/**
 * A monitor adapter is used to blend a monitor bean onto a property backed bean.   So the methods and properties of the monitor are added together 
 * with the methods and properies of the property backed bean.  
 */
/** package scope */ class MonitorAdapter extends MBeanSupport
{
  
   
   private PropertyBackedBeanWithMonitor bean;
   
   private List<String> readOnlyProperties = new ArrayList<String>();
   
   private static Log logger = LogFactory.getLog(PropertyBackedBeanAdapter.class);
   
   /** 
    * @throws NoSuchMethodException 
    * @throws InvocationTargetException 
    *  @throws IllegalAccessException 
    */ 
   /*package scope */MonitorAdapter(TransactionService transactionService, PropertyBackedBeanWithMonitor bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
       super(transactionService);
       this.bean = bean;
       
       final Object monitor = bean.getMonitorObject();
 
       Map<String, Object> stuff = PropertyUtils.describe(monitor);     

       for(Object key : stuff.keySet())
       {
          String prop = (String)key;
          Object value = stuff.get(prop);
    
          if(!key.equals("class"))
          {
              logger.debug("adding:" + key + "value:" + value);
              readOnlyProperties.add(prop);
          }
       }
   }
   
   public boolean hasAttribute(final String attribute)
   {
       return readOnlyProperties.contains(attribute);
   }
   
   public Object getAttribute(final String attribute) throws AttributeNotFoundException, MBeanException
   {
	   final Object monitor = bean.getMonitorObject();
	   
               try
               {
                   return BeanUtils.getProperty(monitor, attribute);
               }
               catch (InvocationTargetException e)
               {
                   logger.error(e);
                   throw new RuntimeException(e.getTargetException());
               }
               catch (NoSuchMethodException e)
               {
                   logger.error(e);
                   throw new RuntimeException(e);
                 
               }
               catch (IllegalAccessException e)
               {
                   logger.error(e);
                   throw new RuntimeException(e);
               }
   }
   
   public List<String>getReadOnlyProperties()
   {
       return readOnlyProperties;
   }
   
   public String getPropertyDescripton(String name)
   {
	   final Object monitor = bean.getMonitorObject();
       try
       {
           PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(monitor, name);
           return descriptor.getShortDescription();
       } 
       catch (IllegalAccessException e)
       {
           return name;

       } 
       catch (InvocationTargetException e)
       {
           return name;
        
       } 
       catch (NoSuchMethodException e)
       {
           return name;
       }
   }
   
   /**
    * Pattern to exclude methods beginning with set/get/is
    */
   static Pattern  excludeMethodPattern = Pattern.compile("^[gs]et.*|^is.*");
   
   public Method[] getDeclaredMethods()
   {
	   final Object monitor = bean.getMonitorObject();
	   
       Method[] methods = monitor.getClass().getDeclaredMethods();
       
       List<Method>retVal = new ArrayList<Method>(methods.length);
       
       for (Method method : methods)
       {
           Matcher matcher = excludeMethodPattern.matcher(method.getName());
           if(!matcher.matches())
           {
               retVal.add(method);
           }
       }
         
       // TODO need to strip out methods beginning with get, set and is
       return retVal.toArray(new Method[retVal.size()]);
   }
   
   public boolean hasMethod(String actionName,  Class<?>[]parameterTypes)
   {
	   final Object monitor = bean.getMonitorObject();
	   
       Method[] methods = monitor.getClass().getDeclaredMethods();
       for(Method method : methods)
       {
           if(method.getName().equals(actionName))
           {
               return true;
           }
       }
       return false;
   }
   
     
   public Object invokeMethod(String actionName,  Class<?>[]parameterTypes, Object[] params) 
   throws IllegalAccessException, 
   IllegalArgumentException, 
   InvocationTargetException, 
   NoSuchMethodException, 
   SecurityException
   {
	   final Object monitor = bean.getMonitorObject();
       return monitor.getClass().getMethod(actionName, parameterTypes).invoke(monitor, params);
   }
   
   public boolean getIsIs(String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
   {
	   final Object monitor = bean.getMonitorObject();
	   
       PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(monitor, name);
       
       if(descriptor != null)
       {
           Method writeMethod = descriptor.getWriteMethod();
           if(writeMethod != null)
           {
               if(writeMethod.getName().startsWith("is"))
               {
                   return true;
               }
           }
       }
       return false;
   }
}

