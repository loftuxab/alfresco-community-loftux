/*
 * Created on 09-May-2005
 */
package org.alfresco.web.ui.repo.component;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.SelfRenderingComponent;
import org.alfresco.web.ui.repo.component.property.UIPropertySheet;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class UISimpleSearch extends UICommand
{
   // ------------------------------------------------------------------------------
   // Component implementation
   
   /**
    * Default Constructor
    */
   public UISimpleSearch()
   {
      // specifically set the renderer type to null to indicate to the framework
      // that this component renders itself - there is no abstract renderer class
      setRendererType(null);
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.SimpleSearch";
   }

   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.lastSearch = (String)values[1];
      this.searchOption = ((Integer)values[2]).intValue();
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[3];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.lastSearch;
      values[2] = Integer.valueOf(this.searchOption);
      return (values);
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
    */
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = Utils.getActionHiddenFieldName(context, this);
      String value = (String)requestMap.get(fieldId);
      // we are clicked if the hidden field contained our client id
      if (value != null && value.equals(this.getClientId(context)))
      {
         String searchText = (String)requestMap.get(getClientId(context));
         
         // TODO: strip or escape undesirable characters - for screen and search API
         searchText = searchText.replace('"', ' ');
         if (searchText.length() != 0)
         {
            if (logger.isDebugEnabled())
               logger.debug("*****Search text submitted: " + searchText);
            int option = -1;
            String optionFieldName = getClientId(context) + NamingContainer.SEPARATOR_CHAR + OPTION_PARAM;
            String optionStr = (String)requestMap.get(optionFieldName);
            if (optionStr.length() != 0)
            {
               option = Integer.parseInt(optionStr);
            }
            if (logger.isDebugEnabled())
               logger.debug("*****Search option submitted: " + option);
            
            // queue event so system can perform a search and update the component
            SearchEvent event = new SearchEvent(this, searchText, option);
            this.queueEvent(event);
         }
      }
   }
   
   /**
    * @see javax.faces.component.UICommand#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof SearchEvent)
      {
         // update the component parameters from the search event details
         SearchEvent searchEvent = (SearchEvent)event;
         this.setLastSearch(searchEvent.SearchText);
         this.setSearchMode(searchEvent.SearchMode);
      }
      super.broadcast(event);
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // script for dynamic simple search menu drop-down options
      out.write("<script>");
      out.write("function _searchDropdown() {" +
            "if (document.getElementById('_search').style.display == 'none') {" + 
            "   document.getElementById('_search').style.display = '';" + 
            "} else {" + 
            "   document.getElementById('_search').style.display = 'none';" + 
            "} }");
      out.write("function _noenter(event) {" +
            "if (event && event.keyCode == 13) {" +
            "   _searchSubmit();return false; }" +
            "else {" +
            "   return true; } }");
      out.write("function _searchSubmit() {");
      out.write(Utils.generateFormSubmit(context, this, Utils.getActionHiddenFieldName(context, this), getClientId(context)));
      out.write("}");
      out.write("</script>");
      
      // outer table containing search drop-down icon, text box and search Go image button
      out.write("<table cellspacing=4 cellpadding=0>");
      out.write("<tr><td style='padding-top:2px'>");
      
      String searchImage = Utils.buildImageTag(context, "/images/icons/search_icon.gif", 15, 15, "Go", "_searchSubmit();");
      
      out.write(Utils.buildImageTag(context, "/images/icons/search_controls.gif", 27, 13, "Options", "javascript:_searchDropdown();"));
      
      // dynamic DIV area containing search options
      out.write("<br><div id='_search' style='position:absolute;display:none'>");
      out.write("<table border=0 bgcolor='#eeeeee' style='border-top:thin solid #FFFFFF;border-left:thin solid #FFFFFF;border-right:thin solid #444444;border-bottom:thin solid #444444;' cellspacing=4 cellpadding=0>");
      //out.write("<tr><td class='userInputForm'><nobr>What would you like to search?</nobr></td></tr>");
      
      // output each option - setting the current one to CHECKED
      String optionFieldName = getClientId(context) + NamingContainer.SEPARATOR_CHAR + OPTION_PARAM;
      String radioOption = "<tr><td class='userInputForm'><input type='radio' name='" + optionFieldName + "'";
      out.write(radioOption);
      out.write(" VALUE='0'");
      if (getSearchMode() == 0) out.write(" CHECKED");
      out.write("><nobr>All Items</nobr></td></tr>");
      out.write(radioOption);
      out.write(" VALUE='1'");
      if (getSearchMode() == 1) out.write(" CHECKED");
      out.write("><nobr>File Names and Contents</nobr></td></tr>");
      out.write(radioOption);
      out.write(" VALUE='2'");
      if (getSearchMode() == 2) out.write(" CHECKED");
      out.write("><nobr>File Names only</nobr></td></tr>");
      out.write(radioOption);
      out.write(" VALUE='3'");
      if (getSearchMode() == 3) out.write(" CHECKED");
      out.write("><nobr>Space Names only</nobr></td></tr>");
      
      // close button
      out.write("<tr><td><table width=100%><tr><td>" +
                "<input type='button' value='Close' class='dialogControls' onclick=\"document.getElementById('_search').style.display='none';\">" + 
                "</td><td align=right>");
      out.write(searchImage);
      out.write("</td></tr></table></td></tr>");
      out.write("</table></div>");
      
      // input text box
      out.write("</td><td>");
      out.write("<input name='");
      out.write(getClientId(context));
      // TODO: style and class from component properties!
      out.write("' onkeypress=\"return _noenter(event)\"");
      out.write(" type='text' maxlength='255' style='width:90px;padding-top:3px' value=\"");
      // output previous search text stored in this component!
      out.write(getLastSearch());
      out.write("\">");
      
      // search Go image button
      out.write("</td><td>");
      out.write(searchImage);
      
      // end outer table
      out.write("</td></tr></table>");
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors
   
   /**
    * Set the last search text value
    */
   public void setLastSearch(String text)
   {
      if (text != null)
      {
         this.lastSearch = text;
      }
   }
   
   /**
    * @return The last set search text value
    */
   public String getLastSearch()
   {
      return this.lastSearch;
   }
   
   /**
    * Set the current search mode (see constants)
    * 
    * @param option     Search mode option (see constants)
    */
   public void setSearchMode(int option)
   {
      // see constants below
      if (option >= 0 && option < 4)
      {
         this.searchOption = option;
      }
   }
   
   /** 
    * @return The current search mode (see constants) 
    */
   public int getSearchMode()
   {
      return this.searchOption;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger logger = Logger.getLogger(UISimpleSearch.class);
   
   private static final String OPTION_PARAM = "_option";
   
   /** last search string */
   private String lastSearch = "";
   
   /** last used search option mode */
   private int searchOption = SEARCH_ALL;
   
   /** Search mode constants */
   public final static int SEARCH_ALL = 0;
   public final static int SEARCH_FILE_NAMES_CONTENTS = 1;
   public final static int SEARCH_FILE_NAMES = 2;
   public final static int SEARCH_SPACE_NAMES = 3;
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing a search execution from the UISimpleSearch component.
    */
   public static class SearchEvent extends ActionEvent
   {
      private static final long serialVersionUID = 3918135612344774322L;

      public SearchEvent(UIComponent component, String text, int mode)
      {
         super(component);
         SearchText = text;
         SearchMode = mode;
      }
      
      public String SearchText;
      public int SearchMode;
   }
}
