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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.config.ScriptConfigModel;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author muzquiano
 */
public final class ScriptWizard extends ScriptBase
{
    public ScriptWizard(RequestContext context)
    {
        super(context);
    }

    protected HttpServletRequest getHttpServletRequest()
    {
        if (context instanceof HttpRequestContext)
        {
            return ((HttpRequestContext) context).getRequest();
        }
        return null;
    }

    // API

    protected JSONObject request = null;
    protected JSONObject response = null;
    protected String id = null;
    protected String currentPageId = null;
    protected String previousPageId = null;
    protected boolean isCurrentPageStart = false;
    protected boolean isCurrentPageEnd = false;

    public void setResponse(String key, String value)
    {
        try
        {
            response.put(key, value);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public void setResponse(String key, boolean value)
    {
        try
        {
            response.put(key, value);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public void setResponse(String key, Object value)
    {
        try
        {
            response.put(key, value);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void init() throws JSONException, DocumentException
    {
        // arguments
        Map args = (Map) getModel().get("args");
        String json = (String) args.get("json");

        // debug handling
        String debug = (String) args.get("debug");
        if ("true".equals(debug))
        {
            JSONObject o = new JSONObject();
            o.put("schema", "adw10");
            o.put("windowId", "test-window-id");
            json = o.toString();
        }
        init(json);
    }

    public void init(String jsonRequestString) throws JSONException,
            DocumentException
    {
        // build the request and response objects
        request = new JSONObject(jsonRequestString);
        response = new JSONObject();

        // set the schema
        setResponse("schema", "adw10");

        // copy in things from the original request
        response.put("windowId", request.get("windowId"));

        // copy in the previous and current pages
        try
        {
            String _previousPageId = (String) request.get("currentPageId");
            if (_previousPageId != null)
                previousPageId = _previousPageId;
        }
        catch (Exception ex)
        {
        }
        try
        {
            String _currentPageId = (String) request.get("requestedPageId");
            if (_currentPageId != null)
                currentPageId = _currentPageId;
        }
        catch (Exception ex)
        {
        }

        // process the request
        processRequest();
    }

    protected void processRequest() throws DocumentException, JSONException
    {
        // arguments
        //Map args = (Map) getModel().get("args");
        Description webscript = (Description) getModel().get("webscript");

        // load the wizard configuration
        ScriptConfigModel config = (ScriptConfigModel) getModel().get("config");
        String xml = (String) config.getScript();

        // parse the wizard configuration
        Document doc = DocumentHelper.parseText(xml);
        Element root = doc.getRootElement();
        String wizardId = root.attribute("id").getStringValue();
        setId(wizardId);

        // the current page element
        Element currentPageElement = null;

        // some properties
        String title = root.elementText("title");
        setResponseTitle(title);
        String uri = null;
        String[] uris = webscript.getURIs();
        if (uris != null)
        {
            uri = uris[0];
            setResponseURI(uri);
        }

        // are we being told to "refresh" the wizard?
        try
        {
            if (request.getBoolean("refreshSession"))
            {
                this.sessionRemove(getId());
            }
        }
        catch (Exception ex)
        {
        }

        // walk the pages
        List pages = root.elements("page");
        for (int i = 0; i < pages.size(); i++)
        {
            Element page = (Element) pages.get(i);
            String pageId = page.attributeValue("id");
            String start = page.attributeValue("start");
            String end = page.attributeValue("end");

            // if the current page is null and this is the start page, use it
            if (currentPageId == null && "true".equals(start))
                currentPageId = pageId;

            boolean finish = false;
            if ("true".equals(end))
                finish = true;

            // add the page to the json output
            addPage(pageId, uri, finish);

            // IF THIS IS THE CURRENT PAGE, PROCESS BUTTONS, ETC
            if (currentPageId != null && currentPageId.equals(pageId))
            {
                // mark the current page
                currentPageElement = page;

                // properties about the current page
                if ("true".equals(end))
                    isCurrentPageEnd = true;
                if ("true".equals(start))
                    isCurrentPageStart = true;

                // walk the buttons
                Element buttonsElement = page.element("buttons");
                if (buttonsElement != null)
                {
                    List buttons = buttonsElement.elements("button");
                    for (int j = 0; j < buttons.size(); j++)
                    {
                        Element button = (Element) buttons.get(j);
                        String buttonId = (String) button.attributeValue("id");
                        String buttonLabel = (String) button.attributeValue("label");
                        String buttonAction = (String) button.attributeValue("action");
                        String buttonData = (String) button.attributeValue("data");
                        String buttonEnabledString = (String) button.attributeValue("enabled");
                        boolean buttonEnabled = true;
                        if ("false".equals(buttonEnabledString))
                            buttonEnabled = false;

                        // PUSH THIS BUTTON TO DIALOG OUTPUT
                        addButton(buttonId, buttonLabel, buttonAction,
                                buttonData, buttonEnabled);
                    }
                }
            }

            // IF THIS IS THE PREVIOUS PAGE, PROCESS ELEMENT BINDINGS
            if (previousPageId != null && previousPageId.equals(pageId))
            {
                // walk the data bindings
                Element dataElement = page.element("data");
                if (dataElement != null)
                {
                    List elements = dataElement.elements("element");
                    for (int k = 0; k < elements.size(); k++)
                    {
                        Element element = (Element) elements.get(k);
                        String elementId = (String) element.attributeValue("id");

                        // push into the wizard session
                        JSONArray requestElements = null;
                        try
                        {
                            requestElements = request.getJSONArray("elements");
                        }
                        catch (JSONException jsonEx)
                        {
                        }
                        if (requestElements != null)
                        {
                            for (int z = 0; z < requestElements.length(); z++)
                            {
                                JSONObject requestElement = (JSONObject) requestElements.get(z);
                                String requestElementName = (String) requestElement.get("name");
                                if (requestElementName != null && requestElementName.equals(elementId))
                                {
                                    String requestElementValue = (String) requestElement.get("value");

                                    this.sessionPut(requestElementName,
                                            requestElementValue);
                                }
                            }
                        }
                    }
                }
            }
        }

        // set the current page ID
        current().put("id", currentPageId);
        current().put("isStart", isCurrentPageStart);
        current().put("isEnd", isCurrentPageEnd);

        // processing for the current page
        if (currentPageElement != null)
        {
            // process type
            String type = currentPageElement.attributeValue("type");
            if (type != null && type.length() != 0)
            {
                this.setDialogType(type);
            }

            // process data elements (put in scope)			
            Element dataEl = currentPageElement.element("data");
            if (dataEl != null)
            {
                List elementList = dataEl.elements("element");
                for (int u = 0; u < elementList.size(); u++)
                {
                    Element el = (Element) elementList.get(u);

                    String elName = (String) el.attributeValue("id");
                    String elType = (String) el.attributeValue("type");
                    String elDefaultValue = (String) el.attributeValue("defaultValue");

                    if ("String".equals(elType))
                    {
                        String elValue = (String) sessionGet(elName,
                                elDefaultValue);
                        getModel().put(elName, elValue);
                    }
                }
            }
        }
    }

    public String getCurrentPageId()
    {
        return this.currentPageId;
    }

    public boolean isCurrentPageStart()
    {
        return this.isCurrentPageStart;
    }

    public boolean isCurrentPageEnd()
    {
        return this.isCurrentPageEnd;
    }

    public void setCurrentPageId(String currentPageId)
    {
        this.currentPageId = currentPageId;
    }

    public void finalize()
    {
        Map model = (Map) getModel().get("model");
        model.put("json", response.toString());
    }

    public String request(String name) throws JSONException
    {
        return (String) request.getString(name);
    }

    //////////////////////////////////////////////////////////
    //
    // CURRENT PAGE METHODS
    //
    //////////////////////////////////////////////////////////

    public void setCacheInvalidateAll() throws JSONException
    {
        current().put("cacheInvalidateAll", true);
    }

    public void setBrowserReload(boolean reload) throws JSONException
    {
        current().put("reload", reload);
    }

    public void setDialogType(String dialogType) throws JSONException
    {
        current().put("dialogtype", dialogType);
    }

    public void setDialogForm() throws JSONException
    {
        setDialogType("form");
    }

    public void setDialogHTML(String html) throws JSONException
    {
        setDialogType("html");
        current().put("html", html);
    }

    public void setDialogURL(String url) throws JSONException
    {
        setDialogType("url");
        current().put("url", url);
    }

    public void setResponseCode(String code) throws JSONException
    {
        current().put("code", code);
    }

    public void setResponseCodeOK() throws JSONException
    {
        setResponseCode("ok");
    }

    public void setResponseCodeFinish() throws JSONException
    {
        setCacheInvalidateAll();
        setResponseCode("finish");
    }

    public void setResponseMessage(String message) throws JSONException
    {
        current().put("message", message);
    }

    public void setResponseTitle(String title) throws JSONException
    {
        current().put("title", title);
    }

    public void setResponseURI(String uri) throws JSONException
    {
        current().put("uri", uri);
    }

    public void setFormFieldFocus(String id, String selectText, String focusCls)
            throws JSONException
    {
        JSONObject focus = new JSONObject();
        focus.put("id", id);
        if (selectText != null)
            focus.put("selectText", selectText);
        if (focusCls != null)
            focus.put("focusCls", focusCls);
        current().put("focus", focus);
    }

    //////////////////////////////////////////////////////////
    //
    // JSON STRUCTURE METHODS
    //
    //////////////////////////////////////////////////////////

    protected JSONObject data() throws JSONException
    {
        return ensureJSONObject(response, "data");
    }

    protected JSONArray elements() throws JSONException
    {
        return ensureJSONArray(response, "elements");
    }

    protected JSONArray elementFormats() throws JSONException
    {
        return ensureJSONArray(response, "elementformats");
    }

    protected JSONObject elementValues() throws JSONException
    {
        return ensureJSONObject(response, "elementvalues");
    }

    protected JSONArray buttons() throws JSONException
    {
        return ensureJSONArray(response, "buttons");
    }

    protected JSONArray pages() throws JSONException
    {
        return ensureJSONArray(response, "pages");
    }

    protected JSONObject grid() throws JSONException
    {
        return ensureJSONObject(response, "grid");
    }

    protected JSONObject current() throws JSONException
    {
        return ensureJSONObject(response, "current");
    }

    /// Helpful JSON manip methods

    protected JSONObject ensureJSONObject(JSONObject obj, String objectName)
            throws JSONException
    {
        JSONObject o = null;
        try
        {
            o = obj.getJSONObject(objectName);
        }
        catch (Exception ex)
        {
        }
        if (o == null)
        {
            o = new JSONObject();
            obj.put(objectName, o);
        }
        return o;
    }

    protected JSONArray ensureJSONArray(JSONObject obj, String arrayName)
            throws JSONException
    {
        JSONArray a = null;
        try
        {
            a = obj.getJSONArray(arrayName);
        }
        catch (Exception ex)
        {
        }
        if (a == null)
        {
            a = new JSONArray();
            obj.put(arrayName, a);
        }
        return a;
    }

    //////////////////////////////////////////////////////////
    //
    // ELEMENTS
    //
    //////////////////////////////////////////////////////////

    public void addElement(String name, String value) throws JSONException
    {
        JSONObject element = new JSONObject();
        element.put("name", name);
        element.put("value", value);
        elements().put(element);
    }

    public void addHiddenElement(String name, String value)
            throws JSONException
    {
        addElement(name, value);
        addElementFormat(name, null, null);
        addElementFormatKeyPair(name, "hidden", true);
    }

    public void updateElement(String name, String value) throws JSONException
    {
        if (name == null)
            return;

        JSONArray elements = elements();
        for (int i = 0; i < elements.length(); i++)
        {
            JSONObject element = (JSONObject) elements.get(i);
            String _name = (String) element.get("name");
            if (name.equals(_name))
                element.put("value", value);
        }
    }

    public void addElementFormat(String name, String label, String type)
            throws JSONException
    {
        addElementFormat(name, label, type, -1, -1);
    }

    public void addElementFormat(String name, String label, String type,
            int width) throws JSONException
    {
        addElementFormat(name, label, type, width, -1);
    }

    public void addElementFormat(String name, String label, String type,
            int width, int height) throws JSONException
    {
        JSONArray elementFormats = elementFormats();

        JSONObject element = new JSONObject();
        element.put("name", name);

        if (label != null)
            element.put("label", label);
        if (type != null)
            element.put("type", type);
        if (width != -1)
            element.put("width", width);
        if (height != -1)
            element.put("height", height);

        elementFormats.put(element);
    }

    public void addElementFormatKeyPair(String name, String key, String value)
            throws JSONException
    {
        JSONArray elementFormats = elementFormats();

        for (int i = 0; i < elementFormats.length(); i++)
        {
            JSONObject elementFormat = (JSONObject) elementFormats.get(i);
            String _name = (String) elementFormat.get("name");
            if (name.equals(_name))
                elementFormat.put(key, value);
        }
    }

    public void addElementFormatKeyPair(String name, String key, boolean value)
            throws JSONException
    {
        JSONArray elementFormats = elementFormats();

        for (int i = 0; i < elementFormats.length(); i++)
        {
            JSONObject elementFormat = (JSONObject) elementFormats.get(i);
            String _name = (String) elementFormat.get("name");
            if (name.equals(_name))
                elementFormat.put(key, value);
        }
    }

    public void addElementSelectionValue(String name, String selectionId,
            String selectionValue) throws JSONException
    {
        JSONObject elementValues = elementValues();
        JSONArray values = ensureJSONArray(elementValues, name);

        // create our value tuple
        JSONArray ourValue = new JSONArray();
        ourValue.put(selectionId);
        ourValue.put(selectionValue);

        // add our value into the values array		
        values.put(ourValue);
    }

    public Object getElementValue(String name) throws JSONException
    {
        JSONArray elements = elements();
        for (int i = 0; i < elements.length(); i++)
        {
            JSONObject element = (JSONObject) elements.get(i);
            String _name = (String) element.get("name");
            if (name.equals(_name))
                return element.get("value");
        }
        return null;
    }

    public String getElementStringValue(String name) throws JSONException
    {
        return (String) getElementValue(name);
    }

    //////////////////////////////////////////////////////////
    //
    // BUTTONS
    //
    //////////////////////////////////////////////////////////

    public void addButton(String id, String text, String action, String data,
            boolean enabled) throws JSONException
    {
        JSONObject button = new JSONObject();
        button.put("id", id);
        if (text != null)
            button.put("text", text);
        if (action != null)
            button.put("action", action);
        if (data != null)
            button.put("data", data);
        button.put("enabled", enabled);

        buttons().put(button);
    }

    //////////////////////////////////////////////////////////
    //
    // PAGE STATES
    //
    //////////////////////////////////////////////////////////

    public void addPage(String id, String uri, boolean finish)
            throws JSONException
    {
        JSONObject page = new JSONObject();
        page.put("id", id);
        if (uri != null)
            page.put("uri", uri);
        page.put("finish", finish);
        pages().put(page);
    }

    //////////////////////////////////////////////////////////
    //
    // GRID
    //
    //////////////////////////////////////////////////////////

    /*
     public void addGridData(String gridData)
     throws JSONException
     {
     JSONArray griddata = ensureJSONArray(grid(), "griddata");
     griddata.put(gridData);
     }
     */
    public void addGridData(Object[] array) throws JSONException
    {
        JSONArray griddata = ensureJSONArray(grid(), "griddata");

        JSONArray data = new JSONArray();
        for (int i = 0; i < array.length; i++)
            data.put(array[i]);

        griddata.put(data);
    }

    public void addGridColumn(String id, String text) throws JSONException
    {
        JSONArray columns = ensureJSONArray(grid(), "columns");

        JSONObject column = new JSONObject();
        column.put("id", id);
        column.put("text", text);
        column.put("dataIndex", id);
        columns.put(column);
    }

    public void addGridColumnFormat(String id, String width, String sortable)
            throws JSONException
    {
        JSONArray columnformats = ensureJSONArray(grid(), "columnformats");

        JSONObject column = new JSONObject();
        column.put("id", id);
        if (width != null)
            column.put("width", width);
        if (sortable != null)
            column.put("sortable", sortable);
        columnformats.put(column);
    }

    public void addGridToolbar(String id, String text, String tooltip,
            String iconCls) throws JSONException
    {
        JSONArray toolbar = ensureJSONArray(grid(), "toolbar");

        JSONObject item = new JSONObject();
        item.put("id", id);
        if (text != null)
            item.put("text", text);
        if (tooltip != null)
            item.put("tooltip", tooltip);
        if (iconCls != null)
            item.put("iconCls", iconCls);
        toolbar.put(item);
    }

    public void addGridToolbarSpacer() throws JSONException
    {
        JSONArray toolbar = ensureJSONArray(grid(), "toolbar");
        toolbar.put("-");
    }

    public void addGridNoDataMessage(String message) throws JSONException
    {
        grid().put("nodatamessage", message);
    }

    //////////////////////////////////////////////////////////
    //
    // WIZARD STATE
    //
    //////////////////////////////////////////////////////////

    protected Map ensureMap()
    {
        HttpServletRequest r = getHttpServletRequest();
        if (r != null)
        {
            Map map = (Map) r.getSession().getAttribute(
                    "scriptWizard-state-" + getId());
            if (map == null)
            {
                map = new HashMap();
                r.getSession().setAttribute("scriptWizard-state-" + getId(),
                        map);
            }
            return map;
        }
        return null;
    }

    public void sessionPut(String name, String value)
    {
        Map map = ensureMap();
        map.put(name, value);
    }

    public String sessionGet(String name)
    {
        Map map = ensureMap();
        return (String) map.get(name);
    }

    public String sessionGet(String name, String defaultValue)
    {
        Map map = ensureMap();
        String result = (String) map.get(name);
        if (result == null)
            result = defaultValue;
        return result;
    }

    public void sessionRemove(String name)
    {
        HttpServletRequest r = getHttpServletRequest();
        if (r != null)
        {
            r.getSession().removeAttribute("scriptWizard-state-" + getId());
        }
    }

    //////////////////////////////////////////////////////////
    //
    // TOOLS
    //
    //////////////////////////////////////////////////////////

    public String getResponse()
    {
        return response.toString();
    }

    public void putResponse(String str) throws JSONException
    {
        response = new JSONObject(str);
    }

    public String getSafeProperty(ScriptModelObject obj, String propertyName)
    {
        String value = obj.getProperty(propertyName);
        if (value == null)
            value = "";
        return value;
    }

    public String getSafeElementValue(String elementName) throws JSONException
    {
        String value = this.getElementStringValue(elementName);
        if (value == null)
            value = "";
        return value;
    }
    
    //////////////////////////
    // To be removed
    //////////////////////////
    public String getSafeSetting(ScriptModelObject obj, String settingName)
    {
        String value = obj.getSetting(settingName);
        if (value == null)
            value = "";
        return value;
    }
    

}
