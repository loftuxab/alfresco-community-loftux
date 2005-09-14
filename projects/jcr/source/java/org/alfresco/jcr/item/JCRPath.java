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
package org.alfresco.jcr.item;

import java.util.StringTokenizer;

import org.alfresco.service.cmr.repository.Path;

/**
 * JCR Path Helper
 * 
 * @author David Caruana
 */
public class JCRPath
{
    private Path path;
    
    /**
     * Constuct path from string representation of path
     * 
     * @param strPath
     */
    public JCRPath(String strPath)
    {
        path = new Path();
        StringTokenizer tokenizer = new StringTokenizer(strPath, "/", false);
        while (tokenizer.hasMoreTokens())
        {
            path.append(new SimpleElement(tokenizer.nextToken()));
        }
    }
    
    /**
     * Return a new Path representing this path to the specified depth
     *  
     * @param depth  the path depth (0 based)
     * @return  the sub-path
     */
    public Path subPath(int depth)
    {
        return path.subPath(depth);
    }

    /**
     * @return  the count of path elements
     */
    public int size()
    {
        return path.size();
    }
    
    @Override
    public String toString()
    {
        return path.toString();
    }    

    /**
     * Simple Path Element used for building JCR Paths
     * 
     * @author David Caruana
     */
    public static class SimpleElement extends Path.Element
    {
        private static final long serialVersionUID = -6510331182652872996L;
        private String path;

        /**
         * @param path  path element name
         */
        public SimpleElement(String path)
        {
            this.path = path;
        }
        
        @Override
        public String getElementString()
        {
            return path;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o)
        {
            if(o == this)
            {
                return true;
            }
            if(!(o instanceof SimpleElement))
            {
                return false;
            }
            SimpleElement other = (SimpleElement)o;
            return this.path.equals(other.path);
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            return path.hashCode();
        }
    }

}
