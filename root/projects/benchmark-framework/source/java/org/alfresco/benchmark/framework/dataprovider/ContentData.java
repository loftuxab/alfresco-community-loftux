/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.benchmark.framework.dataprovider;

import java.io.File;

import org.alfresco.benchmark.framework.BenchmarkUtils;

public class ContentData
{
    private String contentLocation;
    private String mimetype;
    private String encoding;
    private int size;
    private String extension;
    
    public ContentData(String contentLocation, String mimetype, String encoding, int size, String extension)
    {
        this.contentLocation = contentLocation;
        this.mimetype = mimetype;
        this.encoding = encoding;
        this.size = size;
        this.extension = extension;
    }
    
    public File getFile()
    {
        return new File(contentLocation);
    }
    
    public String getMimetype()
    {
        return mimetype;
    }
    
    public String getEncoding()
    {
        return encoding;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public String getName()
    {
        return BenchmarkUtils.getGUID() + "." + this.extension;
    }
}