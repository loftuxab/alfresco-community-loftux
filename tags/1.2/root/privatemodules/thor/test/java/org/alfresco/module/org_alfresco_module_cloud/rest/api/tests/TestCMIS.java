/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.CMISDispatcherRegistry.Binding;
import org.alfresco.repo.activities.ActivityType;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.repo.model.filefolder.HiddenAspect.Visibility;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.rest.api.tests.PersonInfo;
import org.alfresco.rest.api.tests.RepoService.SiteInformation;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;
import org.alfresco.rest.api.tests.RepoService.TestPerson;
import org.alfresco.rest.api.tests.RepoService.TestSite;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.CmisSession;
import org.alfresco.rest.api.tests.client.PublicApiClient.Comments;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.Nodes;
import org.alfresco.rest.api.tests.client.PublicApiClient.Paging;
import org.alfresco.rest.api.tests.client.PublicApiClient.People;
import org.alfresco.rest.api.tests.client.PublicApiClient.Sites;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Activity;
import org.alfresco.rest.api.tests.client.data.CMISNode;
import org.alfresco.rest.api.tests.client.data.Comment;
import org.alfresco.rest.api.tests.client.data.FolderNode;
import org.alfresco.rest.api.tests.client.data.MemberOfSite;
import org.alfresco.rest.api.tests.client.data.NodeRating;
import org.alfresco.rest.api.tests.client.data.NodeRating.Aggregate;
import org.alfresco.rest.api.tests.client.data.Person;
import org.alfresco.rest.api.tests.client.data.SiteRole;
import org.alfresco.rest.api.tests.client.data.Tag;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.PublicAPILuceneTestsCategory;
import org.alfresco.util.FileFilterMode.Client;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import org.alfresco.util.TempFileProvider;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 *
 * @author steveglover
 *
 */
@Category(PublicAPILuceneTestsCategory.class)
public class TestCMIS extends CloudTestApi
{
	private String getBareObjectId(String objectId)
	{
		int idx = objectId.indexOf(";");
		String bareObjectId = null;
		if(idx != -1)
		{
			bareObjectId = objectId.substring(0, idx);
		}
		else
		{
			bareObjectId = objectId;
		}

		return bareObjectId;
	}

