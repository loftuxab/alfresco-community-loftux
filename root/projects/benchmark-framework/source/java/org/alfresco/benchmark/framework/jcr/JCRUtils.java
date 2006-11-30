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
package org.alfresco.benchmark.framework.jcr;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile.PropertyType;

/**
 * @author Roy Wetherall
 */
public class JCRUtils
{
    public static String PROP_NAME =             "name";
    public static String PROP_CONTENT =          "content";
    public static String PROP_DC_PUBLISHER =     "publisher";
    public static String PROP_DC_CONTRIBUTER =   "contributer";
    public static String PROP_DC_TYPE =          "type";
    public static String PROP_DC_IDENTIFIER =    "identifier";
    public static String PROP_DC_DCSOURCE =      "dcsource";
    public static String PROP_DC_COVERAGE =      "coverage";
    public static String PROP_DC_RIGHTS =        "rights";
    public static String PROP_DC_SUBJECT =       "subject";
    public static String PROP_DC_AUTHOR =        "author";
    public static String PROP_TITLE =            "title";
    public static String PROP_DESCRIPTION =      "description";    
    
    @SuppressWarnings("unused")
    protected static Repository repository;
    protected static List<PropertyProfile> contentPropertyProfiles;
    static
    {
        // Prepare the property profile data
        contentPropertyProfiles = new ArrayList<PropertyProfile>();
        
        // content properties
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_NAME.toString()));
        contentPropertyProfiles.add(new PropertyProfile(JCRUtils.PROP_CONTENT.toString(), PropertyType.CONTENT));
        
        // dublincore properties
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_PUBLISHER.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_CONTRIBUTER.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_TYPE.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_IDENTIFIER.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_DCSOURCE.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_COVERAGE.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_RIGHTS.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_SUBJECT.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DC_AUTHOR.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_TITLE.toString()));
        contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(JCRUtils.PROP_DESCRIPTION.toString()));
    }
    /**
     * Get a list of the content property profiles
     * 
     * @return
     */
    public static synchronized List<PropertyProfile> getContentPropertyProfiles()
    {
        return contentPropertyProfiles;
    }
    
    public static Node createFile(Node parentNode)
    throws Exception
    {
        return createFile(parentNode, null);
    }
    
    /**
     * Create file
     * 
     * @param parentNode
     * @return
     * @throws Exception
     */
    public static Node createFile(Node parentNode, String folderName)
        throws Exception
    {
        Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(getContentPropertyProfiles());
        return createFile(propertyValues, parentNode, folderName);
    }
    
    /**
     * Create file
     * 
     * @param propertyValues
     * @param parentNode
     * @return
     * @throws Exception
     */
    public static Node createFile(Map<String, Object> propertyValues, Node parentNode)
    throws Exception
    {
        return createFile(propertyValues, parentNode, null);
    }
    
    /**
     * Create file
     * 
     * @param propertyValues
     * @param parentNode
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Node createFile(Map<String, Object> propertyValues, Node parentNode, String fileName)
        throws Exception
    {
        ContentData contentData = (ContentData)propertyValues.get(PROP_CONTENT);
        String name = fileName;
        if (name == null)
        {
            name = contentData.getName();
        }
        
        // Create the file node
        Node fileNode = parentNode.addNode(name, "nt:file");
        
        // Add the lockable mixin to the file
        fileNode.addMixin("mix:lockable");
        
        // Add referenceable aspect
        fileNode.addMixin("mix:referenceable");
    
        // Add the content
        Node resNode = fileNode.addNode ("jcr:content", "nt:resource");
        resNode.setProperty ("jcr:mimeType", contentData.getMimetype());
        resNode.setProperty ("jcr:encoding", contentData.getEncoding());
        resNode.setProperty ("jcr:data", new FileInputStream(contentData.getFile()));
        
        // TODO need to add and set the Dublin code properties
        
        // Need to set the mandatory 'lastModified' property
        Calendar lastModified = Calendar.getInstance ();
        lastModified.setTimeInMillis (contentData.getFile().lastModified ());
        resNode.setProperty ("jcr:lastModified", lastModified);
        
        //System.out.println(fileNode.getPath());
        
        return fileNode;
    }
    
    /**
     * Create folder
     * 
     * @param parentNode
     * @return
     * @throws Exception
     */
    public static Node createFolder(Node parentNode)
        throws Exception
    {
        return createFolder(parentNode, null);
    }
    
    /**
     * Create folder 
     * 
     * @param parentNode
     * @return
     * @throws Exception
     */
    public static Node createFolder(Node parentNode, String folderName)
        throws Exception
    {
        String name = folderName;
        if (name ==  null)
        {
            name = BenchmarkUtils.getGUID();
        }
        Node folderNode = parentNode.addNode(name, "nt:folder");
        folderNode.addMixin("mix:lockable");
        
        // Add referenceable aspect
        folderNode.addMixin("mix:referenceable");
    
        return folderNode;
    }
    
    /**
     * Get the test data root folders
     * 
     * @param rootNode
     * @return
     * @throws Exception
     */
    public static Node getRootTestDataFolder(Node rootNode)
        throws Exception
    {
        Node rootFolder = null;
        NodeIterator nodes = rootNode.getNodes();
        
        while (nodes.hasNext() == true)
        {
            Node node = nodes.nextNode();
            
            if (node.getName().startsWith(JCRDataLoaderComponentImpl.JCR_BENCHMARK_OBJECT_PREFIX) == true)
            {
                rootFolder = node;
                break;
            }
        }
        
        if (rootFolder == null)
        {
            throw new RuntimeException("ERROR:  No JCR benchamrk data has been loaded into the repository");
        }
        
        return rootFolder;
    } 
    
    protected static String rootNodeName;
    protected static RepositoryProfile repositoryProfile;
    
    public static RepositoryProfile getRepositoryProfile()
        throws Exception
    {
        if (repositoryProfile == null)
        {
            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try 
            {            
                // Get the root node and the folder that we are going to create the new node within
                Node rootNode = session.getRootNode(); 
                Node dataRootNode = getRootTestDataFolder(rootNode);
                
                String repositoryProfileValue = null;
                if (BenchmarkUtils.getJCRType().equals("Alfresco") == true)
                {
                    repositoryProfileValue = dataRootNode.getProperty("ben:repositoryProfile").getString();
                }
                else
                {
                    String name = dataRootNode.getName();
                    String[] values = name.split("_");
                    repositoryProfileValue = values[values.length-1];
                }
                repositoryProfile = new RepositoryProfile(repositoryProfileValue);
            }                                   
            finally
            {
                // Close the session
                session.logout();
            }
        }
        return repositoryProfile;
    }
    
    public static String getRootNodeName()
        throws Exception
    {
        if (rootNodeName == null)
        {
            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            try 
            {            
                // Get the root node and the folder that we are going to create the new node within
                Node rootNode = session.getRootNode(); 
                Node dataRootNode = getRootTestDataFolder(rootNode);
                rootNodeName = dataRootNode.getName();
            }                                   
            finally
            {
                // Close the session
                session.logout();
            }
        }
        return rootNodeName;
    }
}
