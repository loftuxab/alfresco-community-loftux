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
package org.alfresco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ExportStreamHandler;
import org.alfresco.service.cmr.view.Exporter;
import org.alfresco.service.cmr.view.ExporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.TempFileProvider;
import org.springframework.context.ApplicationContext;


/**
 * Alfresco Repository Export Tool
 * 
 * @author David Caruana
 */
public class Export
{

    /**
     * Entry Point
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            ExportContext context = processArgs(args);
    
            try
            {
                Export export = new Export(context);
                export.doExport();
                System.exit(0);
            }
            catch (ExportException e)
            {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
        catch(ExportException e)
        {
            System.out.println(e.getMessage());
            System.out.println();
            displayHelp();
            System.exit(-1);
        }
        catch (Throwable e)
        {
            System.out.println("The following export error has occured: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    
    /** Export Context */
    private ExportContext context;
    

    /**
     * Constuct Export Tool
     * 
     * @param context  export context
     */
    private Export(ExportContext context)
    {
        this.context = context;
    }

    /**
     * Performs the Export
     */        
    private void doExport()
    {
        log("Alfresco Repository Exporter");

        // Get handle onto Exporter service
        ApplicationContext appcontext = ApplicationContextHelper.getApplicationContext();
        ServiceRegistry registry = (ServiceRegistry) appcontext.getBean(ServiceRegistry.SERVICE_REGISTRY);
        ExporterService exporter = registry.getExporterService();
        
        // Create Export package
        File packageFile = context.getPackageFile();
        log("Exporting to package " + packageFile.getAbsolutePath());
        OutputStream output = createPackageFile(packageFile, context.overwrite);
        PackageStreamHandler streamHandler = new PackageStreamHandler(context.getDestDir(), context.getPackageDir(), context.overwrite);
        streamHandler.createPackage();

        // Export Repository content to export package
        exporter.exportView(output, streamHandler, context.getLocation(), context.children, new ExportProgress());
        
        // Close Export File
        try
        {
            output.close();
        }
        catch(IOException e)
        {
            throw new ExportException("Failed to create package file " + packageFile.getAbsolutePath() + " due to" + e.getMessage());
        }
        
        log("Export completed successfully.");
    }

    /**
     * Create XML Export File
     * 
     * @param packageFile  the name of the file
     * @param overwrite  force overwrite of existing xml export file
     * @return the output stream to the file
     */
    private OutputStream createPackageFile(File packageFile, boolean overwrite)
    {
        if (packageFile.exists())
        {
            if (overwrite == false)
            {
                throw new ExportException("Package file " + packageFile.getAbsolutePath() + " already exists.");
            }
            log("Warning: Overwriting existing package file " + packageFile.getAbsolutePath());
            packageFile.delete();
        }

        try
        {
            packageFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(packageFile);
            return outputStream;
        }
        catch(IOException e)
        {
            throw new ExportException("Failed to create package file " + packageFile.getAbsolutePath() + " due to " + e.getMessage());
        }
    }

    /**
     * Log Export message
     * 
     * @param msg  message to log
     */
    private void log(String msg)
    {
        if (context.quiet == false)
        {
            System.out.println(msg);
        }
    }

    /**
     * Log Export message
     * 
     * @param msg  message to log
     */
    private void logVerbose(String msg)
    {
        if (context.verbose)
        {
            log(msg);
        }
    }
    
