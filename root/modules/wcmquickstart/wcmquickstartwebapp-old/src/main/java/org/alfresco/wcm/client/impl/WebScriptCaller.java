package org.alfresco.wcm.client.impl;

import java.util.List;

import org.json.JSONObject;

public interface WebScriptCaller
{

    public abstract JSONObject getJsonObject(String servicePath, List<WebscriptParam> params);

}