	/**
	 * Tests OpenCMIS API.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCMIS() throws Exception
	{
		// Test Case cloud-2353
		// Test Case cloud-2354
		// Test Case cloud-2356
		// Test Case cloud-2378
		// Test Case cloud-2357
		// Test Case cloud-2358
		// Test Case cloud-2360

		final TestNetwork network1 = getTestFixture().getRandomNetwork();
		Iterator<String> personIt = network1.getPersonIds().iterator();
    	final String personId = personIt.next();
    	assertNotNull(personId);
		Person person = repoService.getPerson(personId);
		assertNotNull(person);

		// Create a site
    	final TestSite site = TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
		{
			@Override
			public TestSite doWork() throws Exception
			{
				String siteName = "site" + System.currentTimeMillis();
				SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
				TestSite site = network1.createSite(siteInfo);
				return site;
			}
		}, personId, network1.getId());

		publicApiClient.setRequestContext(new RequestContext(network1.getId(), personId));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());
		Nodes nodesProxy = publicApiClient.nodes();
		Comments commentsProxy = publicApiClient.comments();
		People peopleProxy = publicApiClient.people();

		class ExpectedActivity
		{
			private String activityType;
			private String objectId;
			
			public ExpectedActivity(String activityType, String objectId)
			{
				super();
				this.activityType = activityType;
				this.objectId = objectId;
			}
			
			@SuppressWarnings("unused")
			public String getActivityType()
			{
				return activityType;
			}
			
			@SuppressWarnings("unused")
			public String getObjectId()
			{
				return objectId;
			}

			@Override
			public int hashCode()
			{
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((activityType == null) ? 0 : activityType.hashCode());
				result = prime * result
						+ ((objectId == null) ? 0 : objectId.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj)
			{
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				ExpectedActivity other = (ExpectedActivity) obj;
				if (activityType == null) {
					if (other.activityType != null)
						return false;
				} else if (!activityType.equals(other.activityType))
					return false;
				if (objectId == null) {
					if (other.objectId != null)
						return false;
				} else if (!objectId.equals(other.objectId))
					return false;
				return true;
			}

			@Override
			public String toString()
			{
				return "ExpectedActivity [activityType=" + activityType
						+ ", objectId=" + objectId + "]";
			}
		}

		String expectedContent = "Ipsum and so on";
		Document doc = null;
		Folder documentLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary");
		FolderNode expectedDocumentLibrary = (FolderNode)CMISNode.createNode(documentLibrary);
		Document testDoc = null;
		Folder testFolder = null;
		FolderNode testFolderNode = null;
		List<ExpectedActivity> expectedActivities = new ArrayList<ExpectedActivity>(40);

		// create some sub-folders and documents
		{
			for(int i = 0; i < 3; i++)
			{
		        Map<String, String> properties = new HashMap<String, String>();
		        {
		        	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		        	properties.put(PropertyIds.NAME, "folder-" + i);
		        }

				Folder f = documentLibrary.createFolder(properties);
				expectedActivities.add(new ExpectedActivity(ActivityType.FOLDER_ADDED, f.getId()));
				FolderNode fn = (FolderNode)CMISNode.createNode(f);
				if(testFolder == null)
				{
					testFolder = f;
					testFolderNode = fn;
				}
				expectedDocumentLibrary.addFolder(fn);

				for(int k = 0; k < 3; k++)
				{
			        properties = new HashMap<String, String>();
			        {
			        	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			        	properties.put(PropertyIds.NAME, "folder-" + k);
			        }

					Folder f1 = f.createFolder(properties);
					expectedActivities.add(new ExpectedActivity(ActivityType.FOLDER_ADDED, f1.getId()));
					
					if(i == 0)
					{
						f1.delete();
						expectedActivities.add(new ExpectedActivity(ActivityType.FOLDER_DELETED, f1.getId()));
					}
				}

				for(int j = 0; j < 3; j++)
				{
					properties = new HashMap<String, String>();
			        {
			        	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			        	properties.put(PropertyIds.NAME, "doc-" + j);
			        }

					ContentStreamImpl fileContent = new ContentStreamImpl();
					{
			            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
			            writer.putContent(expectedContent);
			            ContentReader reader = writer.getReader();
			            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
			            fileContent.setStream(reader.getContentInputStream());
					}

					Document d = f.createDocument(properties, fileContent, VersioningState.MAJOR);
					expectedActivities.add(new ExpectedActivity(ActivityType.FILE_ADDED, d.getId()));
					if(testDoc == null)
					{
						testDoc = d;
					}

					CMISNode childDocument = CMISNode.createNode(d);
					fn.addNode(childDocument);
				}
			}

			for(int i = 0; i < 10; i++)
			{
				Map<String, String> properties = new HashMap<String, String>();
		        {
		        	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		        	properties.put(PropertyIds.NAME, "doc-" + i);
		        }

				ContentStreamImpl fileContent = new ContentStreamImpl();
				{
		            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
		            writer.putContent(expectedContent);
		            ContentReader reader = writer.getReader();
		            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
		            fileContent.setStream(reader.getContentInputStream());
				}

				Document d = documentLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);

				// trick the activities feed generator into generating an activity per operation rather than rolling them up
				repoService.generateFeed();

				expectedActivities.add(new ExpectedActivity(ActivityType.FILE_ADDED, d.getId()));

				if(i == 0)
				{
					d.delete();
					expectedActivities.add(new ExpectedActivity(ActivityType.FILE_DELETED, d.getId()));
				}
				else if(i == 1)
				{
			        String name = GUID.generate();
			        properties = new HashMap<String, String>();
			        properties.put(PropertyIds.NAME, name);
			        d.updateProperties(properties);
					expectedActivities.add(new ExpectedActivity(ActivityType.FILE_UPDATED, d.getId()));
				}
			}
		}

		repoService.generateFeed();

		int skipCount = 0;
		int maxItems = 100;
		int numMatches = 0;
		Paging paging = getPaging(skipCount, maxItems);
		ListResponse<Activity> activities = peopleProxy.getActivities(personId, createParams(paging, null));
		for(Activity activity : activities.getList())
		{
			String activityType = activity.getActivityType();
			Map<String, Object> summary = activity.getSummary();
			String objectId = (String)summary.get("objectId");
			ExpectedActivity expectedActivity = new ExpectedActivity(activityType, objectId);
			expectedActivities.remove(expectedActivity);
		}
		for(ExpectedActivity expectedActivity : expectedActivities)
		{
			log(expectedActivity.toString());
		}
		assertEquals(0, numMatches);

		// try to add and remove ratings, comments, tags to folders created by CMIS
		{
			Aggregate aggregate = new Aggregate(1, null);
			NodeRating expectedNodeRating = new NodeRating("likes", true, aggregate);
			Comment expectedComment = new Comment("commenty", "commenty", false, null, person, person);
			Tag expectedTag = new Tag("taggy");

			NodeRating rating = nodesProxy.createNodeRating(testFolder.getId(), expectedNodeRating);
			expectedNodeRating.expected(rating);
			assertNotNull(rating.getId());
			
			Tag tag = nodesProxy.createNodeTag(testFolder.getId(), expectedTag);
			expectedTag.expected(tag);
			assertNotNull(tag.getId());

			Comment comment = commentsProxy.createNodeComment(testFolder.getId(), expectedComment);
			expectedComment.expected(comment);
			assertNotNull(comment.getId());
		}

		// try to add and remove ratings, comments, tags to documents created by CMIS
		{
			Aggregate aggregate = new Aggregate(1, null);
			NodeRating expectedNodeRating = new NodeRating("likes", true, aggregate);
			Comment expectedComment = new Comment("commenty", "commenty", false, null, person, person);
			Tag expectedTag = new Tag("taggy");

			NodeRating rating = nodesProxy.createNodeRating(testDoc.getId(), expectedNodeRating);
			expectedNodeRating.expected(rating);
			assertNotNull(rating.getId());

			Tag tag = nodesProxy.createNodeTag(testDoc.getId(), expectedTag);
			expectedTag.expected(tag);
			assertNotNull(tag.getId());

			Comment comment = commentsProxy.createNodeComment(testDoc.getId(), expectedComment);
			expectedComment.expected(comment);
			assertNotNull(comment.getId());
		}

		// descendants
		{
			List<Tree<FileableCmisObject>> descendants = documentLibrary.getDescendants(4);
			expectedDocumentLibrary.checkDescendants(descendants);
		}

		// upload/setContent
		{
	        Map<String, String> fileProps = new HashMap<String, String>();
	        {
	            fileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
	            fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
	        }
			ContentStreamImpl fileContent = new ContentStreamImpl();
			{
	            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
	            writer.putContent(expectedContent);
	            ContentReader reader = writer.getReader();
	            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	            fileContent.setStream(reader.getContentInputStream());
			}
			doc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);

			String nodeId = stripCMISSuffix(doc.getId());
			final NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
			ContentReader reader = TenantUtil.runAsUserTenant(new TenantRunAsWork<ContentReader>()
			{
				@Override
				public ContentReader doWork() throws Exception
				{
					ContentReader reader = repoService.getContent(nodeRef, ContentModel.PROP_CONTENT);
					return reader;
				}
			}, personId, network1.getId());

			String actualContent = reader.getContentString();
			assertEquals(expectedContent, actualContent);
		}

		// get content
		{
			ContentStream stream = doc.getContentStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(stream.getStream(), writer, "UTF-8");
			String actualContent = writer.toString();
			assertEquals(expectedContent, actualContent);
		}

		// get children
		{
			Folder folder = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary/" + testFolder.getName());

			ItemIterable<CmisObject> children = folder.getChildren();
			testFolderNode.checkChildren(children);
		}

		// query
		{
			Folder folder = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary/" + testFolder.getName());
			String folderId = folder.getId();

			Set<String> expectedFolderNames = new HashSet<String>();
			for(CMISNode n : testFolderNode.getFolderNodes().values())
			{
				expectedFolderNames.add((String)n.getProperty("cmis:name"));
			}
			int expectedNumFolders = expectedFolderNames.size();
			int numMatchingFoldersFound = 0;
			List<CMISNode> results = cmisSession.query("SELECT * FROM cmis:folder where IN_TREE('" + folderId + "')", false, 0, Integer.MAX_VALUE);
			for(CMISNode node : results)
			{
				String name = (String)node.getProperties().get("cmis:name");
				if(expectedFolderNames.contains(name))
				{
					numMatchingFoldersFound++;
				}
			}
			assertEquals(expectedNumFolders, numMatchingFoldersFound);

			Set<String> expectedDocNames = new HashSet<String>();
			for(CMISNode n : testFolderNode.getDocumentNodes().values())
			{
				expectedDocNames.add((String)n.getProperty("cmis:name"));
			}
			int expectedNumDocs = expectedDocNames.size();
			int numMatchingDocsFound = 0;
			results = cmisSession.query("SELECT * FROM cmis:document where IN_TREE('" + folderId + "')", false, 0, Integer.MAX_VALUE);
			for(CMISNode node : results)
			{
				String name = (String)node.getProperties().get("cmis:name");
				if(expectedDocNames.contains(name))
				{
					numMatchingDocsFound++;
				}
			}
			assertEquals(expectedNumDocs, numMatchingDocsFound);
		}

		// versioning
		{
			String nodeId = stripCMISSuffix(doc.getId());
			final NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);

			// checkout
			ObjectId pwcId = doc.checkOut();
            Document pwc = (Document)cmisSession.getObject(pwcId.getId());
			Boolean isCheckedOut = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
			{
				@Override
				public Boolean doWork() throws Exception
				{
					Boolean isCheckedOut = repoService.isCheckedOut(nodeRef);
					return isCheckedOut;
				}
			}, personId, network1.getId());
			assertTrue(isCheckedOut);

			// checkin with new content
			expectedContent = "Big bad wolf";

			ContentStreamImpl fileContent = new ContentStreamImpl();
			{
	            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
	            writer.putContent(expectedContent);
	            ContentReader reader = writer.getReader();
	            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	            fileContent.setStream(reader.getContentInputStream());
			}
			ObjectId checkinId = pwc.checkIn(true, Collections.EMPTY_MAP, fileContent, "checkin 1");
			doc = (Document)cmisSession.getObject(checkinId.getId());
			isCheckedOut = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
			{
				@Override
				public Boolean doWork() throws Exception
				{
					Boolean isCheckedOut = repoService.isCheckedOut(nodeRef);
					return isCheckedOut;
				}
			}, personId, network1.getId());
			assertFalse(isCheckedOut);

			// check that the content has been updated
			ContentStream stream = doc.getContentStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(stream.getStream(), writer, "UTF-8");
			String actualContent = writer.toString();
			assertEquals(expectedContent, actualContent);
			
			List<Document> allVersions = doc.getAllVersions();
			assertEquals(2, allVersions.size());
			assertEquals("2.0", allVersions.get(0).getVersionLabel());
			assertEquals("1.0", allVersions.get(1).getVersionLabel());
		}
		
		{
			// https://issues.alfresco.com/jira/browse/PUBLICAPI-95
			// Test that documents are created with autoVersion=true

	        Map<String, String> fileProps = new HashMap<String, String>();
	        {
	            fileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
	            fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
	        }
			ContentStreamImpl fileContent = new ContentStreamImpl();
			{
	            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
	            writer.putContent("Ipsum and so on");
	            ContentReader reader = writer.getReader();
	            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	            fileContent.setStream(reader.getContentInputStream());
			}

			{
				// a versioned document
				
				Document autoVersionedDoc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);
				String objectId = autoVersionedDoc.getId();
				String bareObjectId = getBareObjectId(objectId);
				final NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, bareObjectId);
				Boolean autoVersion = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
				{
					@Override
					public Boolean doWork() throws Exception
					{
						Boolean autoVersion = (Boolean)repoService.getProperty(nodeRef, ContentModel.PROP_AUTO_VERSION);
						return autoVersion;
					}
				}, personId, network1.getId());
				assertEquals(Boolean.FALSE, autoVersion);
			}

			// https://issues.alfresco.com/jira/browse/PUBLICAPI-92
			// Test that a get on an objectId without a version suffix returns the current version of the document
			{
				// do a few checkout, checkin cycles to create some versions
				fileProps = new HashMap<String, String>();
		        {
		            fileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		            fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
		        }

				Document autoVersionedDoc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);
				String objectId = autoVersionedDoc.getId();
				String bareObjectId = getBareObjectId(objectId);

				for(int i = 0; i < 3; i++)
				{
					Document doc1 = (Document)cmisSession.getObject(bareObjectId);

					ObjectId pwcId = doc1.checkOut();
		            Document pwc = (Document)cmisSession.getObject(pwcId.getId());
		            
					ContentStreamImpl contentStream = new ContentStreamImpl();
					{
			            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
			            expectedContent = GUID.generate();
			            writer.putContent(expectedContent);
			            ContentReader reader = writer.getReader();
			            contentStream.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
			            contentStream.setStream(reader.getContentInputStream());
					}
		            pwc.checkIn(true, Collections.EMPTY_MAP, contentStream, "checkin " + i);
				}
				
				// get the object, supplying an objectId without a version suffix
				Document doc1 = (Document)cmisSession.getObject(bareObjectId);
				String versionLabel = doc1.getVersionLabel();
				ContentStream cs = doc1.getContentStream();
				String content = IOUtils.toString(cs.getStream());
				
				assertEquals("4.0", versionLabel);
				assertEquals(expectedContent, content);
			}
		}
	}

	/**
	 * Tests CMIS and non-CMIS public api interactions
	 */
	@Test
	public void testScenario1() throws Exception
	{
		final TestNetwork network1 = getTestFixture().getRandomNetwork();
		Iterator<String> personIt = network1.getPersonIds().iterator();
    	final String person = personIt.next();
    	assertNotNull(person);

		Sites sitesProxy = publicApiClient.sites();
		Comments commentsProxy = publicApiClient.comments();

		publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());

