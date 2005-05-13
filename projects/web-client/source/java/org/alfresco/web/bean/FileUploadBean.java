package org.alfresco.web.bean;

import java.io.File;

/**
 * Bean to hold the results of a file upload
 * 
 * @author gavinc
 */
public class FileUploadBean
{
   private File file;
   private String fileName;
   private String filePath;
   
   /**
    * @return Returns the file
    */
   public File getFile()
   {
      return file;
   }
   
   /**
    * @param file The file to set
    */
   public void setFile(File file)
   {
      this.file = file;
   }

   /**
    * @return Returns the name of the file uploaded
    */
   public String getFileName()
   {
      return fileName;
   }

   /**
    * @param fileName The name of the uploaded file
    */
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   /**
    * @return Returns the path of the file uploaded
    */
   public String getFilePath()
   {
      return filePath;
   }

   /**
    * @param filePath The file path of the uploaded file
    */
   public void setFilePath(String filePath)
   {
      this.filePath = filePath;
   }  
}
