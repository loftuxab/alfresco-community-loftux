package com.activiti.web.data;

import java.util.List;

/**
 * IDataHandler
 * 
 * Data Handler interface. Implemented by an intermediate object with the
 * responsibility of managing the state of the datasource including paging of
 * data and sorting.
 */
public interface IDataHandler
{
   /**
    * Set the data source to use
    * 
    * @param datasrc             the IDataSource data source to use
    */
   public void setDataSource(IDataSource datasrc);
   
   /**
    * Get the current datasource
    * 
    * @return IDataSource
    */
   public IDataSource getDataSource();
   
   /**
    * Get the form relative unique name of this data handler
    * 
    * @return the name
    */
   public String getUniqueName();
   
   /**
    * Set the form relative unique name for this data handler
    * 
    * @param name                the unique name
    */
   public void setUniqueName(String name);
   
   /**
    * Return the total number of rows available
    * 
    * @return total number of rows
    */
   public int getRowCount();
   
   /**
    * Return the current row index into the datasource
    * 
    * @return current row index into the datasource
    */
   public int getCurrentRow();
   
   /**
    * Set the page size to be used
    * 
    * @param size                the page size
    */
   public void setPageSize(int size);
   
   /**
    * Get the page size currently being used - default is 0 which means that
    * the data is not paged. Set this to any value >=1 to activate paging.
    * 
    * @return the current page size
    */
   public int getPageSize();
   
   /**
    * Set the page index to be displayed on the next refresh
    * 
    * @param page                the current page index
    */
   public void setCurrentPage(int page);
   
   /**
    * Return the index of the current page being displayed
    * 
    * @return the current page index
    */
   public int getCurrentPage();
   
   /**
    * Return the count of the pages available for display
    * 
    * @return the page count
    */
   public int getPageCount();
   
   /**
    * Returns whether the data is returned in pages or not
    * 
    * @return true if paging is active, false otherwise
    */
   public boolean isPaged();
   
   /**
    * Returns true if another page of data is available for display
    * 
    * @return true if another page is available, false othewise
    */
   public boolean hasNextPage();
   
   /**
    * Returns true if a previous page of data is available for display
    * 
    * @return true if previous page is available, false othewise
    */
   public boolean hasPreviousPage();
   
   /**
    * Increments the page to be displayed on the next refresh
    */
   public void nextPage();
   
   /**
    * Decrements the page to be displayed on the next refresh
    */
   public void previousPage();
   
   /**
    * Sets the first page to be displayed on the next refresh
    */
   public void firstPage();
   
   /**
    * Sets the last page to be displayed on the next refresh
    */
   public void lastPage();
   
   /**
    * Increments the current row counter within the current displayed page.
    * This method must be called once to check for the existance of any data
    * rows on the current page before display.
    * 
    * @return true if another row of data is available, false otherwise
    */
   public boolean nextRow();
   
   /**
    * Resolves the value of the specified field name for the current row in
    * the current page of data.
    * 
    * @param field                  the column name of the field to get
    * 
    * @return the value of the specified field
    */
   public String resolveField(String field);
   
   /**
    * Resolves the value of the specified field name for the current row in
    * the current page of data.
    * 
    * @param field                  the column name of the field to resolve
    * 
    * @return the Object value of the specified field
    */
   public Object resolveObjectField(String field);
   
   /**
    * Sorts the dataset.
    * 
    * @param column                 the column index to sort by
    * @param forward                true for a forward sort, false otherwise
    * @param mode                   IDataHandler sorting mode constant
    */
   public void sort(int column, boolean forward, int mode);
   
   /**
    * The last column index to be sorted if any
    * 
    * @return last column index to be sorted or -1 if none set
    */
   public int getLastSortedColumn();
   
   /**
    * The last column sort direction
    * 
    * @return true for Forward, false for Reverse
    */
   public boolean getLastSortedDirection();
   
   /**
    * Reset current row index within the current page
    */
   public void resetRowIndex();
   
   /**
    * Invalidate the data handler so it forces an init() call next time 
    * data is requested from it.
    */
   public void invalidate();
   
   
   // ------------------------------------------------------------------------------
   // Constants
   
   /** case sensitive sort */
   public final static int SORT_CASE      = 0;
   
   /** case in-sensitive sort */
   public final static int SORT_CASEINS   = 1;
   
   /** numeric sort */
   public final static int SORT_NUMERIC   = 2;
   
} // end interface IDataHandler
