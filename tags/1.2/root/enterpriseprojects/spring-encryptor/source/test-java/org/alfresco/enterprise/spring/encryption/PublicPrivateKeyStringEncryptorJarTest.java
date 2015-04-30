package org.alfresco.enterprise.spring.encryption;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mrogers
 * 
 * Integration test for PublicPrivateKeyStringEncryptor
 */
public class PublicPrivateKeyStringEncryptorJarTest
{
    public String JAR_FILE_NAME = "alfresco-spring-encryptor.jar";
    
  

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testUsageMessage() throws Exception
    {
        Runtime rt = Runtime.getRuntime();
        
        // test needs to be run in build dir (.target)
        // if run by maven root pom sets target dir
        String targetDir = System.getProperty("alfresco.target.dir");
        if(targetDir == null)
        {
            targetDir = "./target";    // test needs to be run in target dir.
        }
   
        File target = new File(targetDir);     
        assertTrue("target dir does not exist :" + targetDir ,target.exists());
        
        Process pr = rt.exec("java -jar " + JAR_FILE_NAME, null, target);
             
        
        String line;
        
        BufferedReader out = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while((line = out.readLine()) != null)
        {
            assertEquals(line, "Alfresco Encrypted Properties Management Tool");
        }
        
        BufferedReader err = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        while((line = err.readLine()) != null)
        { 
        }
       
        int exitValue = pr.waitFor();
        
        assertEquals("return not 255", 255, exitValue);
    }
    
    @Test
    public void testEncryptAndDecrypt() throws Exception
    {
        Runtime rt = Runtime.getRuntime();
        
        // test needs to be run in build dir (.target)
        // if run by maven root pom sets target dir
        String targetDir = System.getProperty("alfresco.target.dir");
        if(targetDir == null)
        {
            targetDir = "./target";    // test needs to be run in target dir.
        }
        
        File target = new File(targetDir);        
        assertTrue("target dir does not exist :" + targetDir ,target.exists());

        File alfresco = new File(target, "alfresco");
        File extension = new File(alfresco, "extension");
        File enterprise = new File(extension, "enterprise");
        enterprise.mkdirs();
          
        assertTrue("enterprise dir does not exist :" + targetDir ,enterprise.exists());        
        
        // Initialise the keystore
        Process pr = rt.exec("java -jar " + JAR_FILE_NAME + " initkey .", null, target);
      
        String line;
        BufferedReader out = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while((line = out.readLine()) != null)
        {
        }
        
        int exitValue = pr.waitFor();
        
        assertEquals("return not 0 (initialise the keystore)", 0, exitValue);
        
        // Encrypt a value with prompting
        pr = rt.exec("java -jar " + JAR_FILE_NAME + " encrypt . foo foo", null, new File(targetDir));
        
        //Input i = new Input(pr.getOutputStream(), new String[]{"foo" , "foo"});
        //i.run();
        
        StringBuffer encryptedReader = new StringBuffer();
        out = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while((line = out.readLine()) != null)
        {    
            encryptedReader.append(line);
        }
        
        String encryptedValue = encryptedReader.toString();
        assertNotNull("encryptedValue");
        assertNotEquals("", encryptedValue);
        exitValue = pr.waitFor();
        assertEquals("return not 0 (encrypt a value)", 0, exitValue);
        
        // Decrypt the encrypted value
        pr = rt.exec("java -jar " + JAR_FILE_NAME + " validate . " + encryptedValue + " foo foo", null, new File(targetDir));
                
        out = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while((line = out.readLine()) != null)
        {    
        }
        
        exitValue = pr.waitFor();
        assertEquals("return not 0 (validate a value)", 0, exitValue);
        
    }
    
    
    /**
     * 
     */
    class Input extends Thread
    {
        OutputStream is;
        String[] input;
        Input(OutputStream is, String[]input)
        {
            this.is = is;
            this.input = input;
        }
        
        public void run()
        {
            try (BufferedWriter in = new BufferedWriter (new OutputStreamWriter(is));)
            {
                for(String line : input)
                {
                    in.write(line);
                    in.newLine();
                }
            }
            catch (IOException e)
            {
                
            }
        }
    }
    
    
}
