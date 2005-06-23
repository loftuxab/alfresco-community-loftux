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

import javax.faces.convert.Converter;
import javax.faces.webapp.ConverterTag;
import javax.servlet.jsp.JspException;

import org.alfresco.web.ui.common.converter.ByteSizeConverter;

/**
 * @author Kevin Roast
 */
public class ByteSizeConverterTag extends ConverterTag
{
   /**
    * Default Constructor
    */
   public ByteSizeConverterTag()
   {
      setConverterId(ByteSizeConverter.CONVERTER_ID);
   }

   /**
    * @see javax.faces.webapp.ConverterTag#createConverter()
    */
   protected Converter createConverter() throws JspException
   {
      return (ByteSizeConverter)super.createConverter();
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#setPageContext(javax.servlet.jsp.PageContext)
    */
   /*public void setPageContext(PageContext arg0)
   {
      // TODO: needed for MyFaces dodgy impl??? 
      setConverterId(ByteSizeConverter.CONVERTER_ID);
      super.setPageContext(arg0);
   }*/
}
