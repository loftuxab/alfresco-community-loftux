package org.alfresco.module.alfnetwork;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.alfresco.repo.importer.ExportSourceImporter;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class LDAPSyncWebScript extends AbstractRepositoryWebScript
{
    public synchronized void execute(WebScriptRequest req, WebScriptResponse res)
        throws IOException
    {
        // the export source importer
        ExportSourceImporter importer = null;
        
        // build the export source
        String command = req.getParameter("command");
        if("user".equalsIgnoreCase(command))
        {
            String userId = req.getParameter("id");
            if(userId != null)
            {
                // get the LDAPSinglePersonExportSource
                LDAPSinglePersonExportSource exportSource = (LDAPSinglePersonExportSource) getApplicationContext().getBean("ldapSinglePersonExportSource");
                
                // set the user id
                exportSource.setUserId(userId);
                
                // get the importer
                importer = (ExportSourceImporter) getApplicationContext().getBean("ldapSinglePersonImport");
            }
        }
        
        if(importer != null)
        {
            try
            {
                importer.doImport();
                res.getWriter().write("Success");
            }
            catch(Exception ex)
            {
                // log out to console
                ex.printStackTrace();
                
                // write out to screen as well (for debug / test)
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                
                ex.printStackTrace(pw);
                pw.flush();
                pw.close();
                
                String trace = sw.toString();
                res.getWriter().write("An exception occurred");
                res.getWriter().write(ex.getMessage());
                res.getWriter().write(trace);
            }
        }
    }    
}
