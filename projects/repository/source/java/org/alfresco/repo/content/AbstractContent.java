/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.content;

import org.alfresco.service.cmr.repository.Content;

/**
 * Provides basic information for <tt>Content</tt>.
 * 
 * @author Derek Hulley
 */
public class AbstractContent implements Content
{
    private String contentUrl;
    private String mimetype;
    private String encoding;

    /**
     * @param contentUrl the content URL
     */
    protected AbstractContent(String contentUrl)
    {
        if (contentUrl == null || contentUrl.length() == 0)
        {
            throw new IllegalArgumentException("contentUrl must be a valid String");
        }
        this.contentUrl = contentUrl;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Content")
          .append("[ url=").append(contentUrl)
          .append(", mimetype=").append(mimetype)
          .append(", encoding=").append(encoding)
          .append("]");
        return sb.toString();
    }
    
    public String getContentUrl()
    {
        return contentUrl;
    }
    
    public String getMimetype()
    {
        return mimetype;
    }

    /**
     * @param mimetype the underlying content's mimetype - null if unknown
     */
    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }
    
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param encoding the underlying content's encoding - null if unknown
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
}
