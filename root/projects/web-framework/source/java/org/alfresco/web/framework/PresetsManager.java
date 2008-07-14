/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.tools.XMLUtil;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.scripts.SearchPath;
import org.alfresco.web.scripts.Store;
import org.alfresco.web.site.Model;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * Spring util bean responsible for preset model object generation.
 * <p>
 * Presets are defined as XML snippets representing the model objects for a given set.
 * Each file can contain many presets each referenced by a unique ID. The preset definitions
 * can be located in any Store and any number of stores can be searched.
 * <p>
 * A set of parameterised model objects such as page, template instances and component
 * bindings can be defined for a preset. The XML for each model object definition is
 * effectively identical to that used to define the model object within its own file -
 * but nested within the preset structure as follows:
 * <pre>
 * <?xml version='1.0' encoding='UTF-8'?>
 * <presets>
 *     <preset id="someid">
 *         <components>
 *             ...
 *         </components>
 *         <pages>
 *             ...
 *         </pages>
 *         <template-instances>
 *             ...
 *         </template-instances>
 *     </preset>
 *     <preset id="anotherid">
 *         ...
 *     </preset>
 * </presets>
 * </pre>
 * One important difference to standard model object XML is that the ID for an object is
 * specified as an attribute on the parent element, for instance:
 * <pre><page id="user/${userid}/dashboard"></pre>
 * See the file slingshot\config\alfresco\site-data\presets\presets.xml for example usage.
 * <p>
 * Each preset supports parameterisation via "token" name/value pair replacements. For example:
 * <pre>
 *     <preset id="site-dashboard">
 *         <components>
 *             <component>
 *                 <scope>${scope}</scope>
 *                 <region-id>title</region-id>
 *                 <source-id>site/${siteid}/dashboard</source-id>
 *                 <url>/components/title/collaboration-title</url>
 *             </component>
 *             ...
 * </pre>
 * where the values of "${scope}" and "${siteid}" would be replaced if supplied in the token
 * map during preset construction. See the method constructPreset() below.
 * 
 * @author Kevin Roast
 */
public class PresetsManager
{
    private SearchPath searchPath;
    private List<String> files;
    
    private Document[] documents;
    
    
    /**
     * @param searchPath        the SearchPath to set
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    /**
     * @param files             the preset files list to set
     */
    public void setFiles(List<String> files)
    {
        this.files = files;
    }
    
    
    /**
     * Initialise the presets manager
     */
    private void init()
    {
        if (this.searchPath == null || this.files == null)
        {
            throw new IllegalArgumentException("SearchPath and Files list are mandatory.");
        }
        
        // search for our preset XML descriptor documents
        List<Document> docs = new ArrayList<Document>(4);
        for (Store store : this.searchPath.getStores())
        {
            for (String file : this.files)
            {
                if (store.hasDocument(file))
                {
                    try
                    {
                        docs.add(XMLUtil.parse(store.getDocument(file)));
                    }
                    catch (IOException ioe)
                    {
                        throw new AlfrescoRuntimeException("Error loading presets XML file: " +
                                file + " in store: " + store.toString(), ioe);
                    }
                    catch (DocumentException de)
                    {
                        throw new AlfrescoRuntimeException("Error processing presets XML file: " +
                                file + " in store: " + store.toString(), de);
                    }
                }
            }
        }
        this.documents = docs.toArray(new Document[docs.size()]);
    }
    
