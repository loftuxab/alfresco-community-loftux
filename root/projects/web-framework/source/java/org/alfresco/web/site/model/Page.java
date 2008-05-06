/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.web.site.Framework;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Page model object
 * 
 * @author muzquiano
 */
public class Page extends AbstractModelObject
{
    public static String TYPE_NAME = "page";
    public static String PROP_TEMPLATE_INSTANCE = "template-instance";
    public static String ATTR_FORMAT_ID = "format-id";
    public static String PROP_ROOT_PAGE = "root-page";
    public static String PROP_PAGE_TYPE_ID = "page-type-id";
    public static String PROP_AUTHENTICATION = "authentication";
    public static String DEFAULT_PAGE_TYPE_ID = "generic";
    
    /**
     * Instantiates a new page for a given XML document
     * 
     * @param document the document
     */
    public Page(Document document)
    {
        super(document);
        
        // default page type
        if(getPageTypeId() == null)
        {
            setPageTypeId(DEFAULT_PAGE_TYPE_ID);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Page Instance: " + getId() + ", " + toXML();
    }

    /**
     * Gets the template id.
     * 
     * @return the template id
     */
    public String getTemplateId()
    {
        return getTemplateId(null);
    }

    /**
     * Gets the template id.
     * 
     * @param formatId the format id
     * 
     * @return the template id
     */
    public String getTemplateId(String formatId)
    {
        Element templateElement = getTemplateElement(formatId);
        if (templateElement != null)
        {
            return templateElement.getStringValue();
        }
        return null;
    }

    /**
     * Gets the template element.
     * 
     * @param formatId the format id
     * 
     * @return the template element
     */
    protected Element getTemplateElement(String formatId)
    {
        if (formatId != null && formatId.equals(Framework.getConfig().getDefaultFormatId()))
        {
            formatId = null;
        }
        
        Element result = null;
        
        List<Element> templateElements = getDocument().getRootElement().elements(PROP_TEMPLATE_INSTANCE);
        for (int i = 0; i < templateElements.size(); i++)
        {
            Element templateElement = templateElements.get(i);
            String _formatId = templateElement.attributeValue(ATTR_FORMAT_ID);
            if (_formatId != null && _formatId.length() == 0)
            {
                _formatId = null;
            }
            if (formatId == null)
            {
                if (_formatId == null || _formatId.length() == 0)
                {
                    result = templateElement;
                    break;
                }
            }
            else
            {
                if (formatId.equals(_formatId))
                {
                    result = templateElement;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Sets the template id.
     * 
     * @param templateId the new template id
     */
    public void setTemplateId(String templateId)
    {
        setTemplateId(templateId, null);
    }

    /**
     * Sets the template id.
     * 
     * @param templateId the template id
     * @param formatId the format id
     */
    public void setTemplateId(String templateId, String formatId)
    {
        if(formatId != null && formatId.equals(Framework.getConfig().getDefaultFormatId()))
        {
            formatId = null;
        }
        
        Element templateElement = getTemplateElement(formatId);
        if (templateElement == null)
        {
            templateElement = getDocument().getRootElement().addElement(
                    PROP_TEMPLATE_INSTANCE);
            if (formatId != null)
                templateElement.addAttribute(ATTR_FORMAT_ID, formatId);
        }
        templateElement.setText(templateId);
    }

    /**
     * Removes the template id.
     * 
     * @param formatId the format id
     */
    public void removeTemplateId(String formatId)
    {
        if(formatId != null && formatId.equals(Framework.getConfig().getDefaultFormatId()))
        {
            formatId = null;
        }
        
        Element templateElement = this.getTemplateElement(formatId);
        if (templateElement != null)
            templateElement.getParent().remove(templateElement);
    }

    /**
     * Gets the templates.
     * 
     * @param context the context
     * 
     * @return the templates
     */
    public Map<String, TemplateInstance> getTemplates(RequestContext context)
    {
        Map map = new HashMap(8, 1.0f);

        List templateElements = getDocument().getRootElement().elements(
                PROP_TEMPLATE_INSTANCE);
        for (int i = 0; i < templateElements.size(); i++)
        {
            Element templateElement = (Element) templateElements.get(i);
            String formatId = templateElement.attributeValue(ATTR_FORMAT_ID);
            if (formatId == null || formatId.length() == 0)
            {
                formatId = context.getConfig().getDefaultFormatId();
            }

            String templateId = templateElement.getStringValue();
            if (templateId != null)
            {
                TemplateInstance template = (TemplateInstance) context.getModel().loadTemplate(
                        context, templateId);
                map.put(formatId, template);
            }
        }

        return map;
    }

    /**
     * Gets the root page.
     * 
     * @return the root page
     */
    public boolean getRootPage()
    {
        return getBooleanProperty(PROP_ROOT_PAGE);
    }

    /**
     * Sets the root page.
     * 
     * @param b the new root page
     */
    public void setRootPage(boolean b)
    {
        this.setProperty(PROP_ROOT_PAGE, (b ? "true" : "false"));
    }

    /**
     * Gets the template.
     * 
     * @param context the context
     * 
     * @return the template
     */
    public TemplateInstance getTemplate(RequestContext context)
    {
        return getTemplate(context, null);
    }

    /**
     * Gets the template.
     * 
     * @param context the context
     * @param formatId the format id
     * 
     * @return the template
     */
    public TemplateInstance getTemplate(RequestContext context, String formatId)
    {
        String templateId = getTemplateId(formatId);
        if (templateId != null)
            return context.getModel().loadTemplate(context, templateId);
        return null;
    }

    /**
     * Gets the child pages.
     * 
     * @param context the context
     * 
     * @return the child pages
     */
    public Page[] getChildPages(RequestContext context)
    {
        PageAssociation[] associations = ModelUtil.findPageAssociations(
                context, this.getId(), null, PageAssociation.CHILD_ASSOCIATION_TYPE_ID);
        Page[] pages = new Page[associations.length];
        for (int i = 0; i < associations.length; i++)
        {
            Page childPage = (Page) associations[i].getDestPage(context);
            pages[i] = childPage;
        }
        return pages;
    }
    
    /**
     * Gets the page type id.
     * 
     * @return the page type id
     */
    public String getPageTypeId()
    {
        return this.getProperty(PROP_PAGE_TYPE_ID);        
    }
    
    /**
     * Sets the page type id.
     * 
     * @param pageTypeId the new page type id
     */
    public void setPageTypeId(String pageTypeId)
    {
        this.setProperty(PROP_PAGE_TYPE_ID, pageTypeId);        
    }
    
    /**
     * Gets the page type.
     * 
     * @param context the context
     * 
     * @return the page type
     */
    public PageType getPageType(RequestContext context)
    {
        String pageTypeId = getPageTypeId();
        if(pageTypeId != null)
        {
            return context.getModel().loadPageType(context, pageTypeId);
        }
        return null;
    }
       
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeName() 
    {
        return TYPE_NAME;
    }
}
