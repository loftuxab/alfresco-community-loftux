/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.alfresco.util.TempFileProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Base class for Spring integration-style testing that requires a cluster-enabled license.
 * 
 * @author Matt Ward
 */
public class AbstractLicensedClusterTestBase
{
    private static File licSourceDir;
    private static File sandBoxDir;
    private static ClassPathXmlApplicationContext context;
    private static final String[] CONFIG_LOCATIONS = new String[]
    {
        "classpath:alfresco/application-context.xml",
        "classpath:license-application-context-test.xml",
        "classpath*:alfresco/license-application-context-override.xml"
    };
    
    
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        // Stops ConcurrentModificationException when run as part of ClusterTestSuite...
        ClusteredObjectProxyFactory.clear();
        
        locateTestLicenses();        
        initLicenseSandbox();
        // Create the Spring application context
        context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, false);
        setUpContextClassLoader();        
        context.refresh();
    }

    @AfterClass
    public static void afterClass() throws Exception
    {
        deleteFileRecurse(sandBoxDir);
        context.close();
    }

    private static void initLicenseSandbox() throws IOException
    {
        sandBoxDir = TempFileProvider.getTempDir();

        // Copy this test's files to a sandbox directory
        copyFileRecurse(new File(licSourceDir, "testClusterEnabled"), sandBoxDir);
    }

    private static void setUpContextClassLoader() throws MalformedURLException
    {
        // Put the sandbox directory at the front of the classpath
        ClassLoader classLoader = new URLClassLoader(new URL[]
        {
            sandBoxDir.toURL()
        }, Thread.currentThread().getContextClassLoader());
        context.setClassLoader(classLoader);
    }

    private static void locateTestLicenses() throws ExceptionInInitializerError
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        // Find the source directory on the classpath
        Resource[] resources;
        try
        {
            resources = resolver.getResources("classpath*:/licensetest");
            licSourceDir = resources[0].getFile();
        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static ClassPathXmlApplicationContext getContext()
    {
        return context;
    }
    
    /**
     * Utility method to do a recursive file copy.
     * 
     * @param source
     *            the source file or directory (must exist)
     * @param dest
     *            the destination path (need not exist)
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void copyFileRecurse(File source, File dest) throws IOException
    {
        if (source.isDirectory())
        {
            dest.mkdirs();
            for (File file : source.listFiles())
            {
                copyFileRecurse(file, new File(dest, file.getName()));
            }
        }
        else
        {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(dest);
            byte[] buff = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buff)) != -1)
            {
                out.write(buff, 0, bytesRead);
            }
            in.close();
            out.close();
        }
        dest.setLastModified(source.lastModified());
    }

    /**
     * Utility method to recursively delete a file or directory.
     * 
     * @param file
     *            the file or directory
     */
    private static void deleteFileRecurse(File file)
    {
        if (file.isDirectory())
        {
            // from Thor
            File[] children = file.listFiles();
            if (children != null)
            {
                for (File child : children)
                {
                    deleteFileRecurse(child);
                }
            }
        }
        file.delete();
    }

}
