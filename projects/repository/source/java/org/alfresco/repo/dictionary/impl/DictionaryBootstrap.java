package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.QName;

/**
 * Provides support for creating initial set of meta-data.
 * 
 * @author David Caruana
 */
public class DictionaryBootstrap
{   
    // Base type constants
    public static final QName TYPE_QNAME_BASE = QName.createQName(NamespaceService.ALFRESCO_URI, "base");
    
    // Referenceable aspect constants
    public static final QName TYPE_QNAME_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, "reference");
    public static final String PROP_REFERENCE = "reference";
    public static final QName PROP_QNAME_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, PROP_REFERENCE);
      
	// Copy aspect constants
	public static final String ASPECT_NAME_COPIEDFROM = "copiedfrom";
	public static final QName ASPECT_QNAME_COPIEDFROM = QName.createQName(NamespaceService.ALFRESCO_URI, ASPECT_NAME_COPIEDFROM);
	public static final String PROP_COPY_REFERENCE = "copyReference";
	public static final QName PROP_QNAME_COPY_REFERENCE = QName.createQName(NamespaceService.ALFRESCO_URI, PROP_COPY_REFERENCE);
	
	// Working copy aspect contants
	public static final String ASPECT_NAME_WORKING_COPY = "workingcopy";
	public static final QName ASPECT_QNAME_WORKING_COPY = QName.createQName(NamespaceService.ALFRESCO_URI, ASPECT_NAME_WORKING_COPY);
	
    // Container type constants
    public static final QName TYPE_QNAME_CONTAINER = QName.createQName(NamespaceService.ALFRESCO_URI, "container");
    public static final QName CHILD_ASSOC_CONTENTS = QName.createQName(NamespaceService.ALFRESCO_URI, "contents");

    // Content type constants
    public static final QName QNAME_CONTENT = QName.createQName(NamespaceService.ALFRESCO_URI, "content");
    public static final QName PROP_QNAME_CONTENT_URL = QName.createQName(NamespaceService.ALFRESCO_URI, "contentUrl");
    public static final QName PROP_QNAME_MIME_TYPE = QName.createQName(NamespaceService.ALFRESCO_URI, "mimetype");
    public static final QName PROP_QNAME_ENCODING = QName.createQName(NamespaceService.ALFRESCO_URI, "encoding");
    public static final QName PROP_QNAME_SIZE = QName.createQName(NamespaceService.ALFRESCO_URI, "size");
    
    
    // Categories and roots
    
    public static final QName ASPECT_QNAME_ROOT = QName.createQName(NamespaceService.ALFRESCO_URI, "aspect_root");
    public static final QName ASPECT_QNAME_CATEGORISATION = QName.createQName(NamespaceService.ALFRESCO_URI, "aspect_categorisation");
    public static final QName ASPECT_QNAME_GEN_CATEGORISATION = QName.createQName(NamespaceService.ALFRESCO_URI, "aspect_gen_categorisation");
    public static final QName TYPE_QNAME_CATEGORY = QName.createQName(NamespaceService.ALFRESCO_URI, "category");
    public static final QName TYPE_QNAME_STOREROOT = QName.createQName(NamespaceService.ALFRESCO_URI, "store_root");
    public static final QName TYPE_QNAME_CATEGORYROOT = QName.createQName(NamespaceService.ALFRESCO_URI, "category_root");  
    
	/**
     * Lock aspect QName
     */
    public final static String ASPECT_LOCKABLE = "lockable";
    public final static QName ASPECT_QNAME_LOCKABLE = QName.createQName(NamespaceService.ALFRESCO_URI, ASPECT_LOCKABLE);
    
    /**
     * Lock aspect attribute names
     */
    public final static String PROP_LOCK_OWNER = "lockOwner";
    public final static QName PROP_QNAME_LOCK_OWNER = QName.createQName(NamespaceService.ALFRESCO_URI, PROP_LOCK_OWNER);
    public final static String PROP_LOCK_TYPE = "lockType";
    public final static QName PROP_QNAME_LOCK_TYPE = QName.createQName(NamespaceService.ALFRESCO_URI, PROP_LOCK_TYPE);
	
	/**
	 * Version aspect name and attributes
	 */
	public static final String ASPECT_VERSIONABLE = "versionable";
	public static final QName ASPECT_QNAME_VERSIONABLE = QName.createQName(NamespaceService.ALFRESCO_URI, ASPECT_VERSIONABLE);
	public static final String PROP_CURRENT_VERSION_LABEL = "currentVersionLabel";
	public static final QName PROP_QNAME_CURRENT_VERSION_LABEL = QName.createQName(NamespaceService.ALFRESCO_URI, PROP_CURRENT_VERSION_LABEL);

    // Content type constants
    public static final QName TYPE_QNAME_CONTENT = QName.createQName(NamespaceService.ALFRESCO_URI, "content");
 
    // expected application types
    public static final QName TYPE_QNAME_FOLDER = QName.createQName(NamespaceService.ALFRESCO_URI, "folder");
    public static final QName TYPE_QNAME_FILE = QName.createQName(NamespaceService.ALFRESCO_URI, "file");
    public static final QName ASPECT_QNAME_SPACE = QName.createQName(NamespaceService.ALFRESCO_URI, "space");
    public static final String PROP_CREATED_DATE = "createddate";
    public static final String PROP_MODIFIED_DATE = "modifieddate";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_ICON = "icon";
    public static final String PROP_SPACE_TYPE = "spacetype";    
    
}