    /**
     * Construct the model objects for a given preset.
     * Objects persist to the default store for the appropriate object type.
     * 
     * @param id        Preset ID to use
     * @param tokens    Name value pair tokens to replace in preset definition
     */
    public void constructPreset(Model model, String id, Map<String, String> tokens)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Preset ID is mandatory.");
        }
        
        // perform one time init - this cannot be perform in an app handler or by the
        // framework init - as it requires the Alfresco server to be started...
        synchronized (this)
        {
            if (this.documents == null)
            {
                init();
            }
        }
        
        for (Document doc : this.documents)
        {
            for (Element preset : (List<Element>)doc.getRootElement().elements("preset"))
            {
                // found preset with matching id?
                if (id.equals(preset.attributeValue("id")))
                {
                    // any components in the preset?
                    Element components = preset.element("components");
                    if (components != null)
                    {
                        for (Element c : (List<Element>)components.elements("component"))
                        {
                            // apply token replacement to each value as it is retrieved
                            String title = replace(c.elementTextTrim(Component.PROP_TITLE), tokens);
                            String description = replace(c.elementTextTrim(Component.PROP_DESCRIPTION), tokens);
                            String typeId = replace(c.elementTextTrim(Component.PROP_COMPONENT_TYPE_ID), tokens);
                            String scope = replace(c.elementTextTrim(Component.PROP_SCOPE), tokens);
                            String regionId = replace(c.elementTextTrim(Component.PROP_REGION_ID), tokens);
                            String sourceId = replace(c.elementTextTrim(Component.PROP_SOURCE_ID), tokens);
                            String url = replace(c.elementTextTrim(Component.PROP_URL), tokens);
                            String chrome = replace(c.elementTextTrim(Component.PROP_CHROME), tokens);
                            
                            // validate mandatory values
                            if (scope == null || scope.length() == 0)
                            {
                                throw new IllegalArgumentException("Scope is a mandatory property for a component preset.");
                            }
                            if (regionId == null || regionId.length() == 0)
                            {
                                throw new IllegalArgumentException("RegionID is a mandatory property for a component preset.");
                            }
                            if (sourceId == null || sourceId.length() == 0)
                            {
                                throw new IllegalArgumentException("SourceID is a mandatory property for a component preset.");
                            }
                            
                            // generate component
                            Component component = model.newComponent(scope, regionId, sourceId);
                            component.setComponentTypeId(typeId);
                            component.setTitle(title);
                            component.setDescription(description);
                            component.setURL(url);
                            component.setChrome(chrome);
                            
                            // apply arbituary custom properties
                            if (c.element("properties") != null)
                            {
                                for (Element prop : (List<Element>)c.element("properties").elements())
                                {
                                    String propName = replace(prop.getName(), tokens);
                                    String propValue = replace(prop.getTextTrim(), tokens);
                                    component.setCustomProperty(propName, propValue);
                                }
                            }
                            
                            // persist the object
                            model.saveObject(component);
                        }
                    }
                    
                    // any pages in the preset?
                    Element pages = preset.element("pages");
                    if (pages != null)
                    {
                        for (Element p : (List<Element>)pages.elements("page"))
                        {
                            // apply token replacement to each value as it is retrieved
                            String pageId = replace(p.attributeValue(Page.PROP_ID), tokens);
                            String title = replace(p.elementTextTrim(Page.PROP_TITLE), tokens);
                            String description = replace(p.elementTextTrim(Page.PROP_DESCRIPTION), tokens);
                            String typeId = replace(p.elementTextTrim(Page.PROP_PAGE_TYPE_ID), tokens);
                            String auth = replace(p.elementTextTrim(Page.PROP_AUTHENTICATION), tokens);
                            String template = replace(p.elementTextTrim(Page.PROP_TEMPLATE_INSTANCE), tokens);
                            
                            // validate mandatory values
                            if (pageId == null || pageId.length() == 0)
                            {
                                throw new IllegalArgumentException("ID is a mandatory attribute for a page preset.");
                            }
                            if (template == null || template.length() == 0)
                            {
                                throw new IllegalArgumentException("Template is a mandatory property for a page preset.");
                            }
                            
                            // generate page
                            Page page = model.newPage(pageId);
                            page.setPageTypeId(typeId);
                            page.setTitle(title);
                            page.setDescription(description);
                            page.setAuthentication(auth);
                            page.setTemplateId(template);
                            
                            // apply arbituary custom properties
                            if (p.element("properties") != null)
                            {
                                for (Element prop : (List<Element>)p.element("properties").elements())
                                {
                                    String propName = replace(prop.getName(), tokens);
                                    String propValue = replace(prop.getTextTrim(), tokens);
                                    page.setCustomProperty(propName, propValue);
                                }
                            }
                            
                            // persist the object
                            model.saveObject(page);
                        }
                    }
                    
                    // any template instances in the preset?
                    Element templates = preset.element("template-instances");
                    if (templates != null)
                    {
                        for (Element t : (List<Element>)templates.elements("template-instance"))
                        {
                            // apply token replacement to each value as it is retrieved
                            String templateId = replace(t.attributeValue(TemplateInstance.PROP_ID), tokens);
                            String title = replace(t.elementTextTrim(TemplateInstance.PROP_TITLE), tokens);
                            String description = replace(t.elementTextTrim(TemplateInstance.PROP_DESCRIPTION), tokens);
                            String templateType = replace(t.elementTextTrim(TemplateInstance.PROP_TEMPLATE_TYPE), tokens);
                            
                            // validate mandatory values
                            if (templateId == null || templateId.length() == 0)
                            {
                                throw new IllegalArgumentException("ID is a mandatory attribute for a template-instance preset.");
                            }
                            if (templateType == null || templateType.length() == 0)
                            {
                                throw new IllegalArgumentException("Template is a mandatory property for a page preset.");
                            }
                            
                            // generate template-instance
                            TemplateInstance template = model.newTemplate(templateId);
                            template.setTitle(title);
                            template.setDescription(description);
                            template.setTemplateType(templateType);
                            
                            // apply arbituary custom properties
                            if (t.element("properties") != null)
                            {
                                for (Element prop : (List<Element>)t.element("properties").elements())
                                {
                                    String propName = replace(prop.getName(), tokens);
                                    String propValue = replace(prop.getTextTrim(), tokens);
                                    template.setCustomProperty(propName, propValue);
                                }
                            }
                            
                            // persist the object
                            model.saveObject(template);
                        }
                    }
                    
                    // TODO: any chrome, associations, types, themes etc. in the preset...
                    
                    // found our preset - no need to process further
                    break;
                }
            }
        }
    }
    
    
    /**
     * Replace token strings - marked by ${...} in the given string with
     * the supplied tokens.
     * 
     * @param s         String to process (can be null - will return null)
     * @param tokens    Token map (can be null - will return original string)
     * 
     * @return replaced string or null if input is null or original string if tokens is null
     */
    private static String replace(String s, Map<String, String> tokens)
    {
        if (s != null && tokens != null)
        {
            for (Entry<String, String> entry : tokens.entrySet())
            {
                String key = "${" + entry.getKey() + "}";
                s = s.replace(key, entry.getValue());
            }
        }
        
        return s;
    }
}
