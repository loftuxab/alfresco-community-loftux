package org.alfresco.solr.component;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;


@RunWith(MockitoJUnitRunner.class)
public class TempFileWarningLoggerTest
{
    private @Mock Logger log;
    private Path path;
    
    @Before
    public void setUp()
    {
        path = Paths.get(System.getProperty("java.io.tmpdir"));
        
        // Simulate warn-level logging
        Mockito.when(log.isWarnEnabled()).thenReturn(true);
    }
    
    @Test
    public void checkGlobBuiltCorrectly()
    {
        TempFileWarningLogger warner = 
                    new TempFileWarningLogger(
                                log,
                                "MyPrefix*",
                                new String[] { "temp", "remove-me", "~notrequired" },
                                path);
        
        assertEquals("MyPrefix*.{temp,remove-me,~notrequired}", warner.getGlob());
    }
    
    @Test
    public void checkFindFiles() throws IOException
    {
        File f = File.createTempFile("MyPrefix", ".remove-me", path.toFile());
        f.deleteOnExit();
        
        try
        {
            TempFileWarningLogger warner = 
                        new TempFileWarningLogger(
                                    log,
                                    "MyPrefix*",
                                    new String[] { "temp", "remove-me", "~notrequired" },
                                    path);
            
            boolean found = warner.checkFiles();
            
            assertTrue("Should have found matching files", found);
            // Should be a warn-level log message.
            Mockito.verify(log, atLeastOnce()).warn(Mockito.anyString());
        }
        finally
        {
            f.delete();
        }
    }
    
    
    @Test
    public void checkWhenNoFilesToFind() throws IOException
    {
        File f = new File(path.toFile(), "TestFile.random");
        
        // It would be very odd if this file exists!
        assertFalse("Unable to perform test as file exists: " + f, f.exists());
                
        TempFileWarningLogger warner = 
                    new TempFileWarningLogger(
                                log,
                                "TestFile",
                                new String[] { "random" },
                                path);
        
        boolean found = warner.checkFiles();
        
        assertFalse("Should NOT have found matching file", found);
        // Should be no warn-level log message.
        Mockito.verify(log, Mockito.never()).warn(Mockito.anyString());
    }    
}
