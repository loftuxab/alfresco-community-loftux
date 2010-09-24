/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.directive;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.alfresco.wcm.client.Asset;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive to stream a repository asset's content to the HTTP
 * response. Usage: <@streamasset asset=xxx/> where xxx is a variable which
 * references an asset object
 * 
 * @author Chris Lack
 */
public class AssetDirective implements TemplateDirectiveModel
{
    private static ThreadLocal<char[]> streamBuffer = new ThreadLocal<char[]>()
    {
        @Override
        protected char[] initialValue()
        {
            return new char[10240];
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException
    {
        if (params.size() != 1)
        {
            throw new TemplateModelException("asset directive expects one parameter of type Asset");
        }

        StringModel assetParam = (StringModel) params.get("asset");
        if (assetParam == null || !(assetParam.getWrappedObject() instanceof Asset))
        {
            throw new TemplateModelException("asset directive expects asset parameter with a value of class Asset");
        }
        Asset asset = (Asset) assetParam.getWrappedObject();

        // Get the assets content stream
        InputStream stream = asset.getContentAsInputStream().getStream();
        Reader reader = new InputStreamReader(stream, "UTF-8");

        // Write the content stream to the servlet out
        Writer out = env.getOut();
        BufferedWriter bufWrite = new BufferedWriter(out);
        char[] buf = streamBuffer.get();
        int count;
        while ((count = reader.read(buf)) != -1)
        {
            bufWrite.write(buf, 0, count);
        }
        bufWrite.flush();
    }

}