    /**
     * Process Export Tool command line arguments
     * 
     * @param args  the arguments
     * @return  the export context
     */
    private static ExportContext processArgs(String[] args)
    {
        ExportContext context = new ExportContext();

        int i = 0;
        while (i < args.length)
        {
            if (args[i].equals("-h") || args[i].equals("-help"))
            {
                displayHelp();
                System.exit(0);
            }
            else if (args[i].equals("-s") || args[i].equals("-store"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ExportException("The value <store> for the parameter -store must be specified");
                }
                context.storeRef = new StoreRef(args[i]);
            }
            else if (args[i].equals("-p") || args[i].equals("-path"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ExportException("The value <path> for the parameter -path must be specified");
                }
                context.path = args[i];
            }
            else if (args[i].equals("-d") || args[i].equals("-dir"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ExportException("The value <dir> for the parameter -dir must be specified");
                }
                context.destDir = args[i];
            }
            else if (args[i].equals("-packagedir"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ExportException("The value <packagedir> for the parameter -packagedir must be specified");
                }
                context.packageDir = args[i];
            }
            else if (args[i].equals("-nochildren"))
            {
                context.children = false;
            }
            else if (args[i].equals("-overwrite"))
            {
                context.overwrite = true;
            }
            else if (args[i].equals("-quiet"))
            {
                context.quiet = true;
            }
            else if (args[i].equals("-verbose"))
            {
                context.verbose = true;
            }
            else if (i == (args.length - 1))
            {
                context.packageName = args[i];
            }
            else
            {
                throw new ExportException("Unknown option " + args[i]);
            }

            // next argument
            i++;
        }

        context.validate();
        return context;
    }

    /**
     * Display Help
     */
    private static void displayHelp()
    {
        System.out.println("Usage: export -store store [options] packagename");
        System.out.println("");
        System.out.println("store: the store to extract from in the form of scheme://store_name");
        System.out.println("packagename: the filename to export to (with or without extension)");
        System.out.println("");
        System.out.println("Options:");
        System.out.println(" -h[elp] display this help");
        System.out.println(" -p[ath] the path within the store to extract from (default: /)");
        System.out.println(" -d[ir] the destination directory to export to (default: current directory)");
        System.out.println(" -packagedir the directory to place extracted content (default: dir/<packagename>)");
        System.out.println(" -nochildren do not extract children");
        System.out.println(" -overwrite force overwrite of existing export package if it already exists");
        System.out.println(" -quiet do not display any messages during export");
        System.out.println(" -verbose report export progress");
    }
    
    /**
     * Handler for exporting Repository content streams to file system files
     * 
     * @author David Caruana
     */
    private class PackageStreamHandler
        implements ExportStreamHandler
    {
        private File packageDir;
        private File absPackageDir;
        private boolean overwrite;

        /**
         * Constuct Handler
         * 
         * @param destDir  destination directory
         * @param packageDir  relative directory within destination to place exported content  
         * @param overwrite  force overwrite of existing package directory
         */
        /*package*/ PackageStreamHandler(File destDir, File packageDir, boolean overwrite)
        {
            this.packageDir = packageDir;
            this.absPackageDir = new File(destDir, packageDir.getPath());
            this.overwrite = overwrite;
        }
        
        /**
         * Create the Package Directory
         */
        /*package*/ void createPackage()
        {
            if (absPackageDir.exists())
            {
                if (overwrite == false)
                {
                    throw new ExportException("Package dir " + absPackageDir.getAbsolutePath() + " already exists.");
                }
                log("Warning: Overwriting existing package dir " + absPackageDir.getAbsolutePath());
            }

            try
            {
                absPackageDir.mkdirs();
            }
            catch(SecurityException e)
            {
                throw new ExportException("Failed to create package dir " + absPackageDir.getAbsolutePath() + " due to " + e.getMessage());
            }
        }
        

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ExportStreamHandler#exportStream(java.io.InputStream)
         */
        public String exportStream(InputStream exportStream)
        {
            // Create file in package directory to hold exported content
            File outputFile = TempFileProvider.createTempFile("export", ".bin", absPackageDir);
            
            try
            {
                // Copy exported content from repository to exported file
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[2048 * 10];
                int read = exportStream.read(buffer, 0, 2048 *10);
                while (read != -1)
                {
                    outputStream.write(buffer, 0, read);
                    read = exportStream.read(buffer, 0, 2048 *10);
                }
                outputStream.close();
            }
            catch(FileNotFoundException e)
            {
                throw new ExportException("Failed to create export package file due to " + e.getMessage());
            }
            catch(IOException e)
            {
                throw new ExportException("Failed to export content due to " + e.getMessage());
            }
            
            // return relative path to exported content file (relative to xml export file) 
            return new File(packageDir, outputFile.getName()).getPath();
        }
    }
    
