package org.alfresco.web.site;

import org.alfresco.web.config.WebFrameworkConfigElement;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.Chrome;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.PageType;
import org.alfresco.web.site.model.TemplateInstance;
import org.alfresco.web.site.model.TemplateType;
import org.alfresco.web.site.model.Theme;

public interface Model
{
    // returns the file system impl used for storing/retrieving model objects
    public IFileSystem getFileSystem();

    // load
    
    public Chrome loadChrome(RequestContext context, String id);
    
    public Component loadComponent(RequestContext context, String id);

    public ComponentType loadComponentType(RequestContext context, String id);

    public Configuration loadConfiguration(RequestContext context, String id);

    public ContentAssociation loadContentAssociation(RequestContext context,
            String id);

    public Page loadPage(RequestContext context, String id);
    
    public PageType loadPageType(RequestContext context, String id);

    public PageAssociation loadPageAssociation(RequestContext context, String id);

    public TemplateInstance loadTemplate(RequestContext context, String id);

    public TemplateType loadTemplateType(RequestContext context, String id);
    
    public Theme loadTheme(RequestContext context, String id);

    // instantiation
    
    public Chrome newChrome(RequestContext context);
    
    public Component newComponent(RequestContext context);

    public ComponentType newComponentType(RequestContext context);

    public Configuration newConfiguration(RequestContext context);

    public ContentAssociation newContentAssociation(RequestContext context);

    public Page newPage(RequestContext context);
    
    public PageType newPageType(RequestContext context);

    public PageAssociation newPageAssociation(RequestContext context);

    public TemplateInstance newTemplate(RequestContext context);

    public TemplateType newTemplateType(RequestContext context);
    
    public Theme newTheme(RequestContext context);

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
    public WebFrameworkConfigElement getConfiguration();
}
