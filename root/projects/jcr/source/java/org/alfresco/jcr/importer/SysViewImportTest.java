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
        super.onTearDownInTransaction();
        superuserSession.logout();
    }
    
    public void testImport()
        throws Exception
    {
        ClassPathResource sysview = new ClassPathResource("org/alfresco/jcr/test/sysview.xml");
        superuserSession.importXML("/testroot", sysview.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
        
        // TODO: Perform import tests
        
        
        //setComplete();
    }

}
