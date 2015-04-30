/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.httpclient.methods.multipart.PartBase;

/**
 * For sending JSON data in a multi-part HttpClient request.
 * 
 * Assumes the JSON isn't scarily large, so buffers everything internally
 *  
 * @author Nick Burch
 * @since TODO
 */
public class JSONPart extends PartBase
{
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private byte[] jsonBytes;
    
    public JSONPart(String json)
    {
        super("json", MimetypeMap.MIMETYPE_JSON, "utf-8", "8bit");
        
        this.jsonBytes = json.getBytes(UTF_8);
    }
    
    public String getJSON()
    {
        return new String(jsonBytes, UTF_8);
    }

    @Override
    protected long lengthOfData() throws IOException
    {
        return jsonBytes.length;
    }

    @Override
    protected void sendData(OutputStream outputStream) throws IOException
    {
        outputStream.write(jsonBytes);
    }
}
