package org.alfresco.jcr.importer;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.jcr.test.BaseJCRTest;
import org.springframework.core.io.ClassPathResource;

public class SysViewImportTest extends BaseJCRTest
{
    protected Session superuserSession;

    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        SimpleCredentials superuser = new SimpleCredentials("superuser", "".toCharArray());
        superuserSession = repository.login(superuser, getWorkspace());
    }
    
    @Override
    protected void onTearDownInTransaction()
    {
        superuserSession.logout();
        super.onTearDownInTransaction();
    }

    public void testDummy()
    {
    }
    
    public void xtestImport()
        throws Exception
    {
        ClassPathResource sysview = new ClassPathResource("org/alfresco/jcr/test/sysview.xml");
        superuserSession.importXML("/testroot", sysview.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
    }

}
