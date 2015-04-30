/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.metadata.DataModelMapper;
import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.enterprise.repo.officeservices.vfs.FolderNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.AbstractSoapService;
import com.xaldon.officeservices.URLPathDecoder;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.ContentTypeDefinition;
import com.xaldon.officeservices.datamodel.ContentTypeId;
import com.xaldon.officeservices.datamodel.Guid;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.lists.AbstractListsService;
import com.xaldon.officeservices.lists.CheckinType;
import com.xaldon.officeservices.lists.ListChanges;
import com.xaldon.officeservices.lists.ListDescription;
import com.xaldon.officeservices.lists.ListItem;
import com.xaldon.officeservices.lists.ListUpdatesBatch;
import com.xaldon.officeservices.lists.ListsGetListAndViewView;
import com.xaldon.officeservices.lists.ListsGetListContentTypeContentType;
import com.xaldon.officeservices.lists.ListsGetListContentTypesContentType;
import com.xaldon.officeservices.lists.ListsGetListList;
import com.xaldon.officeservices.lists.StandardListChanges;
import com.xaldon.officeservices.lists.UpdateListItemsResults;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.protocol.SoapParameter;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSNode;

public class ListsService extends AbstractListsService
{
    
    private static final long serialVersionUID = 5522867539956803717L;

    protected AlfrescoVirtualFileSystem vfs;
    
    protected AuthenticationService authenticationService;
    
    protected NamespaceService namespaceService;
    
    protected DictionaryService dictionaryService;
    
    protected DataModelMapper dataModelMapper;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    // initialization

    @Override
    public void init() throws ServletException
    {
        super.init();
        
        setupFieldEvaluators();
        
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if(wac == null)
        {
            throw new ServletException("Error initializing Servlet. No WebApplicationContext available.");
        }
        
        vfs = (AlfrescoVirtualFileSystem) wac.getBean("AosVirtualFileSystem");
        if(vfs == null)
        {
            throw new ServletException("Cannot find bean AosVirtualFileSystem in WebApplicationContext.");
        }
        ((AlfrescoVirtualFileSystem)vfs).prepare();
        authenticationService = (AuthenticationService) wac.getBean("AuthenticationService");
        if(authenticationService == null)
        {
            throw new ServletException("Cannot find bean AuthenticationService in WebApplicationContext.");
        }
        namespaceService = (NamespaceService) wac.getBean("NamespaceService");
        if(namespaceService == null)
        {
            throw new ServletException("Cannot find bean NamespaceService in WebApplicationContext.");
        }
        dictionaryService = (DictionaryService) wac.getBean("DictionaryService");
        if(dictionaryService == null)
        {
            throw new ServletException("Cannot find bean DictionaryService in WebApplicationContext.");
        }
        dataModelMapper = (DataModelMapper) wac.getBean("aosServerPropertiesProvider");
        if(dataModelMapper == null)
        {
            throw new ServletException("Cannot find bean aosServerPropertiesProvider in WebApplicationContext.");
        }
    }
    
    // transaction

