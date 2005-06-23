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
package org.alfresco.config.xml.elementreader;

import org.dom4j.Element;

import org.alfresco.config.ConfigElement;

/**
 * Definition of an object responsible for converting the XML representation of a config
 * element into an in-memory object representation
 * 
 * @author gavinc
 */
public interface ConfigElementReader
{
   /**
    * Parses the given XML element into a ConfigElement object
    * 
    * @param element The XML element to parse
    * @return The object representation of the XML element
    */
   public ConfigElement parse(Element element);
}
