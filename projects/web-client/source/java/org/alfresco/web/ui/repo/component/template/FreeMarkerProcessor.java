/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.repo.component.template;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;

import javax.faces.context.FacesContext;
import javax.transaction.UserTransaction;

import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.apache.log4j.Logger;

import freemarker.cache.MruCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarker implementation the template processor interface
 * 
 * @author Kevin Roast
 */
public class FreeMarkerProcessor implements ITemplateProcessor
{
   private final static String MSG_ERROR_NO_TEMPLATE   = "error_no_template";
   private final static String MSG_ERROR_TEMPLATE_FAIL = "error_template_fail";
   private final static String MSG_ERROR_TEMPLATE_IO   = "error_template_io";
   
   private static Logger logger = Logger.getLogger(FreeMarkerProcessor.class);
   
   private Configuration config;
   
   /**
    * Constructor
    */
   public FreeMarkerProcessor()
   {
      Configuration config = new Configuration();
      
      // setup template cache
      config.setCacheStorage(new MruCacheStorage(20, 0));
      
      // use our custom loader to find templates on the ClassPath
      //config.setTemplateLoader(new ClassPathTemplateLoader());
      config.setTemplateLoader(new ClassPathRepoTemplateLoader());
      
      // use our custom object wrapper that can deal with QNameMap objects directly
      config.setObjectWrapper(new QNameAwareObjectWrapper());
      
      // rethrow any exception so we can deal with them
      config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      
      this.config = config;
   }

   /**
    * @see org.alfresco.web.ui.repo.component.template.ITemplateProcessor#process(java.lang.String, java.lang.Object, java.io.Writer)
    */
   public void process(String template, Object model, Writer out)
   {
      if (template == null || template.length() == 0)
      {
         throw new IllegalArgumentException("Template name is mandatory.");
      }
      if (model == null)
      {
         throw new IllegalArgumentException("Model is mandatory.");
      }
      if (out == null)
      {
         throw new IllegalArgumentException("Output Writer is mandatory.");
      }
      
      try
      {
         if (logger.isDebugEnabled())
            logger.debug("Executing template: " + template + " on model: " + model);
         
         Template t = this.config.getTemplate(template);
         if (t != null)
         {
            UserTransaction tx = null;
            try
            {
               FacesContext context = FacesContext.getCurrentInstance();
               tx = Repository.getUserTransaction(context);
               tx.begin();
               
               // perform the template processing against supplied data model
               t.process(model, out);
               
               tx.commit();
            }
            catch (Throwable err)
            {
               Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), MSG_ERROR_TEMPLATE_FAIL), new Object[] {err.getMessage()}), err);
               try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
            }
         }
         else
         {
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), MSG_ERROR_NO_TEMPLATE), new Object[] {template}));
         }
      }
      catch (IOException ioerr)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), MSG_ERROR_TEMPLATE_IO), new Object[] {template}), ioerr);
      }
   }
}
