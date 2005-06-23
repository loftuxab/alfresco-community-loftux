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
package org.alfresco.web.ui.common.tag;

import javax.servlet.jsp.PageContext;

import org.alfresco.web.ui.common.converter.XMLDateConverter;
import org.apache.myfaces.taglib.core.ConvertDateTimeTag;

/**
 * Tag definition to use the XMLDateConverter on a page 
 *  
 * @author gavinc
 */
public class XMLDateConverterTag extends ConvertDateTimeTag
{
   public void setPageContext(PageContext context) 
   {
      super.setPageContext(context);
      setConverterId(XMLDateConverter.CONVERTER_ID);
   }
}
