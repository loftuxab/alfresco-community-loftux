package com.activiti.web;

import org.doomdark.uuid.UUIDGenerator;

/**
 * GUID
 *
 * @author kevinr
 */
public final class GUID
{
   /**
    * Private Constructor for GUID.
    */
   private GUID()
   {
   }

   /**
    * Generates and returns a new GUID as a string
    *
    * @return String GUID
    */
   public static String generate()
   {
      return UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
   }
}
