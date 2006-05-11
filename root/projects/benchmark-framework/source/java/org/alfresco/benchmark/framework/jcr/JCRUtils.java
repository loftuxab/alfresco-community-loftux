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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.DataLoaderComponent;
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
    
    public static synchronized List<PropertyProfile> getContentPropertyProfiles()
    {
        if (contentPropertyProfiles == null)
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
        
        return contentPropertyProfiles;
    }
    
    public static Node createFile(RepositoryProfile repositoryProfile, Node parentNode)
        throws Exception
    {
        Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(repositoryProfile, getContentPropertyProfiles());
        return createFile(propertyValues, parentNode);
    }
    
    public static Node createFile(Map<String, Object> propertyValues, Node parentNode)
        throws Exception
    {
        ContentData contentData = (ContentData)propertyValues.get(PROP_CONTENT);
        
        // Create the file node
        Node fileNode = parentNode.addNode(contentData.getName(), "nt:file");
    
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
        
        return fileNode;
    }
    
    public static Node createFolder(RepositoryProfile repositoryProfile, Node parentNode)
        throws Exception
    {
        return parentNode.addNode (BenchmarkUtils.getGUID(), "nt:folder");        
    }
    
    public static synchronized List<Node> getRootFolders(Node rootNode)
        throws Exception
    {
        NodeIterator nodes = rootNode.getNodes();
        List<Node> rootFolders = new ArrayList<Node>();
        
        while (nodes.hasNext() == true)
        {
            Node node = nodes.nextNode();
            
            if (node.getName().startsWith(JCRDataLoaderComponentImpl.JCR_BENCHMARK_OBJECT_PREFIX) == true)
            {
                rootFolders.add(node);
            }
        }
        
        if (rootFolders.size() == 0)
        {
            throw new RuntimeException("No JCR benchamrk data has been loaded into the repository");
        }
        
        return rootFolders;
    }
    
    private static List<String> folders;
    private static List<String> content;
    
    @SuppressWarnings("unchecked")
    public static synchronized String getRandomFolder()
    {
        try
        {
            if (folders == null)
            {
                folders = (List<String>)new ObjectInputStream(new FileInputStream(BenchmarkUtils.getOutputFileLocation() + File.separator + "loaded_folders.bin")).readObject();            
            }
            
            int size = folders.size();
            int rand = BenchmarkUtils.rand.nextInt(size);
            return folders.get(rand);
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to get random folder path", exception);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static synchronized String getRandomContent()
    {
        try
        {
            if (content == null)
            {
                content = (List<String>)new ObjectInputStream(new FileInputStream(BenchmarkUtils.getOutputFileLocation() + File.separator + "loaded_content.bin")).readObject();            
            }
            
            int size = content.size();
            int rand = BenchmarkUtils.rand.nextInt(size);
            return content.get(rand);
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to get random content path", exception);
        }
    } 
    
//    public static Node getRandomFolder(Node rootNode)
//        throws Exception
//    {       
//        List<Node> folders = new ArrayList<Node>(); 
//        getRandomFolder(getRootFolders(rootNode), folders);
//        
//        Node folder = folders.get(BenchmarkUtils.rand.nextInt(folders.size()));        
//        return folder;
//    }
//    
//    private static void getRandomFolder(List<Node> folders, List<Node> result)
//        throws Exception
//    {
//        int randIndex = BenchmarkUtils.rand.nextInt(folders.size());
//        Node folder = folders.get(randIndex);
//        result.add(folder);
//
//        // Get the sub-folders of the folder
//        NodeIterator children = folder.getNodes();
//        List<Node> subFolders = new ArrayList<Node>();
//        while(children.hasNext() == true)
//        {
//            Node child = children.nextNode();
//            if (child.getPrimaryNodeType().getName().equals("nt:folder") == true)
//            {
//                subFolders.add(child);
//            }
//        }
//        
//        if (subFolders.size() != 0)
//        {
//            getRandomFolder(subFolders, result);
//        }
//    }
//    
//    public static Node getRandomContent(Node rootNode)
//        throws Exception
//    {
//        List<Node> contentList = new ArrayList<Node>();
//        
//        while (contentList.size() == 0)
//        {
//            Node folder = getRandomFolder(rootNode);
//            
//            NodeIterator children = folder.getNodes();
//            while(children.hasNext() == true)
//            {
//                Node child = children.nextNode();
//                if (child.getPrimaryNodeType().getName().equals("nt:file") == true)
//                {
//                    contentList.add(child);
//                }
//            }
//        }
//        
//        Node content = contentList.get(BenchmarkUtils.rand.nextInt(contentList.size())); 
//        return content;
//   }
}
