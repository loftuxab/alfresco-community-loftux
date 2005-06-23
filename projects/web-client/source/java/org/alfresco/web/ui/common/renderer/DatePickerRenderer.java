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
package org.alfresco.web.ui.common.renderer;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

/**
 * @author kevinr
 * 
 * Example of a custom JSF renderer. This demonstrates how to encode/decode a set
 * of input field params that we use to generate a Date object. This object is held
 * in our component and the renderer will output it to the page.
 */
public class DatePickerRenderer extends BaseRenderer
{
   /**
    * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    * 
    * The decode method takes the parameters from the external requests, finds the
    * ones revelant to this component and decodes the results into an object known
    * as the "submitted value".
    */
   public void decode(FacesContext context, UIComponent component)
   {
      // TODO: should check for disabled/readonly here - no need to decode
      String clientId = component.getClientId(context);
      Map params = context.getExternalContext().getRequestParameterMap();
      String year = (String)params.get(clientId + "_year");
      if (year != null)
      {
         // found data for our component
         String month = (String)params.get(clientId + "_month");
         String day = (String)params.get(clientId + "_day");
         
         // we encode the values needed for the component as we see fit
         int[] parts = new int[3];
         parts[0] = Integer.parseInt(year);
         parts[1] = Integer.parseInt(month);
         parts[2] = Integer.parseInt(day);
         
         // save the data in an object for our component as the "EditableValueHolder"
         // all UI Input Components support this interface for the submitted value
         ((EditableValueHolder)component).setSubmittedValue(parts);
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#getConvertedValue(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    * 
    * In the Process Validations phase, this method is called to convert the values
    * to the datatype as required by the component.
    * 
    * It is possible at this point that a custom Converter instance will be used - this
    * is why we have not yet converted the values to a data type.
    */
   public Object getConvertedValue(FacesContext context, UIComponent component, Object val) throws ConverterException
   {
      int[] parts = (int[])val;
      Date date = new Date(parts[0] - 1900,
                           parts[1] - 1,
                           parts[2]);
      
      return date;
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    * 
    * All rendering logic for this component is implemented here. A renderer for an
    * input component must render the submitted value if it's set, and use the local
    * value only if there is no submitted value.
    */
   public void encodeBegin(FacesContext context, UIComponent component)
         throws IOException
   {
      // always check for this flag - as per the spec
      if (component.isRendered() == true)
      {
         Date date = null;
         
         // this is part of the spec:
         // first you attempt to build the date from the submitted value
         int[] submittedValue = (int[])((EditableValueHolder)component).getSubmittedValue();
         if (submittedValue != null)
         {
            date = (Date)getConvertedValue(context, component, submittedValue);
         }
         else
         {
            // second if no submitted value is found, default to the current value
            Object value = ((ValueHolder)component).getValue();
            // finally check for null value and create default if needed
            date = value instanceof Date ? (Date)value : new Date();
         }
         
         // get the attributes from the component we need for rendering
         int nStartYear = 1990;
         Integer startYear = (Integer)component.getAttributes().get("startYear");
         if (startYear != null)
         {
            nStartYear = startYear.intValue();
         }
         
         int nYearCount = 10;
         Integer yearCount = (Integer)component.getAttributes().get("yearCount");
         if (yearCount != null)
         {
            nYearCount = yearCount.intValue();
         }
         
         // now we render the output for our component
         // we create 3 drop-down menus for day, month and year
         String clientId = component.getClientId(context);
         ResponseWriter out = context.getResponseWriter();
         
         // note that we build a client id for our form elements that we are then
         // able to decode() as above.
         renderMenu(out, component, getDays(), date.getDate(), clientId + "_day");
         renderMenu(out, component, getMonths(), date.getMonth() + 1, clientId + "_month");
         renderMenu(out, component, getYears(nStartYear, nYearCount), date.getYear() + 1900, clientId + "_year");
      }
   }
   
   /**
    * Render a drop-down menu to represent an element for the date picker.
    * 
    * @param out              Response Writer to output too
    * @param component        The compatible component
    * @param items            To display in the drop-down list
    * @param selected         Which item index is selected
    * @param clientId         Client Id to use
    * 
    * @throws IOException
    */
   private void renderMenu(ResponseWriter out, UIComponent component, List items,
         int selected, String clientId)
      throws IOException
   {
      out.startElement("select", component);
      out.writeAttribute("name", clientId, "id");
      
      if (component.getAttributes().get("styleClass") != null)
      {
         out.writeAttribute("class", component.getAttributes().get("styleClass"), "styleClass");
      }
      if (component.getAttributes().get("style") != null)
      {
         out.writeAttribute("style", component.getAttributes().get("style"), "style");
      }
      
      for (Iterator i=items.iterator(); i.hasNext(); /**/)
      {
         SelectItem item = (SelectItem)i.next();
         Integer value = (Integer)item.getValue();
         out.startElement("option", component);
         out.writeAttribute("value", value, null);
         
         // show selected value
         if (value.intValue() == selected)
         {
            out.writeAttribute("selected", "selected", null);
         }
         out.writeText(item.getLabel(), null);
      }
      out.endElement("select");
   }
   
   private List getYears(int startYear, int yearCount)
   {
      List years = new ArrayList();
      for (int i=startYear; i<startYear + yearCount; i++)
      {
         Integer year = Integer.valueOf(i);
         years.add(new SelectItem(year, year.toString()));
      }
      return years;
   }
   
   private List getMonths()
   {
      // get names of the months for default locale
      DateFormatSymbols dfs = new DateFormatSymbols();
      String[] names = dfs.getMonths();
      List months = new ArrayList(12);
      for (int i=0; i<12; i++)
      {
         Integer key = Integer.valueOf(i + 1);
         months.add(new SelectItem(key, names[i]));
      }
      return months;
   }
   
   private List getDays()
   {
      List days = new ArrayList(31);
      for (int i=1; i<32; i++)
      {
         Integer day = Integer.valueOf(i);
         days.add(new SelectItem(day, day.toString()));
      }
      return days;
   }
}
