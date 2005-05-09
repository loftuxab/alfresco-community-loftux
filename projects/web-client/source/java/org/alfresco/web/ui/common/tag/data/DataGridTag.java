package org.alfresco.web.ui.common.tag.data;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.BaseComponentTag;

/**
 * DataGridTag
 * 
 * Datagrid Tag Handler. The data handler manages the dissemination of data from 
 * the datasrc in rows per page.
 */
public class DataGridTag extends BaseComponentTag
{
   // ------------------------------------------------------------------------------
   // Component methods 
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "org.alfresco.faces.DataGrid";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // The datagrid component is self rendering
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      //setStringProperty(component, "datasource", this.datasourceName);
      setStringProperty(component, "cellpadding", this.cellpadding);
      setStringProperty(component, "cellspacing", this.cellspacing);
      setStringProperty(component, "styleClass", this.styleClass);
      setStringProperty(component, "style", this.style);
      setStringProperty(component, "initialSortedColumn", this.initialSortedColumn);
      setBooleanProperty(component, "initialSortedDirection", this.initialSortedDirection);
      setIntProperty(component, "pageSize", this.pageSize);
   }
   
   
   // ------------------------------------------------------------------------------
   // Tag properties
   
   /**
    * Name of the data source object to lookup
    * 
    * @param datasource
    */
   //public void setDatasource(String datasource)
   //{
   //   this.datasourceName = datasource;
   //}
   
   /**
    * Get the style
    *
    * @return the style
    */
   public String getStyle()
   {
      return this.style;
   }

   /**
    * Set the style
    *
    * @param style     the style
    */
   public void setStyle(String style)
   {
      this.style = style;
   }

   /**
    * Get the styleClass
    *
    * @return the styleClass
    */
   public String getStyleClass()
   {
      return this.styleClass;
   }

   /**
    * Set the styleClass
    *
    * @param styleClass     the styleClass
    */
   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
   }

   /**
    * Get the cellspacing
    *
    * @return the cellspacing
    */
   public String getCellspacing()
   {
      return this.cellspacing;
   }

   /**
    * Set the cellspacing
    *
    * @param cellspacing     the cellspacing
    */
   public void setCellspacing(String cellspacing)
   {
      this.cellspacing = cellspacing;
   }

   /**
    * Get the cellpadding
    *
    * @return the cellpadding
    */
   public String getCellpadding()
   {
      return this.cellpadding;
   }

   /**
    * Set the cellpadding
    *
    * @param cellpadding     the cellpadding
    */
   public void setCellpadding(String cellpadding)
   {
      this.cellpadding = cellpadding;
   }

   /**
    * Get the initialSortedColumn
    *
    * @return the initialSortedColumn
    */
   public String getInitialSortedColumn()
   {
      return this.initialSortedColumn;
   }

   /**
    * Set the initialSortedColumn
    *
    * @param initialSortedColumn     the initialSortedColumn
    */
   public void setInitialSortedColumn(String initialSortedColumn)
   {
      this.initialSortedColumn = initialSortedColumn;
   }

   /**
    * Get the initialSortedDirection
    *
    * @return the initialSortedDirection
    */
   public String getInitialSortedDirection()
   {
      return this.initialSortedDirection;
   }

   /**
    * Set the initialSortedDirection
    *
    * @param initialSortedDirection     the initialSortedDirection
    */
   public void setInitialSortedDirection(String initialSortedDirection)
   {
      this.initialSortedDirection = initialSortedDirection;
   }

   /**
    * Get the pageSize
    *
    * @return the pageSize
    */
   public String getPageSize()
   {
      return this.pageSize;
   }

   /**
    * Set the pageSize
    *
    * @param pageSize     the pageSize
    */
   public void setPageSize(String pageSize)
   {
      this.pageSize = pageSize;
   }
   
   
   //------------------------------------------------------------------------------
   // Tag implemenation
   
   /**
    * release tag attributes
    */
   public void release()
   {
      this.pageSize = null;
      this.cellpadding = null;
      this.cellspacing = null;
      this.initialSortedColumn = null;
      this.initialSortedDirection = "true";
   }
   
   /**
    * Process the start tag.
    *
    * @return               Continuation instruction.
    */
   /*public int doStartTag() throws JspTagException
   {
      // register the data grid hidden field with the parent form
      FormTag parent = (FormTag)findAncestorWithClass(this, FormTag.class);
      if (parent == null)
      {
         throw new IllegalStateException("DataGridTag must be contained within parent Form tag!");
      }
      parent.addHiddenField(DataConstants.DATAHANDLER_NAME_PARAM, null);
      
      // bind the datahandler to the tag attributes
      bind();
      
      try
      {
         JspWriter out = pageContext.getOut();
         
         // start the table grid
         out.print("<table border=0");
         if (this.styleClass != null && this.styleClass.length() != 0)
         {
            out.print(" class='");
            out.print(this.styleClass);
            out.print('\'');
         }
         if (this.cellpadding != null && this.cellpadding.length() != 0)
         {
            out.print(" cellpadding='");
            out.print(this.cellpadding);
            out.print('\'');
         }
         if (this.cellspacing != null && this.cellspacing.length() != 0)
         {
            out.print(" cellspacing='");
            out.print(this.cellspacing);
            out.print('\'');
         }
         out.print('>');
      }
      catch (Exception e)
      {
         throw new JspTagException(e.getMessage());
      }
      
      return EVAL_BODY_INCLUDE;
   }*/
   
   /**
    * Process the end tag.
    *
    * @return               Always return EVAL_PAGE.
    */
   /*public int doEndTag() throws JspTagException
   {
      try
      {
         JspWriter out = pageContext.getOut();
         
         out.print("</table>");
      }
      catch (Exception e)
      {
         throw new JspTagException(e.getMessage());
      }
      
      return EVAL_PAGE;
   }*/
   
   
   //------------------------------------------------------------------------------
   // Private methods
   
   /**
    * Return the parent FormTag instance
    * 
    * @return the parent FormTag
    */
   /*private FormTag getParentForm()
   {
      return (FormTag)findAncestorWithClass(this, FormTag.class);
   }*/
   
   /**
    * Bind tag attributes to the data handler
    */
   /*private void bind()
   {
      // set the mandatory attributes so the data handler can initialise
      IDataHandler handler = getDataHandler();
      handler.setUniqueName(this.datasourceName);
      handler.setPageSize(this.nPageSize);
      
      // apply initially sorted column if required
      if (handler.getLastSortedColumn() == -1 && this.initialSortedColumn != null)
      {
         int column = handler.getDataSource().getMetaData().lookupFieldIndex(this.initialSortedColumn);
         if (column != -1)
         {
            // TODO: add initially sorted mode tag property
            handler.sort(column, this.initialSortedDirection, IDataHandler.SORT_CASEINS);
         }
      }
   }*/
   
   
   //------------------------------------------------------------------------------
   // Private data
   
   /** the style */
   private String style;

   /** the CSS Class */
   private String styleClass;

   /** the cellspacing */
   private String cellspacing;

   /** the cellpadding */
   private String cellpadding;

   /** the initialSortedColumn */
   private String initialSortedColumn;

   /** the initialSortedDirection */
   private String initialSortedDirection;

   /** the page size */
   private String pageSize;

} // end class DataGridTag
