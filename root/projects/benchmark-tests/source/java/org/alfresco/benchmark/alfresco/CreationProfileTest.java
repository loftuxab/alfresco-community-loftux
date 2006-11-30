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
package org.alfresco.benchmark.alfresco;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile.PropertyType;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class CreationProfileTest extends TestCase {
	public static String PROP_NAME = "name";

	public static String PROP_CONTENT = "content";

	public static String PROP_DC_PUBLISHER = "publisher";

	public static String PROP_DC_CONTRIBUTER = "contributer";

	public static String PROP_DC_TYPE = "type";

	public static String PROP_DC_IDENTIFIER = "identifier";

	public static String PROP_DC_DCSOURCE = "dcsource";

	public static String PROP_DC_COVERAGE = "coverage";

	public static String PROP_DC_RIGHTS = "rights";

	public static String PROP_DC_SUBJECT = "subject";

	public static String PROP_DC_AUTHOR = "author";

	public static String PROP_TITLE = "title";

	public static String PROP_DESCRIPTION = "description";

	private static ApplicationContext ctx = ApplicationContextHelper
			.getApplicationContext();

	private static Repository repository = (Repository) ctx
			.getBean("JCR.Repository");

	private static ServiceRegistry serviceRegistry = (ServiceRegistry) ctx
			.getBean("ServiceRegistry");

	private Map<String, Object> contentPropertyValues;

	private static List<PropertyProfile> contentPropertyProfiles;

	private static final int FILES = 1;

	private static final int REPEATS = 4;

	private static final int THREADS = 4;

	public CreationProfileTest() {
		super();
	}

	public CreationProfileTest(String arg0) {
		super(arg0);
	}

	public void setUp() {
		contentPropertyValues = DataProviderComponent.getInstance()
				.getPropertyData(getContentPropertyProfiles());
	}

	public void testJCRCreateContent() {
		Thread runner = null;
		for (int i = 0; i < THREADS; i++) {
			runner = new Nester("Concurrent-" + i, runner, new Job() {

				public void run() {

					for (int i = 0; i < FILES; i++) {
						createJCRContent();
					}

				}

			});
		}
		if (runner != null) {
			runner.start();

			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void testAlfrescoCreateContent() {
		Thread runner = null;
		for (int i = 0; i < THREADS; i++) {
			runner = new Nester("Concurrent-" + i, runner, new Job() {

				public void run() {

					for (int i = 0; i < FILES; i++) {
						createAlfrescoContent();
					}

				}

			});
		}
		if (runner != null) {
			runner.start();

			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void testAlfrescoJCRCreateContent() {

		Thread runner = null;
		for (int i = 0; i < THREADS; i++) {
			runner = new Nester("Concurrent-" + i, runner, new Job() {

				public void run() {

					for (int i = 0; i < FILES; i++) {
						createAlfrescoJCRContent();
					}
				}

			});
		}
		if (runner != null) {
			runner.start();

			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void createAlfrescoContent() {
		try {
			UserTransaction tx = serviceRegistry.getTransactionService()
					.getUserTransaction();
			tx.begin();
			serviceRegistry.getAuthenticationService().authenticate("admin",
					"admin".toCharArray());

			// ContentData contentData =
			// (ContentData)this.contentPropertyValues.get(JackRabbitUtils.PROP_CONTENT);

			// Get the root node and the folder that we are going to create the
			// new node within
			NodeRef rootNodeRef = serviceRegistry.getNodeService().getRootNode(
					new StoreRef("workspace://SpacesStore"));
			NodeRef companyHome = serviceRegistry.getSearchService()
					.selectNodes(rootNodeRef, "./app:company_home", null,
							serviceRegistry.getNamespaceService(), true).get(0);

			try {
				// Create the new file in the folder
				for (int i = 0; i < REPEATS; i++) {
					createAlfrescoFile(contentPropertyValues, companyHome);
				}
			} catch (Exception exception) {
				throw new RepositoryException(exception);
			}

			tx.commit();

		} catch (Throwable exception) {
			throw new RuntimeException("Unable to execute test", exception);
		}
	}

	public void createAlfrescoJCRContent() {
		try {
			UserTransaction tx = serviceRegistry.getTransactionService()
					.getUserTransaction();
			tx.begin();
			serviceRegistry.getAuthenticationService().authenticate("admin",
					"admin".toCharArray());

			// ContentData contentData =
			// (ContentData)this.contentPropertyValues.get(JackRabbitUtils.PROP_CONTENT);

			// Get the root node and the folder that we are going to create the
			// new node within
			NodeRef rootNodeRef = serviceRegistry.getNodeService().getRootNode(
					new StoreRef("workspace://SpacesStore"));
			NodeRef companyHome = serviceRegistry.getSearchService()
					.selectNodes(rootNodeRef, "./app:company_home", null,
							serviceRegistry.getNamespaceService(), true).get(0);

			try {
				// Create the new file in the folder
				for (int i = 0; i < REPEATS; i++) {
					createAlfrescoJCRFile(contentPropertyValues, companyHome);
				}
			} catch (Exception exception) {
				throw new RepositoryException(exception);
			}

			tx.commit();

		} catch (Throwable exception) {
			throw new RuntimeException("Unable to execute test", exception);
		}
	}

	public void createJCRContent() {
		try {
			// Start the session
			Session session = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));
			try {
				// ContentData contentData =
				// (ContentData)this.contentPropertyValues.get(JackRabbitUtils.PROP_CONTENT);

				// Get the root node and the folder that we are going to create
				// the new node within
				Node rootNode = session.getRootNode();
				final Node folder = rootNode.getNode("./app:company_home");

				try {
					// Create the new file in the folder
					for (int i = 0; i < REPEATS; i++) {
						createJCRFile(contentPropertyValues, folder);
					}
				} catch (Exception exception) {
					throw new RepositoryException(exception);
				}

				// Save the session
				session.save();
				// folder.save();

				// return null;

			} finally {
				// Close the session
				session.logout();
			}
		} catch (Throwable exception) {
			throw new RuntimeException("Unable to execute test", exception);
		}
	}

	public static NodeRef createAlfrescoFile(
			Map<String, Object> propertyValues, NodeRef parentNode)
			throws Exception {
		ContentData contentData = (ContentData) propertyValues
				.get(PROP_CONTENT);
		String name = contentData.getName();

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(
				1);
		properties.put(ContentModel.PROP_NAME, (Serializable) name);
		NodeRef newNodeRef = serviceRegistry.getNodeService()
				.createNode(
						parentNode,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(
								NamespaceService.CONTENT_MODEL_1_0_URI, name),
						ContentModel.TYPE_CONTENT, properties).getChildRef();

		ContentWriter contentWriter = serviceRegistry.getContentService()
				.getWriter(newNodeRef, ContentModel.PROP_CONTENT, true);
		contentWriter.setEncoding(contentData.getEncoding());
		contentWriter.setMimetype(contentData.getMimetype());
		contentWriter.putContent(contentData.getFile());

		return newNodeRef;
	}

	public static NodeRef createAlfrescoJCRFile(
			Map<String, Object> propertyValues, NodeRef parentNode)
			throws Exception {
		ContentData contentData = (ContentData) propertyValues
				.get(PROP_CONTENT);
		String name = contentData.getName();

		Map<QName, Serializable> fileProperties = new HashMap<QName, Serializable>(
				1);
		fileProperties.put(ContentModel.PROP_NAME, (Serializable) name);
		NodeRef fileNodeRef = serviceRegistry.getNodeService()
				.createNode(
						parentNode,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(
								NamespaceService.CONTENT_MODEL_1_0_URI, name),
						QName.createQName("nt:file", serviceRegistry
								.getNamespaceService()), fileProperties)
				.getChildRef();

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(
				1);
		properties.put(QName.createQName("jcr:mimeType", serviceRegistry
				.getNamespaceService()), contentData.getMimetype());
		properties.put(QName.createQName("jcr:encoding", serviceRegistry
				.getNamespaceService()), contentData.getEncoding());
		// Need to set the mandatory 'lastModified' property
		Calendar lastModified = Calendar.getInstance();
		lastModified.setTimeInMillis(contentData.getFile().lastModified());
		properties.put(QName.createQName("jcr:lastModified", serviceRegistry
				.getNamespaceService()), lastModified);
		NodeRef newNodeRef = serviceRegistry.getNodeService().createNode(
				fileNodeRef,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
						"jcr:content"),
				QName.createQName("nt:resource", serviceRegistry
						.getNamespaceService()), properties).getChildRef();

		ContentWriter contentWriter = serviceRegistry.getContentService()
				.getWriter(
						newNodeRef,
						QName.createQName("jcr:data", serviceRegistry
								.getNamespaceService()), true);
		contentWriter.setEncoding(contentData.getEncoding());
		contentWriter.setMimetype(contentData.getMimetype());
		contentWriter.putContent(contentData.getFile());

		return newNodeRef;
	}

	public static Node createJCRFile(Map<String, Object> propertyValues,
			Node parentNode) throws Exception {
		ContentData contentData = (ContentData) propertyValues
				.get(PROP_CONTENT);

		// Create the file node
		Node fileNode = parentNode.addNode(contentData.getName(), "nt:file");

		// Add the content
		Node resNode = fileNode.addNode("jcr:content", "nt:resource");
		resNode.setProperty("jcr:mimeType", contentData.getMimetype());
		resNode.setProperty("jcr:encoding", contentData.getEncoding());
		resNode.setProperty("jcr:data", new FileInputStream(contentData
				.getFile()));

		// TODO need to add and set the Dublin code properties

		// Need to set the mandatory 'lastModified' property
		Calendar lastModified = Calendar.getInstance();
		lastModified.setTimeInMillis(contentData.getFile().lastModified());
		resNode.setProperty("jcr:lastModified", lastModified);

		return resNode;
	}

	public static Node createFolder(Node parentNode) throws Exception {
		return parentNode.addNode(BenchmarkUtils.getGUID(), "nt:folder");
	}

	public static synchronized List<PropertyProfile> getContentPropertyProfiles() {
		if (contentPropertyProfiles == null) {
			// Prepare the property profile data
			contentPropertyProfiles = new ArrayList<PropertyProfile>();

			// content properties
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_NAME.toString()));
			contentPropertyProfiles.add(new PropertyProfile(PROP_CONTENT
					.toString(), PropertyType.CONTENT));

			// dublincore properties
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_PUBLISHER.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_CONTRIBUTER.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_TYPE.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_IDENTIFIER.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_DCSOURCE.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_COVERAGE.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_RIGHTS.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_SUBJECT.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DC_AUTHOR.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_TITLE.toString()));
			contentPropertyProfiles.add(PropertyProfile
					.createSmallTextProperty(PROP_DESCRIPTION.toString()));
		}

		return contentPropertyProfiles;
	}

	private class Nester extends Thread {
		Thread waiter;

		Job job;

		Nester(String name, Thread waiter, Job job) {
			super(name);
			this.setDaemon(true);
			this.waiter = waiter;
			this.job = job;
		}

		public void run() {

			if (waiter != null) {
				waiter.start();
			}
			try {
				// System.out.println("Start " + this.getName());
				job.run();
				// System.out.println("End " + this.getName());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(12);
			}
			if (waiter != null) {
				try {
					waiter.join();
				} catch (InterruptedException e) {
				}
			}
		}

	}

	private interface Job {
		public void run();
	}

}
