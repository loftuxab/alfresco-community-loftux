package org.alfresco.web.site;

import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.Template;
import org.alfresco.web.site.model.TemplateType;

public interface IModel
{
    // returns the file system impl used for storing/retrieving model objects
    public IFileSystem getFileSystem();

    // load
    public Component loadComponent(RequestContext context, String id);

    public ComponentType loadComponentType(RequestContext context, String id);

    public Configuration loadConfiguration(RequestContext context, String id);

    public ContentAssociation loadContentAssociation(RequestContext context,
            String id);

    public Endpoint loadEndpoint(RequestContext context, String id);

    public Page loadPage(RequestContext context, String id);

    public PageAssociation loadPageAssociation(RequestContext context, String id);

    public Template loadTemplate(RequestContext context, String id);

    public TemplateType loadTemplateType(RequestContext context, String id);

    // instantiation
    public Component newComponent(RequestContext context);

    public ComponentType newComponentType(RequestContext context);

    public Configuration newConfiguration(RequestContext context);

    public ContentAssociation newContentAssociation(RequestContext context);

    public Endpoint newEndpoint(RequestContext context);

    public Page newPage(RequestContext context);

    public PageAssociation newPageAssociation(RequestContext context);

    public Template newTemplate(RequestContext context);

    public TemplateType newTemplateType(RequestContext context);

    // generic
    public void saveObject(RequestContext context, ModelObject obj);

    public ModelObject loadObject(RequestContext context, String typeId, String id);
    
    public ModelObject loadObject(RequestContext context, String id);

    public ModelObject loadObject(RequestContext context, IFile file);

    public void removeObject(RequestContext context, ModelObject obj);

    public ModelObject newObject(RequestContext context, String tagName);

    public ModelObject[] loadObjects(RequestContext context, String typeName);

    // guids
    public String newGUID();

    public String newGUID(String typeName);

    // configuration
    public AbstractConfig getConfiguration();
}
