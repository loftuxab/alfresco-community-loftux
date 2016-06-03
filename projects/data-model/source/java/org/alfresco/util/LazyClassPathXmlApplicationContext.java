package org.alfresco.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Element;

/**
 * A wrapper around {@link ClassPathXmlApplicationContext} which forces
 *  all beans to be loaded lazily.
 * You shouldn't do this in production, but it can be handy with
 *  unit tests, as it allows a quicker startup when you don't touch
 *  much of the application.
 *  
 * @author Nick Burch
 */
public class LazyClassPathXmlApplicationContext extends
      ClassPathXmlApplicationContext {
   
   public LazyClassPathXmlApplicationContext(String[] configLocations)
         throws BeansException {
      super(configLocations);
   }

   protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
      super.initBeanDefinitionReader(reader);
      
      postInitBeanDefinitionReader(reader);
   }
    
   /**
    * Does the work of enabling Lazy Init on the xml bean reader
    */
   protected static void postInitBeanDefinitionReader(XmlBeanDefinitionReader reader) {
      reader.setDocumentReaderClass(AlwaysLazyInitBeanDefinitionDocumentReader.class);
   }

   protected static class AlwaysLazyInitBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader {
      @Override
      protected BeanDefinitionParserDelegate createHelper(
            XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {
         BeanDefinitionParserDelegate helper = super.createHelper(readerContext, root, parentDelegate);
         helper.getDefaults().setLazyInit("true");
         return helper;
      }
   }
}
