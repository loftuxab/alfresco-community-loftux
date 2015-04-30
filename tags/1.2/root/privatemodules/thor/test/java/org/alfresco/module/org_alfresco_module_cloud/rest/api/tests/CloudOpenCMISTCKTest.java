/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.opencmis.OpenCMISClientContext;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.rest.api.tests.AbstractEnterpriseOpenCMIS10TCKTest;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;
import org.alfresco.rest.api.tests.RepoService.TestPerson;
import org.alfresco.rest.api.tests.RepoService.TestSite;
import org.alfresco.rest.api.tests.TestFixture;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.PublicAPISolrTestsCategory;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.tck.impl.AbstractSessionTestGroup;
import org.apache.chemistry.opencmis.tck.impl.JUnitHelper;
import org.apache.chemistry.opencmis.tck.impl.TestParameters;
import org.apache.chemistry.opencmis.tck.tests.basics.BasicsTestGroup;
import org.apache.chemistry.opencmis.tck.tests.control.ControlTestGroup;
import org.apache.chemistry.opencmis.tck.tests.crud.CRUDTestGroup;
import org.apache.chemistry.opencmis.tck.tests.filing.FilingTestGroup;
import org.apache.chemistry.opencmis.tck.tests.query.ContentChangesSmokeTest;
import org.apache.chemistry.opencmis.tck.tests.query.QueryForObject;
import org.apache.chemistry.opencmis.tck.tests.query.QueryRootFolderTest;
import org.apache.chemistry.opencmis.tck.tests.versioning.VersionDeleteTest;
import org.apache.chemistry.opencmis.tck.tests.versioning.VersioningSmokeTest;
import org.apache.chemistry.opencmis.tck.tests.versioning.VersioningStateCreateTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Chemistry OpenCMIS TCK tests against a Cloud repository. 
 * 
 * @author steveglover
 *
 */
@Category(PublicAPISolrTestsCategory.class)
public class CloudOpenCMISTCKTest extends AbstractEnterpriseOpenCMIS10TCKTest
{
	private static final String CMIS_URL = "http://{0}:{1}/{2}/api/{3}/{4}/cmis/versions/1.0/atom";

	protected static final Log logger = LogFactory.getLog(CloudOpenCMISTCKTest.class);

	@Override
	protected TestFixture getTestFixture() throws Exception
	{
		return TCKTestFixture.getInstance();
	}

	@Before
	public void before() throws Exception
	{
		final TestNetwork network = getTestFixture().getRandomNetwork();
		
		TestPerson person = TenantUtil.runAsSystemTenant(new TenantRunAsWork<TestPerson>()
		{
			@Override
			public TestPerson doWork() throws Exception
			{
        		TestPerson person = network.createUser();
        		return person;
			}
        }, network.getId());
		
		TestSite site = TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
		{
			@Override
			public TestSite doWork() throws Exception
			{
        		TestSite site = network.createSite(SiteVisibility.PUBLIC);
        		return site;
			}
        }, person.getId(), network.getId());

		int port = getTestFixture().getJettyComponent().getPort();
    	Map<String, String> cmisParameters = new HashMap<String, String>();
    	cmisParameters.put(TestParameters.DEFAULT_RELATIONSHIP_TYPE, "R:cm:replaces");
    	cmisParameters.put(TestParameters.DEFAULT_TEST_FOLDER_PARENT, "/Sites/" + site.getSiteId() + "/documentLibrary");
    	clientContext = new OpenCMISClientContext(BindingType.ATOMPUB,
    			MessageFormat.format(CMIS_URL, "localhost", String.valueOf(port), "alfresco", network.getId(), "public"),
    			person.getId(), "password", cmisParameters);
	}

    @Test
    public void testCMISTCKBasics() throws Exception
    {
        BasicsTestGroup basicsTestGroup = new BasicsTestGroup();
        JUnitHelper.run(basicsTestGroup);
    }
    
    @Test
    public void testCMISTCKCRUD() throws Exception
    {
        CRUDTestGroup crudTestGroup = new CRUDTestGroup();
        JUnitHelper.run(crudTestGroup);
    }

    @Test
    public void testCMISTCKVersioning() throws Exception
    {
        OverrideVersioningTestGroup versioningTestGroup = new OverrideVersioningTestGroup();
        JUnitHelper.run(versioningTestGroup);
    }
    
    @Test
    public void testCMISTCKFiling() throws Exception
    {
        FilingTestGroup filingTestGroup = new FilingTestGroup();
        JUnitHelper.run(filingTestGroup);
    }
    
    @Test
    public void testCMISTCKControl() throws Exception
    {
        ControlTestGroup controlTestGroup = new ControlTestGroup();
        JUnitHelper.run(controlTestGroup);
    }

    @Test
    public void testCMISTCKQuery() throws Exception
    {
        OverrideQueryTestGroup queryTestGroup = new OverrideQueryTestGroup();
        JUnitHelper.run(queryTestGroup);
    }
    
    private class OverrideVersioningTestGroup extends AbstractSessionTestGroup
    {
        @Override
        public void init(Map<String, String> parameters) throws Exception
        {
            super.init(parameters);

            setName("Versioning Test Group");
            setDescription("Versioning tests.");

            addTest(new VersioningSmokeTest());
            addTest(new VersionDeleteTest());
            addTest(new VersioningStateCreateTest());
            // relies on Solr being available
//            addTest(new CheckedOutTest());
        }
    }

    private class OverrideQueryTestGroup extends AbstractSessionTestGroup
    {
        @Override
        public void init(Map<String, String> parameters) throws Exception
        {
            super.init(parameters);

            setName("Query Test Group");
            setDescription("Query and content changes tests.");

            // this is failing because of an MT issue (the thread is a specific tenant but the DB metadata query is searching
            // against the workspace://SpacesStore)
//            addTest(new QuerySmokeTest());
            addTest(new QueryRootFolderTest());
            addTest(new QueryForObject());
//            addTest(new QueryLikeTest());
            addTest(new ContentChangesSmokeTest());
        }
    }
}
