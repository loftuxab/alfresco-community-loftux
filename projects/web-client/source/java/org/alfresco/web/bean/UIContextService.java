/*
 * Created on 10-May-2005
 */
package org.alfresco.web.bean;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

/**
 * @author Kevin Roast
 */
public final class UIContextService
{
   /**
    * Private constructor
    */
   private UIContextService()
   {
   }
   
   /**
    * Returns a Session local instance of the UIContextService
    * 
    * @return UIContextService for this Thread
    */
   public static UIContextService getInstance(FacesContext fc)
   {
      Map session = fc.getExternalContext().getSessionMap();
      UIContextService service = (UIContextService)session.get(CONTEXT_KEY);
      if (service == null)
      {
         service = new UIContextService();
         session.put(CONTEXT_KEY, service);
      }
      
      return service;
   }
   
   /**
    * Register a bean to be informed of context events
    * 
    * @param bean    Conforming to the IContextListener interface
    */
   public void registerBean(IContextListener bean)
   {
      if (bean == null)
      {
         throw new IllegalArgumentException("Bean reference specified cannot be null!");
      }
      
      this.registeredBeans.put(bean.getClass(), bean);
   }
   
   /**
    * Remove a bean reference from those notified of changes
    * 
    * @param bean    Conforming to the IContextListener interface
    */
   public void unregisterBean(IContextListener bean)
   {
      if (bean == null)
      {
         throw new IllegalArgumentException("Bean reference specified cannot be null!");
      }
      
      this.registeredBeans.remove(bean);
   }
   
   /**
    * Call to notify all register beans that the UI context has changed and they should
    * refresh themselves as appropriate.
    */
   public void notifyBeans()
   {
      for (IContextListener listener: this.registeredBeans.values())
      {
         listener.contextUpdated();
      }
   }
   
   
   /** key for the UI context service in the session */
   private final static String CONTEXT_KEY = "__uiContextService";
   
   /** Map of bean registered against the context service */
   private Map<Class, IContextListener> registeredBeans = new HashMap<Class, IContextListener>(7, 1.0f);
}