    /**
     * Export Tool Context
     * 
     * @author David Caruana
     */
    private static class ExportContext
    {
        /** Store Reference to export from */
        private StoreRef storeRef;
        /** Path to export from */
        private String path;
        /** Destination directory to export to */
        private String destDir;
        /** The package directory within the destination directory to export to */
        private String packageDir;
        /** The package name to export to */
        private String packageName;
        /** Export children */
        private boolean children = true;
        /** Force overwrite of existing package */
        private boolean overwrite = false;
        /** Log message whilst exporting? */
        private boolean quiet = false;
        /** Verbose logging */
        private boolean verbose = false;
        

        /**
         * Validate the Export Context i.e. ensure all required information has been provided and is correct
         */
        private void validate()
        {
            if (storeRef == null)
            {
                throw new ExportException("Store to export from has not been specified.");
            }
            if (packageName == null)
            {
                throw new ExportException("Package name has not been specified.");
            }
            if (destDir != null)
            {
                File fileDestDir = new File(destDir);
                if (fileDestDir.exists() == false)
                {
                    throw new ExportException("Destination directory " + fileDestDir.getAbsolutePath() + " does not exist.");
                }
            }
        }

        /**
         * Get the location within the Repository to export from
         * 
         * @return the location
         */
        private Location getLocation()
        {
            Location location = new Location(storeRef);
            location.setPath(path);
            return location;
        }

        /**
         * Get the destination directory
         * 
         * @return the destination directory (or null if current directory)
         */
        private File getDestDir()
        {
            File dir = (destDir == null) ? null : new File(destDir); 
            return dir;
        }

        /**
         * Get the package directory
         * 
         * @return the package directory within the destination directory
         */
        private File getPackageDir()
        {
            File dir = null;
            if (packageDir != null)
            {
                dir = new File(packageDir);
            }
            else if (packageName.indexOf('.') != -1)
            {
                dir = new File(packageName.substring(0, packageName.indexOf('.')));
            }
            else
            {
                dir = new File(packageName); 
            }
            return dir;
        }

        /**
         * Get the xml export file
         * 
         * @return the package file
         */
        private File getPackageFile()
        {
            String packageFile = (packageName.indexOf('.') != -1) ? packageName : packageName + ".xml";
            File file = new File(getDestDir(), packageFile); 
            return file;
        }
    }

    
    /**
     * Report Export Progress
     * 
     * @author David Caruana
     */
    private class ExportProgress
        implements Exporter
    {
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#start()
         */
        public void start()
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#startNamespace(java.lang.String, java.lang.String)
         */
        public void startNamespace(String prefix, String uri)
        {
            logVerbose("Exporting namespace " + uri + " (prefix: " + prefix + ")");
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#endNamespace(java.lang.String)
         */
        public void endNamespace(String prefix)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#startNode(org.alfresco.service.cmr.repository.NodeRef)
         */
        public void startNode(NodeRef nodeRef)
        {
            logVerbose("Exporting node " + nodeRef.toString());
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#endNode(org.alfresco.service.cmr.repository.NodeRef)
         */
        public void endNode(NodeRef nodeRef)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#startAspect(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void startAspect(NodeRef nodeRef, QName aspect)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#endAspect(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void endAspect(NodeRef nodeRef, QName aspect)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#startProperty(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void startProperty(NodeRef nodeRef, QName property)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#endProperty(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void endProperty(NodeRef nodeRef, QName property)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#value(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.io.Serializable)
         */
        public void value(NodeRef nodeRef, QName property, Object value)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#content(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.io.InputStream)
         */
        public void content(NodeRef nodeRef, QName property, InputStream content)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#startAssoc(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void startAssoc(NodeRef nodeRef, QName assoc)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#endAssoc(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void endAssoc(NodeRef nodeRef, QName assoc)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#warning(java.lang.String)
         */
        public void warning(String warning)
        {
            log("Warning: " + warning);            
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.Exporter#end()
         */
        public void end()
        {
        }
    }

    
    /**
     * Export Tool Exception
     * 
     * @author David Caruana
     */
    private static class ExportException extends RuntimeException
    {
        private static final long serialVersionUID = 3257008761007847733L;

        /*package*/ ExportException(String msg)
        {
            super(msg);
        }

        /*package*/ ExportException(String msg, Throwable cause)
        {
            super(msg, cause);
        }
    }

}
