/**
 *****************************************************************************
 * 
 * Project        Web UI Framework
 * Module         Web Databound
 * File           IDataProviderTag.java
 * Description    Data Provider interface
 * Created on     10th-Feb-2005
 * Tab width      3
 * 
 *****************************************************************************
 */
package com.activiti.web.jsf.tag.data;

import java.util.List;

import com.activiti.web.data.IDataHandler;

/**
 * IDataProviderTag
 * 
 * Data Provider interface. Implemented by Tags that can provide a Data Handler
 * for use by enclosed child tags.
 */
public interface IDataProviderTag
{
   /**
    * Get the IDataHandler for this Data Provider Tag
    * @return
    */
   public IDataHandler getDataHandler();
   
} // end interface IDataHandler