		ListResponse<MemberOfSite> sites = sitesProxy.getPersonSites(person, null);
		assertTrue(sites.getList().size() > 0);
		MemberOfSite siteMember = sites.getList().get(0);
		String siteId = siteMember.getSite().getSiteId();

		Folder documentLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteId + "/documentLibrary");
		
		System.out.println("documentLibrary id = " + documentLibrary.getId());

        Map<String, String> fileProps = new HashMap<String, String>();
        {
            fileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
            fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
        }
		ContentStreamImpl fileContent = new ContentStreamImpl();
		{
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
		}
		Document doc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);

		System.out.println("Document id = " + doc.getId());

		Comment c = commentsProxy.createNodeComment(doc.getId(), new Comment("comment title 1", "comment 1"));
		
		System.out.println("Comment = " + c);
	}
	
	//@Test
	public void testInvalidMethods() throws Exception
	{
		final TestNetwork network1 = getTestFixture().getRandomNetwork();
		Iterator<String> personIt = network1.getPersonIds().iterator();
    	final String person = personIt.next();
    	assertNotNull(person);

		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));

			publicApiClient.post(Binding.atom, "1.0", null, null);
			
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
			
			publicApiClient.head(Binding.atom, "1.0", null, null);
			
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
			
			publicApiClient.options(Binding.atom, "1.0", null, null);
			
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
			
			publicApiClient.trace(Binding.atom, "1.0", null, null);
			
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
			
			publicApiClient.patch(Binding.atom, "1.0", null, null);
			
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
	}
	
	@Test
	public void testPublicApi110() throws Exception
	{
		Iterator<TestNetwork> networksIt = getTestFixture().networksIterator();
		final TestNetwork network1 = networksIt.next();
		Iterator<String> personIt = network1.getPersonIds().iterator();
    	final String person1Id = personIt.next();
    	final String person2Id = personIt.next();

    	final List<NodeRef> nodes = new ArrayList<NodeRef>(5);
    	
    	// Create some favourite targets, sites, files and folders
    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
		{
			@Override
			public Void doWork() throws Exception
			{
				String siteName1 = "site" + GUID.generate();
				SiteInformation siteInfo1 = new SiteInformation(siteName1, siteName1, siteName1, SiteVisibility.PUBLIC);
				TestSite site1 = network1.createSite(siteInfo1);
				
				String siteName2 = "site" + GUID.generate();
				SiteInformation siteInfo2 = new SiteInformation(siteName2, siteName2, siteName2, SiteVisibility.PRIVATE);
				TestSite site2 = network1.createSite(siteInfo2);

				NodeRef nodeRef1 = repoService.createDocument(site1.getContainerNodeRef("documentLibrary"), "Test Doc1", "Test Doc1 Title", "Test Doc1 Description", "Test Content");
				nodes.add(nodeRef1);
				NodeRef nodeRef2 = repoService.createDocument(site1.getContainerNodeRef("documentLibrary"), "Test Doc2", "Test Doc2 Title", "Test Doc2 Description", "Test Content");
				nodes.add(nodeRef2);
				NodeRef nodeRef3 = repoService.createDocument(site2.getContainerNodeRef("documentLibrary"), "Test Doc2", "Test Doc2 Title", "Test Doc2 Description", "Test Content");
				nodes.add(nodeRef3);
				repoService.createAssociation(nodeRef2, nodeRef1, ContentModel.ASSOC_ORIGINAL);
				repoService.createAssociation(nodeRef3, nodeRef1, ContentModel.ASSOC_ORIGINAL);

				site1.inviteToSite(person2Id, SiteRole.SiteCollaborator);

				return null;
			}
		}, person1Id, network1.getId());

		{
			OperationContext cmisOperationCtxOverride = new OperationContextImpl();
			cmisOperationCtxOverride.setIncludeRelationships(IncludeRelationships.BOTH);
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person2Id, cmisOperationCtxOverride));
			CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());

			CmisObject o1 = cmisSession.getObject(nodes.get(0).getId());
			List<Relationship> relationships = o1.getRelationships();
			assertEquals(1, relationships.size());
			Relationship r = relationships.get(0);
			CmisObject source = r.getSource();
			CmisObject target = r.getTarget();
			String sourceVersionSeriesId = (String)source.getProperty(PropertyIds.VERSION_SERIES_ID).getFirstValue();
			String targetVersionSeriesId = (String)target.getProperty(PropertyIds.VERSION_SERIES_ID).getFirstValue();
			assertEquals(nodes.get(1).getId(), sourceVersionSeriesId);
			assertEquals(nodes.get(0).getId(), targetVersionSeriesId);
		}
	}

    /**
     * Test suits for ACE-2355
     */
    @Test
    public void testPUBLICAPI157() throws Exception
    {
        Iterator<TestNetwork> networksIt = getTestFixture().networksIterator();
        final TestNetwork network1 = networksIt.next();
        final TestNetwork network2 = networksIt.next();
        assertNotNull(network1);
        assertNotNull(network2);

        Iterator<TestPerson> personIt = network1.getPeople().iterator();
        final TestPerson person1 = personIt.next();
        assertNotNull(person1);

        // create a user in more than one network
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            public Void execute() throws Throwable
            {
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
                network2.inviteUser(person1.getId());
                AuthenticationUtil.clearCurrentSecurityContext();
                return null;
            }
        }, false, true);


        publicapi157work("1.0", network1, network2, person1);
        publicapi157work("1.1", network1, network2, person1);
    }

    private void publicapi157work(String endPointVersion, TestNetwork network1, TestNetwork network2, TestPerson person1) throws Exception
    {
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
        List<String> networkdIds = publicApiClient.getNetworkIds(endPointVersion);
        assertEquals(2, networkdIds.size());

        // A GET on a specific networkId should return the service document only for that networkId
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
        String url = httpClient.getPublicApiCmisUrlSuffix(network1.getId(), Binding.atom, endPointVersion, null);
        HttpResponse resp = publicApiClient.get(url, null);
        String response = resp.getResponse();
        assertTrue("The response should contain " + network1, response.contains(network1.getId()));
        assertFalse("The response should not contain " + network2, response.contains(network2.getId()));

        // Check again with network2
        publicApiClient.setRequestContext(new RequestContext(network2.getId(), person1.getId()));
        url = httpClient.getPublicApiCmisUrlSuffix(network2.getId(), Binding.atom, endPointVersion, null);
        resp = publicApiClient.get(url, null);
        response = resp.getResponse();
        assertTrue("The response should contain " + network2, response.contains(network2.getId()));
        assertFalse("The response should not contain " + network1, response.contains(network1.getId()));
    }

	@Test
	public void testLargeUpload() throws Exception
	{
		final TestNetwork network1 = getTestFixture().getRandomNetwork();
		Iterator<TestPerson> personIt = network1.getPeople().iterator();
    	final TestPerson person = personIt.next();
		assertNotNull(person);
    	final String personId = person.getId();
    	assertNotNull(personId);

		// Create a site
    	final TestSite site = TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
		{
			@Override
			public TestSite doWork() throws Exception
			{
				String siteName = "site" + System.currentTimeMillis();
				SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
				TestSite site = network1.createSite(siteInfo);
				return site;
			}
		}, personId, network1.getId());

		publicApiClient.setRequestContext(new RequestContext(network1.getId(), personId));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());
		Folder documentLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary");

		// create a big file to upload
        File bigFile = TempFileProvider.createTempFile(GUID.generate(), ".txt");
		RandomAccessFile raf = new RandomAccessFile(bigFile.getAbsolutePath(), "rw");
		try
		{
			raf.setLength(1024 * 1024 * 8); // 8mb
		}
		finally
		{
			raf.close();
		}

		Map<String, String> properties = new HashMap<String, String>();
        {
        	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        	properties.put(PropertyIds.NAME, "doc-" + GUID.generate());
        }

		ContentStreamImpl fileContent = new ContentStreamImpl();
		{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(bigFile));
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(in);
		}

		documentLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
	}

	/*
     * Test that a hidden aspect with cascade == true applied to a folder determines if children created in the folder
     * are hidden too and no activities are generated.
     */
    @Test
    public void testHiddenNodes() throws Exception
    {
    	final String networkId = GUID.generate();

        Pair<TestNetwork, TestPerson> pair = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Pair<TestNetwork, TestPerson>>()
        {
        	@SuppressWarnings("synthetic-access")
        	public Pair<TestNetwork, TestPerson> execute() throws Throwable
        	{
        		try
        		{
        			AuthenticationUtil.pushAuthentication();
        	        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        	        
        	        TestNetwork network = repoService.createNetworkWithAlias(networkId, true);
        	        network.create();

    				TestPerson person = network.createUser();

    				return new Pair<TestNetwork, TestPerson>(network, person);
        		}
	    		finally
	    		{
	    			AuthenticationUtil.popAuthentication();
	    		}
        	}
        }, false, true);

    	final TestNetwork network = pair.getFirst();
    	final TestPerson person = pair.getSecond();

		People peopleProxy = publicApiClient.people();

		// create a test site
		TestSite site = TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
        {
			@Override
			public TestSite doWork() throws Exception
			{
				String shortName = GUID.generate();
				SiteInformation siteInfo = new SiteInformation(shortName, shortName, shortName, SiteVisibility.PUBLIC);
				TestSite site = network.createSite(siteInfo);

	            return site;
			}
        }, person.getId(), network.getId());

		Set<String> hiddenObjectIds = new HashSet<String>();
		Set<String> visibleObjectIds = new HashSet<String>();

    	// Create some folders and documents and check that they have the hidden aspect but are still visible to CMIS

		publicApiClient.setRequestContext(new RequestContext(network.getId(), person.getId()));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());

		AlfrescoFolder docLibrary = (AlfrescoFolder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary");

    	// create a folder and apply the hidden aspect, with cascade
    	String name = GUID.generate();
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        int visibilityMask = repoService.getClientVisibilityMask(Client.cmis, Visibility.Visible);
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder,P:sys:hidden");
        properties.put("sys:clientVisibilityMask", visibilityMask);
        properties.put("sys:cascadeHidden", Boolean.TRUE);
        properties.put("sys:cascadeIndexControl", Boolean.TRUE);
        properties.put("sys:clientControlled", Boolean.TRUE);
    	Folder hiddenParentFolder = cmisSession.createFolder(docLibrary.getId(), name, properties);
    	final NodeRef hiddenParentFolderNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(hiddenParentFolder.getId()));

    	// create a folder that is not hidden
        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
    	Folder notHiddenParentFolder = cmisSession.createFolder(docLibrary.getId(), name, properties);
    	final NodeRef notHiddenParentFolderNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(notHiddenParentFolder.getId()));

		// create children...
        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
    	Folder folder1 = cmisSession.createFolder(hiddenParentFolderNodeRef.getId(), name, properties);
    	final NodeRef folder1NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(folder1.getId()));

        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        Document file1 = cmisSession.createDocument(hiddenParentFolderNodeRef.getId(), name, properties, null, VersioningState.MAJOR);
        final NodeRef file1NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(file1.getId()));

        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        Document file2 = cmisSession.createDocument(hiddenParentFolderNodeRef.getId(), name, properties, null, VersioningState.MAJOR);
        final NodeRef file2NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(file2.getId()));

        // Folder with hidden aspect applied under a non-hidden parent folder
        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder,P:sys:hidden");
        int clientVisibilityMask = repoService.getClientVisibilityMask(Client.cmis, Visibility.Visible);
        properties.put(repoService.toPrefixString(ContentModel.PROP_VISIBILITY_MASK), clientVisibilityMask);
        properties.put(repoService.toPrefixString(ContentModel.PROP_CLIENT_CONTROLLED), Boolean.TRUE);
        Folder folder2 = cmisSession.createFolder(notHiddenParentFolderNodeRef.getId(), name, properties);
        final NodeRef folder2NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(folder2.getId()));

        // Folder without hidden aspect applied under a non-hidden parent folder
        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        Folder folder3 = cmisSession.createFolder(notHiddenParentFolderNodeRef.getId(), name, properties);
        final NodeRef folder3NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(folder3.getId()));

        // File without hidden aspect applied under a non-hidden parent folder
        name = GUID.generate();
		ContentStreamImpl fileContent = new ContentStreamImpl();
		{
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
		}
        properties = new HashMap<String, Serializable>();
        Document file3 = cmisSession.createDocument(notHiddenParentFolderNodeRef.getId(), name, properties, fileContent, VersioningState.MAJOR);
        final NodeRef file3NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(file3.getId()));

    	// check that these children have been hidden to any client other than CMIS...
		TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
		        assertEquals(Visibility.NotVisible, repoService.getVisibility(Client.script, hiddenParentFolderNodeRef));
		    	assertEquals(Visibility.NotVisible, repoService.getVisibility(Client.script, folder1NodeRef));
		        assertEquals(Visibility.NotVisible, repoService.getVisibility(Client.script, file1NodeRef));
		        assertEquals(Visibility.NotVisible, repoService.getVisibility(Client.script, file2NodeRef));

		        assertEquals(Visibility.Visible, repoService.getVisibility(Client.cmis, hiddenParentFolderNodeRef));
		    	assertEquals(Visibility.Visible, repoService.getVisibility(Client.cmis, folder1NodeRef));
		        assertEquals(Visibility.Visible, repoService.getVisibility(Client.cmis, file1NodeRef));
		        assertEquals(Visibility.Visible, repoService.getVisibility(Client.cmis, file2NodeRef));

		        return null;
			}
        }, person.getId(), network.getId());

        // ...but not to CMIS
        Object obj = cmisSession.getObject(folder1.getId());
        assertNotNull(obj);
        obj = cmisSession.getObject(file1.getId());
        assertNotNull(obj);
        obj = cmisSession.getObject(file2.getId());
        assertNotNull(obj);
        obj = cmisSession.getObject(folder2.getId());
        assertNotNull(obj);
        
        FolderNode nodes = cmisSession.getChildren(hiddenParentFolderNodeRef.getId(), 0, Integer.MAX_VALUE);
        nodes.checkChildren(Arrays.asList(folder1.getId(), folder2.getId()), Arrays.asList(file1.getId(), file2.getId()));

        // These were not hidden, so should be visible to CMIS
        obj = cmisSession.getObject(folder3.getId());
        assertNotNull(obj);
        obj = cmisSession.getObject(file3.getId());
        assertNotNull(obj);

        // Check visibility for getChildren
        nodes = cmisSession.getChildren(notHiddenParentFolderNodeRef.getId(), 0, Integer.MAX_VALUE);
        nodes.checkChildren(Arrays.asList(folder3.getId()), Arrays.asList(file3.getId()));

        // updates...
        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        properties.put(PropertyIds.NAME, name);
        folder1.updateProperties(properties);
        
        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        properties.put(PropertyIds.NAME, name);
        file1.updateProperties(properties);

        name = GUID.generate();
        properties = new HashMap<String, Serializable>();
        properties.put(PropertyIds.NAME, name);
        file2.updateProperties(properties);

        // deletes...
        file1.delete(true);
        file2.delete(true);
        folder1.deleteTree(true, UnfileObject.DELETE, false);

        hiddenObjectIds.add(hiddenParentFolder.getId());
        hiddenObjectIds.add(folder1.getId());
		hiddenObjectIds.add(file1.getId());
		hiddenObjectIds.add(file2.getId());
		hiddenObjectIds.add(folder2NodeRef.getId());

		visibleObjectIds.add(notHiddenParentFolder.getId());
		visibleObjectIds.add(folder3NodeRef.getId());
		visibleObjectIds.add(file3NodeRef.getId());
		
        // check that they do not appear in the activity feeds

		repoService.generateFeed();

		publicApiClient.setRequestContext(new RequestContext(network.getId(), person.getId()));

		Set<String> activityTypes = new HashSet<String>();
		activityTypes.add(ActivityType.FILE_ADDED);
		activityTypes.add(ActivityType.FILES_ADDED);
		activityTypes.add(ActivityType.FILE_UPDATED);
		activityTypes.add(ActivityType.FILES_UPDATED);
		activityTypes.add(ActivityType.FILE_DELETED);
		activityTypes.add(ActivityType.FILES_DELETED);
		activityTypes.add(ActivityType.FOLDER_ADDED);
		activityTypes.add(ActivityType.FOLDERS_ADDED);			
		activityTypes.add(ActivityType.FOLDER_DELETED);
		activityTypes.add(ActivityType.FOLDERS_DELETED);		    

		int skipCount = 0;
		int maxItems = 100;
		Paging paging = getPaging(skipCount, maxItems);
		ListResponse<Activity> activities = peopleProxy.getActivities(person.getId(), createParams(paging, null));
		for(Activity activity : activities.getList())
		{
			String activityType = activity.getActivityType();
			if(activityTypes.contains(activityType))
			{
				Map<String, Object> summary = activity.getSummary();
				String objectId = getBareObjectId((String)summary.get("objectId"));
				visibleObjectIds.remove(objectId);
				if(hiddenObjectIds.contains(objectId))
				{
					fail("Unexpected activity for file/folder creation: objectId = " + objectId + ", activityType = " + activityType);
				}
			}
		}

		assertEquals("Unable to find expected activity", 0, visibleObjectIds.size());
    }
    
    @Test
    public void testClientControlledHiddenAspect()
    {
    	final String networkId = GUID.generate();
    	final String userId = "bob.jones";

        Pair<TestNetwork, TestPerson> pair = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Pair<TestNetwork, TestPerson>>()
        {
        	@SuppressWarnings("synthetic-access")
        	public Pair<TestNetwork, TestPerson> execute() throws Throwable
        	{
        		try
        		{
        			AuthenticationUtil.pushAuthentication();
        	        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        	        
        			final TestNetwork network = repoService.createNetworkWithAlias(networkId, true);
        			network.create();

        			TestPerson person = TenantUtil.runAsSystemTenant(new TenantRunAsWork<TestPerson>()
        			{
						@Override
						public TestPerson doWork() throws Exception
						{
	        				PersonInfo personInfo = new CloudPersonInfo("Bob", "Jones", userId, "password", false, null, null, null, null, null, null, null);
	                		TestPerson person = network.createUser(personInfo);
							return person;
						}

        			}, network.getId());

    				return new Pair<TestNetwork, TestPerson>(network, person);
        		}
	    		finally
	    		{
	    			AuthenticationUtil.popAuthentication();
	    		}
        	}
        }, false, true);

    	final TestNetwork network = pair.getFirst();
    	final TestPerson person = pair.getSecond();

    	final String parentFolderName = GUID.generate();
    	final List<TestSite> sites = new ArrayList<TestSite>();
		final List<NodeRef> folders = new ArrayList<NodeRef>();

		// create a parent folder and apply hidden aspect with cascade
		TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				String shortName = GUID.generate();
				SiteInformation siteInfo = new SiteInformation(shortName, shortName, shortName, SiteVisibility.PUBLIC);
				TestSite site = network.createSite(siteInfo);
				sites.add(site);

	        	// create a folder
	            NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef("documentLibrary"), parentFolderName);
	            folders.add(folderNodeRef);

	            return null;
			}
        }, person.getId(), network.getId());

		final TestSite site = sites.get(0);
		final NodeRef parentFolderNodeRef = folders.get(0);

		publicApiClient.setRequestContext(new RequestContext(network.getId(), person.getId()));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());

		// test client controlled hidden aspect through CMIS API
		{
	        String name = GUID.generate();
			ContentStreamImpl contentStream = new ContentStreamImpl();
			{
	            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
	            writer.putContent("Ipsum and so on");
	            ContentReader reader = writer.getReader();
	            contentStream.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	            contentStream.setStream(reader.getContentInputStream());
			}
			Map<String, Serializable> properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:sys:hidden");
			properties.put("sys:clientControlled", Boolean.TRUE);
			AlfrescoDocument doc = (AlfrescoDocument)cmisSession.createDocument(parentFolderNodeRef.getId(), name, properties, contentStream, VersioningState.MAJOR);
			String docId = doc.getId();
			final NodeRef docNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(docId));

			TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
	        {
				@Override
				public Void doWork() throws Exception
				{
					assertTrue(repoService.getAspects(docNodeRef).contains(ContentModel.ASPECT_HIDDEN));
					return null;
				}
	        }, person.getId(), network.getId());

			name = ".hiddenDoc" + GUID.generate();
			properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.NAME, name);
			doc.updateProperties(properties);
			doc = (AlfrescoDocument)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary/" + parentFolderName + "/" + name);

			TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
	        {
				@Override
				public Void doWork() throws Exception
				{
					assertTrue(repoService.getAspects(docNodeRef).contains(ContentModel.ASPECT_HIDDEN));
					return null;
				}
	        }, person.getId(), network.getId());

			name = "hiddenDoc" + GUID.generate();
			properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.NAME, name);
			doc.updateProperties(properties);
			doc = (AlfrescoDocument)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary/" + parentFolderName + "/" + name);

			TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
	        {
				@Override
				public Void doWork() throws Exception
				{
					assertTrue(repoService.getAspects(docNodeRef).contains(ContentModel.ASPECT_HIDDEN));
					return null;
				}
	        }, person.getId(), network.getId());
		}
    }

    /*
     * CLOUD-1682: test that a renamed document that is hidden and clientControlled == true remains hidden
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testRename()
    {
    	final String networkId = GUID.generate();
    	final String userId = "bob.jones";

        Pair<TestNetwork, TestPerson> pair = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Pair<TestNetwork, TestPerson>>()
        {
        	@SuppressWarnings("synthetic-access")
        	public Pair<TestNetwork, TestPerson> execute() throws Throwable
        	{
        		try
        		{
        			AuthenticationUtil.pushAuthentication();
        	        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        	        
        			final TestNetwork network = repoService.createNetworkWithAlias(networkId, true);
        			network.create();

        			TestPerson person = TenantUtil.runAsSystemTenant(new TenantRunAsWork<TestPerson>()
        			{
						@Override
						public TestPerson doWork() throws Exception
						{
	        				PersonInfo personInfo = new CloudPersonInfo("Bob", "Jones", userId, "password", false, null, null, null, null, null, null, null);
	                		TestPerson person = network.createUser(personInfo);
							return person;
						}

        			}, network.getId());

    				return new Pair<TestNetwork, TestPerson>(network, person);
        		}
	    		finally
	    		{
	    			AuthenticationUtil.popAuthentication();
	    		}
        	}
        }, false, true);

    	final TestNetwork network = pair.getFirst();
    	final TestPerson person = pair.getSecond();

    	final String parentFolderName = GUID.generate();
    	final List<TestSite> sites = new ArrayList<TestSite>();
		final List<NodeRef> folders = new ArrayList<NodeRef>();

		// create a parent folder
		TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				String shortName = GUID.generate();
				SiteInformation siteInfo = new SiteInformation(shortName, shortName, shortName, SiteVisibility.PUBLIC);
				TestSite site = network.createSite(siteInfo);
				sites.add(site);

	        	// create a folder
	            NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef("documentLibrary"), parentFolderName);
	            folders.add(folderNodeRef);

	            return null;
			}
        }, person.getId(), network.getId());

		final NodeRef parentFolderNodeRef = folders.get(0);
		String parentFolderId = parentFolderNodeRef.getId();
		publicApiClient.setRequestContext(new RequestContext(network.getId(), person.getId()));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());

		{
			// create 2 hidden nodes using CMIS, one client-controlled, the other not client-controlled
	        String name1 = GUID.generate();
			ContentStreamImpl contentStream = new ContentStreamImpl();
			{
	            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
	            writer.putContent("Ipsum and so on");
	            ContentReader reader = writer.getReader();
	            contentStream.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	            contentStream.setStream(reader.getContentInputStream());
			}
			Map<String, Serializable> properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:sys:hidden");
			properties.put("sys:clientControlled", Boolean.TRUE);
			properties.put("sys:clientVisibilityMask", 32768); // visible to CMIS only
			AlfrescoDocument doc1 = (AlfrescoDocument)cmisSession.createDocument(parentFolderNodeRef.getId(), name1, properties, contentStream, VersioningState.MAJOR);
			String doc1Id = doc1.getId();
			final NodeRef doc1NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(doc1Id));

			String name2 = GUID.generate();
			properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:sys:hidden");
			properties.put("sys:clientVisibilityMask", 32768); // visible to CMIS only
			AlfrescoDocument doc2 = (AlfrescoDocument)cmisSession.createDocument(parentFolderNodeRef.getId(), name2, properties, contentStream, VersioningState.MAJOR);
			String doc2Id = doc2.getId();
			final NodeRef doc2NodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, getBareObjectId(doc2Id));

			// check that the nodes appear in a getChildren listing
			FolderNode folderNode = cmisSession.getChildren(parentFolderId, 0, Integer.MAX_VALUE);
			folderNode.checkChildren(Collections.EMPTY_LIST, Arrays.asList(new String[] { doc1Id, doc2Id }));

			// rename the nodes
			final String newName1 = name1 + "renamed";
			properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.NAME, newName1);
			doc1.updateProperties(properties);

			final String newName2 = name2 + "renamed";
			properties = new HashMap<String, Serializable>();
			properties.put(PropertyIds.NAME, newName2);
			doc2.updateProperties(properties);

			AlfrescoFolder parentFolder = (AlfrescoFolder)cmisSession.getObject(parentFolderId);
			AlfrescoDocument renamedDoc1 = (AlfrescoDocument)cmisSession.getObjectByPath(parentFolder.getPath() + "/" + newName1);
			AlfrescoDocument renamedDoc2 = (AlfrescoDocument)cmisSession.getObjectByPath(parentFolder.getPath() + "/" + newName2);

			// check that doc1 has been renamed and it is still hidden
			assertEquals(newName1, renamedDoc1.getName());
			TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
	        {
				@Override
				public Void doWork() throws Exception
				{
					String actualName = (String)repoService.getProperty(doc1NodeRef, ContentModel.PROP_NAME);
					assertEquals(newName1, actualName);
					
					assertTrue(repoService.getAspects(doc1NodeRef).contains(ContentModel.ASPECT_HIDDEN));
					assertEquals(Visibility.Visible, repoService.getVisibility(Client.cmis, doc1NodeRef));
	
		            return null;
				}
	        }, person.getId(), network.getId());

			// check that doc2 has been renamed and is no longer hidden
			assertEquals(newName2, renamedDoc2.getName());
			TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
	        {
				@Override
				public Void doWork() throws Exception
				{
					String actualName = (String)repoService.getProperty(doc2NodeRef, ContentModel.PROP_NAME);
					assertEquals(newName2, actualName);
					
					assertFalse(repoService.getAspects(doc2NodeRef).contains(ContentModel.ASPECT_HIDDEN));
					assertEquals(Visibility.Visible, repoService.getVisibility(Client.cmis, doc2NodeRef));

		            return null;
				}
	        }, person.getId(), network.getId());

			// check that the node appears in a getChildren listing
			folderNode = cmisSession.getChildren(parentFolderId, 0, Integer.MAX_VALUE);
			folderNode.checkChildren(Collections.EMPTY_LIST, Arrays.asList(doc1Id, doc2Id));
		}
    }
}
