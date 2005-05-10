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
    * Returns a ThreadLocal instance of the UIContextService
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
   
   public void registerBean(IContextListener bean)
   {
      if (bean == null)
      {
         throw new IllegalArgumentException("Bean reference specified cannot be null!");
      }
      
      this.registeredBeans.put(bean.getClass(), bean);
   }
   
   public void notifyBeans()
   {
      for (IContextListener listener: this.registeredBeans.values())
      {
         listener.contextUpdated();
      }
   }
   
   
   /** key for the UI context service in the session */
   private final static String CONTEXT_KEY = "__uiContextService";
   
   private Map<Class, IContextListener> registeredBeans = new HashMap<Class, IContextListener>(7, 1.0f);
}