    @Override
    public void soapService(final UserData userData, final String methodName, final SimpleSoapParser parser, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationRequiredException
    {
        try
        {
            ((AlfrescoVirtualFileSystem)vfs).getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    ListsService.super.soapService(userData, methodName, parser, request, response);
                    return null;
                }
            });
        }
        catch(Throwable t)
        {
        	try
        	{
        		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	}
        	catch(Exception e)
        	{
        		;
        	}
        	
        }
    }
    
    // file access

    @Override
    protected String getSitePrefix(HttpServletRequest request)
    {
        return ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request);
    }

    @Override
    protected boolean checkoutFile(UserData userData, String documentName, boolean checkoutToLocal, String lastModified) throws AuthenticationRequiredException
    {
        DocumentNode document = null;
        try
        {
            document = (DocumentNode) vfs.getNodeByPath(userData, documentName, VFSNode.CALLCONTEXT_LISTSSERVICE);
        }
        catch (ClassCastException cce)
        {
            logger.error("checkoutFile: Trying to request checkout of non-file node.");
            document = null;
        }
        if (document == null)
        {
            logger.error("checkoutFile: Path does not point to a file: documentName="+documentName);
            return false;
        }
        // check out
        return (document.checkout(0, null, VFSNode.CALLCONTEXT_LISTSSERVICE, checkoutToLocal) == VFSDocumentNode.CHECKOUT_OK);
    }

    @Override
    protected boolean checkinFile(UserData userData, String documentName, String comment, CheckinType checkinType) throws AuthenticationRequiredException
    {
        VFSDocumentNode document = null;
        try
        {
            document = (VFSDocumentNode) vfs.getNodeByPath(userData, documentName, VFSNode.CALLCONTEXT_LISTSSERVICE);
        }
        catch (ClassCastException cce)
        {
            logger.error("checkintFile: Trying to request checkin of non-file node.");
            document = null;
        }
        if (document == null)
        {
            logger.error("checkinFile: Path does not point to a file: documentName="+documentName);
            return false;
        }
        // check in
        return document.checkin(comment, false, VFSNode.CALLCONTEXT_LISTSSERVICE, checkinType);
    }

    @Override
    protected boolean undoCheckOut(UserData userData, String documentName) throws AuthenticationRequiredException
    {
        VFSDocumentNode document = null;
        try
        {
            document = (VFSDocumentNode) vfs.getNodeByPath(userData, documentName, VFSNode.CALLCONTEXT_LISTSSERVICE);
        }
        catch (ClassCastException cce)
        {
            logger.error("undoCheckOut: Trying to request undo checkout of non-file node.");
            document = null;
        }
        if (document == null)
        {
            logger.error("undoCheckOut: Path does not point to a file: documentName="+documentName);
            return false;
        }
        // un check out
        return document.uncheckout(false, VFSNode.CALLCONTEXT_LISTSSERVICE);
    }
    
    // Lists

    @Override
    protected ListsGetListList getList(UserData userData, String listName, HttpServletRequest request)
    {
        Guid listId = Guid.parse(listName);
        String sitePath = ((AlfrescoVirtualFileSystem)vfs).getSitePath(request);
        if(AlfrescoVirtualFileSystem.LISTID_ROOT_DOCUMENTS.equals(listId))
        {
            return new ListsGetListList(ListsGetListList.SERVERTEMPLATE_DOCUMENT_LIBRARY,
                    AlfrescoVirtualFileSystem.LISTID_ROOT_DOCUMENTS.toString(),
                    "Documents",
                    "List of Documents",
                    "",
                    sitePath);
        }
        VFSNode listNode = vfs.getNodeFromList(listId, null, null);
        if(listNode == null)
        {
            return null;
        }
        return new ListsGetListList(ListsGetListList.SERVERTEMPLATE_DOCUMENT_LIBRARY,
                    listId.toString(),
                    listNode.getName(VFSNode.CALLCONTEXT_LISTSSERVICE),
                    "",
                    "",
                    sitePath);
    }

    @Override
    protected ListsGetListAndViewView getView(UserData userData, String listName, String viewName, HttpServletRequest request) throws AuthenticationRequiredException
    {
        // view not found
        return null;
    }

    @Override
    public List<?> getListCollection(SimpleSoapParser parser)
    {
        List<ListDescription> lists = new ArrayList<ListDescription>();
        try
        {
            VFSNode rootNode = vfs.getNodeByPath(null, "/", VFSNode.CALLCONTEXT_HTTPPROPFIND);
            @SuppressWarnings("unchecked")
            List<VFSNode> rootContent = rootNode.getEnumerableContainees(null, VFSNode.CALLCONTEXT_HTTPPROPFIND);
            for(VFSNode n : rootContent)
            {
                if(n instanceof FolderNode)
                {
                    String name = n.getName(VFSNode.CALLCONTEXT_HTTPPROPFIND);
                    Guid listId = Guid.parse(((FolderNode)n).getNodeRef().getId());
                    ListDescription ld =  new ListDescription(AbstractListsService.LISTTYPE_DOCUMENTS,listId.toString(),name,"");
                    lists.add(ld);
                }
            }
        }
        catch(Exception e)
        {
            logger.error("getListCollection: error enumerating all top folders as document lists",e);
        }
        return lists;
    }

    @Override
    protected boolean isWSS3List(UserData userData, String listName)
    {
        return true;
    }

    @Override
    protected int getListType(UserData userData, String listName)
    {
        // handle Document lists
        return ListsService.LISTTYPE_DOCUMENTS;
    }

    @Override
    protected boolean isValidListName(UserData userData, String listName)
    {
        return false;
    }

    @Override
    protected String getAlternateServerURL(UserData userData, String listName, HttpServletRequest request)
    {
        return ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request);
    }

    @Override
    protected String getRootFolder(UserData userData, String listName)
    {
        return "/";
    }

    @Override
    protected ListChanges getListItemChanges(UserData userData, String listName, String since, HttpServletRequest request) throws AuthenticationRequiredException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected ListChanges getListItems(UserData userData, String listName, SoapParameter query, SoapParameter queryOptions, SoapParameter viewFields, HttpServletRequest request) throws AuthenticationRequiredException
    {
        Guid listId = Guid.parse(listName);
        // decode where statement and get requested FileLeafRef or FileRef
        String fileLeafRef = null;
        //String FileRef = null;
        if(query != null)
        {
            SoapParameter Query = query.getFirstSubParameterByName("Query");
            if (Query != null)
            {
                SoapParameter Where = Query.getFirstSubParameterByName("Where");
                if (Where != null)
                {
                    SoapParameter Eq = Where.getFirstSubParameterByName("Eq");
                    if (Eq != null)
                    {
                        SoapParameter FieldRef = Eq.getFirstSubParameterByName("FieldRef");
                        SoapParameter Value = Eq.getFirstSubParameterByName("Value");
                        if ((FieldRef != null) && (Value != null))
                        {
                            SoapParameter FieldRefName = FieldRef.getFirstSubParameterByName("Name");
                            if ((FieldRefName != null) && (FieldRefName.getValue() != null) && (FieldRefName.getValue().equals("FileLeafRef")))
                            {
                                fileLeafRef = Value.getValue();
                            }
                            if ((FieldRefName != null) && (FieldRefName.getValue() != null) && (FieldRefName.getValue().equals("FileRef")))
                            {
                                //FileRef = Value.getValue();
                            }
                        }
                    }
                }
            }
        }
        // get folder from QueryOptions
        String inFolder = null;
        SoapParameter QueryOptions = queryOptions.getFirstSubParameterByName("QueryOptions");
        if (QueryOptions != null)
        {
            SoapParameter Folder = QueryOptions.getFirstSubParameterByName("Folder");
            if (Folder != null)
            {
                try
                {
                    inFolder = URLPathDecoder.decode(Folder.getValue(), "UTF8");
                }
                catch (UnsupportedEncodingException e)
                {
                    inFolder = Folder.getValue();
                };
            }
        }
        // decode viewFields
        ArrayList<String> requestedFields = new ArrayList<String>();
        SoapParameter vf = viewFields.getFirstSubParameterByName("ViewFields");
        if (vf != null)
        {
            List<?> subs = vf.getSubParameters();
            if (subs != null)
            {
                for (int i = 0; i < subs.size(); i++)
                {
                    SoapParameter field = (SoapParameter) subs.get(i);
                    if (field.getName().equals("FieldRef"))
                    {
                        SoapParameter fieldRefName = field.getFirstSubParameterByName("Name");
                        if (fieldRefName != null)
                        {
                            requestedFields.add(fieldRefName.getValue());
                        }
                    }
                }
            }
        }
        // build result list
        StandardListChanges result = new StandardListChanges("0", "0");
        // find referenced node in VFS
        VFSNode node = vfs.getNodeFromList(listId, inFolder, fileLeafRef);
        if(node == null)
        {
            return result;
        }
        // enumerated requested list items
        String baseUrl = this.getSitePrefix(request);
        if(node instanceof VFSDocumentNode)
        {
            ListItem li = convertToListItem(node, requestedFields, inFolder, baseUrl);
            if(li != null)
            {
                result.addModifiedListItem(li);
            }
        }
        else
        {
            String pathToLeaf = ((FolderNode)node).getRelativePath();
            if(pathToLeaf.startsWith("/"))
            {
                if(pathToLeaf.length()==1)
                {
                    pathToLeaf = "";
                }
                else
                {
                    pathToLeaf = pathToLeaf.substring(1);
                }
            }
            if(pathToLeaf!=null&pathToLeaf.length()>0)
            {
                baseUrl = baseUrl + "/" + pathToLeaf;
            }
            @SuppressWarnings("unchecked")
            List<VFSNode> content = node.getEnumerableContainees(userData, VFSNode.CALLCONTEXT_LISTSSERVICE);
            for(VFSNode contentElement : content)
            {
                ListItem li = convertToListItem(contentElement, requestedFields, pathToLeaf, baseUrl);
                if(li != null)
                {
                    result.addModifiedListItem(li);
                }
            }
        }
        // we are done
        return result;
    }

    protected ListItem convertToListItem(VFSNode node, ArrayList<String> requestedFields, String pathToLeaf, String baseUrl)
    {
        FileInfo fileInfo = null;
        if(node instanceof DocumentNode)
        {
            fileInfo = ((DocumentNode)node).getFileInfo();
        }
        else if(node instanceof FolderNode)
        {
            fileInfo = ((FolderNode)node).getFileInfo();
        }
        if(fileInfo == null)
        {
            return null;
        }
        // mandatory properties
        String itemId = "1";
        Guid itemGuid = Guid.parse(fileInfo.getNodeRef().getId());
        ListItem result = new ListItem(itemId,
                    fileInfo.getModifiedDate(),
                    fileInfo.getCreatedDate(),
                    "1", "UNKNOWN\\unknown",
                    "1", "UNKNOWN\\unknown",
                    "1",
                    false);
        result.setNamedColumn("ows__Level", (node instanceof DocumentNode) ? (((DocumentNode)node).isCheckedOutToLocal() ? "255" : "2") : "1");
        result.setNamedColumn("ows_UniqueId", itemId + ";#" + itemGuid.toString());
        result.setNamedColumn("ows_FSObjType", itemId + ";#" + (fileInfo.isFolder() ? "1" : "0"));
        result.setNamedColumn("ows_Created_x0020_Date", itemId + ";#" + AbstractSoapService.formatDate(fileInfo.getCreatedDate()));
        result.setNamedColumn("ows_ProgId", itemId + ";#");
        result.setNamedColumn("ows_FileLeafRef", itemId + ";#" + fileInfo.getName());
        result.setNamedColumn("ows_FileRef", itemId + ";#" + ((pathToLeaf!=null&&pathToLeaf.length()>0)?pathToLeaf+"/":"") + fileInfo.getName());
        result.setNamedColumn("ows_Last_x0020_Modified", itemId + ";#" + AbstractSoapService.formatDate(fileInfo.getModifiedDate()));
        result.setNamedColumn("ows_ServerRedirected", "0");
        // requested properties
        for(String requestedField : requestedFields)
        {
            FieldEvaluator evaluator = fieldEvaluators.get(requestedField);
            if(evaluator != null)
            {
                String fieldValue = evaluator.getValue(node, itemId, baseUrl);
                if(fieldValue != null)
                {
                    result.setNamedColumn("ows_"+requestedField, fieldValue);
                }
            }
        }
        return result;
    }

    protected Map<String, FieldEvaluator> fieldEvaluators;
    
    private interface FieldEvaluator
    {
        public String getValue(VFSNode node, String itemId, String baseUrl);
    }
    
    protected void setupFieldEvaluators()
    {
        fieldEvaluators = new HashMap<String, FieldEvaluator>();
        fieldEvaluators.put("IsCheckedoutToLocal", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return itemId + ";#" + (((DocumentNode)node).isCheckedOutToLocal() ? "1" : "0");
                }
                return null;
            }
        });
        fieldEvaluators.put("CheckoutUser", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    if(((DocumentNode)node).isCheckedOut())
                    {
                        return itemId + ";#" + ((DocumentNode)node).getCheckoutOwner();
                    }
                }
                return null;
            }
        });
        fieldEvaluators.put("ContentType", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return ((DocumentNode)node).getContentTypeName();
                }
                else
                {
                    return "Folder";
                }
            }
        });
        fieldEvaluators.put("DocIcon", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    String name = node.getName(VFSDocumentNode.CALLCONTEXT_LISTSSERVICE);
                    int extSepPos = name.lastIndexOf('.');
                    if( (extSepPos >= 0) && (extSepPos < (name.length()-1)) )
                    {
                        return name.substring(extSepPos + 1);
                    }
                }
                return null;
            }
        });
        fieldEvaluators.put("ContentTypeId", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return ((DocumentNode)node).getContentTypeId().toString();
                }
                else
                {
                    return "0x0120";
                }
            }
        });
        fieldEvaluators.put("LinkFilename", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                return node.getName(VFSDocumentNode.CALLCONTEXT_LISTSSERVICE);
            }
        });
        fieldEvaluators.put("File_x0020_Size", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return itemId + ";#" + Long.toString(((DocumentNode)node).getSize(VFSDocumentNode.CALLCONTEXT_LISTSSERVICE));
                }
                else
                {
                    return itemId + ";#";
                }
            }
        });
        fieldEvaluators.put("File_x0020_Type", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    String name = node.getName(VFSDocumentNode.CALLCONTEXT_LISTSSERVICE);
                    int extSepPos = name.lastIndexOf('.');
                    if( (extSepPos >= 0) && (extSepPos < (name.length()-1)) )
                    {
                        return name.substring(extSepPos + 1);
                    }
                }
                return null;
            }
        });
        fieldEvaluators.put("EncodedAbsUrl", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                return baseUrl + "/" + node.getName(VFSDocumentNode.CALLCONTEXT_LISTSSERVICE);
            }
        });
        fieldEvaluators.put("FileSizeDisplay", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return Long.toString(((DocumentNode)node).getSize(VFSDocumentNode.CALLCONTEXT_LISTSSERVICE));
                }
                return null;
            }
        });
        fieldEvaluators.put("Created_x0020_By", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return ((DocumentNode)node).getCreator();
                }
                return null;
            }
        });
        fieldEvaluators.put("Modified_x0020_By", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return ((DocumentNode)node).getModifier();
                }
                return null;
            }
        });
        fieldEvaluators.put("PermMask", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                return "0x7fffffffffffffff";
            }
        });
        fieldEvaluators.put("_UIVersionString", new FieldEvaluator()
        {
            @Override
            public String getValue(VFSNode node, String itemId, String baseUrl)
            {
                if(node instanceof DocumentNode)
                {
                    return ((DocumentNode)node).getVersionNumber().toString();
                }
                else
                {
                    return "1.0";
                }
            }
        });
    }

    @Override
    protected UpdateListItemsResults updateListItems(UserData userData, String listName, ListUpdatesBatch batch, HttpServletRequest request) throws AuthenticationRequiredException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected ListChanges getListChangesSinceToken(UserData userData, String listName, SoapParameter viewFields, String rowLimit, SoapParameter queryOptions, String changeToken, SoapParameter contains, HttpServletRequest request) throws AuthenticationRequiredException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected ListsGetListContentTypeContentType getListContentType(UserData userData, String listName, String contentTypeId, HttpServletRequest request) throws AuthenticationRequiredException
    {
        // sanity check
        if(contentTypeId == null)
        {
            return null;
        }
        ContentTypeId cti;
        try
        {
            cti = ContentTypeId.parse(contentTypeId);
        }
        catch(ParseException pe)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Invalid ContentTypeId: "+contentTypeId, pe);
            }
            return null;
        }
        // get the ContentTypeDefinition from our DataModelMapper
        ContentTypeDefinition contentTypeDef = this.dataModelMapper.getContentTypeDefinition(cti, true);
        if(contentTypeDef == null)
        {
            return null;
        }
        return new ListsGetListContentTypeContentType(contentTypeDef, vfs.getSitePath(request)+"/Data Dictionary/AOS");
    }

    @Override
    protected ListsGetListContentTypesContentType[] getListContentTypes(UserData userData, String listName, String contentTypeId, HttpServletRequest request) throws AuthenticationRequiredException
    {
        String scope =  this.getSitePrefix(request);
    	if( (contentTypeId != null) && (contentTypeId.length() > 0) )
    	{
    		return getListContentTypesForTypeId(contentTypeId, scope);
    	}
        Collection<ContentTypeDefinition> allContentTypeDefs = dataModelMapper.getAllContentTypes(false, true);
        ArrayList<ListsGetListContentTypesContentType> result = new ArrayList<ListsGetListContentTypesContentType>(allContentTypeDefs.size());
        for(ContentTypeDefinition contentTypeDef : allContentTypeDefs)
        {
            boolean isBestMatch = (contentTypeId !=  null) && contentTypeId.equalsIgnoreCase(contentTypeDef.toString());
            result.add(new ListsGetListContentTypesContentType(contentTypeDef, isBestMatch, scope));
        }
        return result.toArray(new ListsGetListContentTypesContentType[result.size()]);
    }

    private ListsGetListContentTypesContentType[] getListContentTypesForTypeId(String contentTypeId, String scope)
    {
        ContentTypeId cti;
        try
        {
            cti = ContentTypeId.parse(contentTypeId);
        }
        catch(ParseException pe)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Invalid ContentTypeId: "+contentTypeId, pe);
            }
            return null;
        }
        // get the ContentTypeDefinition from our DataModelMapper
        ContentTypeDefinition contentTypeDef = this.dataModelMapper.getContentTypeDefinition(cti, true);
        if(contentTypeDef == null)
        {
            return null;
        }
        ListsGetListContentTypesContentType[] result = new ListsGetListContentTypesContentType[1];
        result[0] = new ListsGetListContentTypesContentType(contentTypeDef, true, scope);
        return result;
    }

    // Authentication

	@Override
    public UserData negotiateAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        return new AuthenticationServiceUserData(authenticationService);
    }

    @Override
    public void requestAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // not required
    }

    @Override
    public void invalidateAuthentication(UserData userData, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // not required
    }

}
