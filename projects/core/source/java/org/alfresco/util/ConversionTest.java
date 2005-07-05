package org.alfresco.util;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Unit test to make sure the Conversion utility is working correctly
 * 
 * @author gavinc
 */
public class ConversionTest extends BaseTest
{
   private static Logger logger = Logger.getLogger(ConversionTest.class);
   
   public void testDateConversion() throws Exception
   {
      Date now = new Date();
      String dateBefore = now.toString();
      logger.info("date before = " + dateBefore);
      
      String formattedDate = Conversion.dateToXmlDate(now);
      logger.info("formattedDate = " + formattedDate);
      
      Date dateAfterXml = Conversion.dateFromXmlDate(formattedDate);
      String dateAfter = dateAfterXml.toString();
      logger.info("date after = " + dateAfter);
      
      assertEquals("The date before and after conversion should be the same", dateBefore, dateAfter);
   }
}
