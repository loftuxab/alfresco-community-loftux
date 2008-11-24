/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
 /*
 * Prerequisites: 
 *               YAHOO:
 * 							button.js 
 */
 
 /**
  * Extends The ButtonGroup class.
  */
if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

/**
 * The ButtonGroup class.
 * @param p_oElement {Object}.
 * @param p_oAttributes {Object}.
 * @namespace WebStudio
 * @class ButtonGroup
 * @constructor
 * @extends YAHOO.widget.ButtonGroup
 */
WebStudio.ButtonGroup = function(p_oElement, p_oAttributes)
{
	 WebStudio.ButtonGroup.superclass.constructor.call(this, this._createGroupElement(), p_oElement);
};

YAHOO.extend(WebStudio.ButtonGroup, YAHOO.widget.ButtonGroup,
{ 
	/**
	 * Gets the name of group.
	 */
	getGroupName: function()
	{
		return this._configs.name.value;
	},
	
	/**
	 * Gets the value of checked button.
	 */
	getCheckedValue: function()
	{
		return this._configs.value.value;
	}
});  

/**
 * The Combobox class.
 * @param p_oElement {Object}.
 * @param p_oAttributes {Object}.
 * @namespace WebStudio
 * @class Combobox
 * @constructor
 * @extends YAHOO.widget.Button
 */
WebStudio.Combobox = function(p_oElement, p_oAttributes)
{
	 WebStudio.Combobox.superclass.constructor.call(this, this.createButtonElement(p_oElement.type), p_oElement);
};

YAHOO.extend(WebStudio.Combobox, YAHOO.widget.Button,
{ 
	/**
	 * Gets the name.
	 */
	getName: function()
	{
		return this._configs.name.value;
	},
	
	/**
	 * Gets the value of selected item.
	 */
	getSelectedItemValue: function()
	{
		return this._configs.selectedMenuItem.value.value;
	}
}); 

/**
 * The SelectSingleGrid class.
 * @param elContainer {string}.
 * @param aColumnFormats {Object}.
 * @param aColumns {Object}.
 * @param oDataSource {Object}.
 * @param oConfigs {Object}.
 * @namespace WebStudio
 * @class SelectSingleGrid
 * @constructor
 * @extends YAHOO.widget.DataTable
 */
WebStudio.SelectSingleGrid = function(elContainer, aColumnFormats, aColumns, oDataSource, oConfigs)
{
 //TODO: adjust parameters
	var columnDefs = [];
	columnDefs.push({
		key: "",
		formatter:YAHOO.widget.DataTable.formatCheckbox,
		width: 30
	});
	var fields = [];
	for (var i = 0, len = aColumnFormats.length; i < len; i++)
	{
		var colf = aColumnFormats[i];
		var col = aColumns[i];
		columnDefs.push({
			key: col.id,
			label: col.text,
			sortable: colf.sortable,
			resizeable: true,
			width: colf.width
			//TODO: need adjust server side object property for YAHOOUI widgets
		/* resizeable, formatter, sortOptions */
		});
		fields.push(col.id);
	}
	var dataSource =  new YAHOO.util.DataSource(oDataSource);
	dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	dataSource.responseSchema = {fields: fields};
	  
	oConfigs.selectionMode = "single"; 
	WebStudio.SelectSingleGrid.superclass.constructor.call(this, elContainer, columnDefs, dataSource, oConfigs);
};

YAHOO.extend(WebStudio.SelectSingleGrid, YAHOO.widget.DataTable,
{ 
	/** 		
	 * @Override
 	 */
	_unselectAllTrEls : function()
	{
	    var selectedRows = YAHOO.util.Dom.getElementsByClassName(YAHOO.widget.DataTable.CLASS_SELECTED,"tr",this._elTbody);
	    YAHOO.util.Dom.removeClass(selectedRows, YAHOO.widget.DataTable.CLASS_SELECTED);
	    for (var i = 0, len = selectedRows.length; i < len; i++)
	    {
	    	this.changeCheckboxValue(selectedRows[i],false);
	    }
	},
	
	/** 		
	 * @Override
 	 */
	formatCheckbox : function(el, oRecord, oColumn, oData)
	{
        var bChecked = oData;
        bChecked = (bChecked ? " checked" : "");
        el.innerHTML = "<input type=\"checkbox\"" + bChecked + " class=\"" + YAHOO.widget.DataTable.CLASS_CHECKBOX + "\">";
        el.onclick = this.onEventSelectRow;        
    },
	
	/** 		
	 * @Override
 	 */
	selectRow : function(row)
	{
	    var oRecord, elRow;
	
	    if(row instanceof YAHOO.widget.Record) 
	    {
	        oRecord = this._oRecordSet.getRecord(row);
	        elRow = this.getTrEl(oRecord);
	    }
	    else if(YAHOO.lang.isNumber(row)) 
	    {
	        oRecord = this.getRecord(row);
	        elRow = this.getTrEl(oRecord);
	    }
	    else 
	    {
	        elRow = this.getTrEl(row);
	        oRecord = this.getRecord(elRow);
	    }
	
	    if(oRecord) 
	    {
	        // Update selection trackers
	        var tracker = this._aSelections || [];
	        var sRecordId = oRecord.getId();
	        var index = -1;
	
	        // Remove if already there:
	        // Use Array.indexOf if available...
	        /*if(tracker.indexOf && (tracker.indexOf(sRecordId) >  -1)) {
	            tracker.splice(tracker.indexOf(sRecordId),1);
	        }*/
	        if(tracker.indexOf) 
	        {
	            index = tracker.indexOf(sRecordId);
	        }
	        else 
	        {
	        	// ...or do it the old-fashioned way
	            for(var j=tracker.length-1; j>-1; j--) {
	                if(tracker[j] === sRecordId){
	                    index = j;
	                    break;
	                }
	            }
	        }
	        if(index > -1) {
	            tracker.splice(index,1);
	        }
	        
	        // Add to the end
	        tracker.push(sRecordId);
	        this._aSelections = tracker;
	
	        // Update trackers
	        if(!this._oAnchorRecord) 
	        {
	            this._oAnchorRecord = oRecord;
	        }
	
	        // Update UI
	        if(elRow) 
	        {
	            YAHOO.util.Dom.addClass(elRow, YAHOO.widget.DataTable.CLASS_SELECTED);
	            this.changeCheckboxValue(elRow,true);
	        }
	
	        this.fireEvent("rowSelectEvent", {record:oRecord, el:elRow});
	    }
	},
	
	/**
	 * Changes checkbox's value.
	 */
	changeCheckboxValue: function(row, value)
	{	
		if (row)
		{
			var checkbox = row.getElementsBySelector("input")[0];
	      if (checkbox)
	      {
	      	checkbox.checked = value;
	      }
		}	
	},
	
	/** 		
	 * @Override
 	 */
	unselectRow : function(row)
	{
	    var elRow = this.getTrEl(row);
	
	    var oRecord;
	    if(row instanceof YAHOO.widget.Record) {
	        oRecord = this._oRecordSet.getRecord(row);
	    }
	    else if(YAHOO.lang.isNumber(row)) {
	        oRecord = this.getRecord(row);
	    }
	    else {
	        oRecord = this.getRecord(elRow);
	    }
	
	    if(oRecord) {
	        // Update selection trackers
	        var tracker = this._aSelections || [];
	        var sRecordId = oRecord.getId();
	        var index = -1;
	
	        // Remove if found
	        var bFound = false;
	
	        // Use Array.indexOf if available...
	        if(tracker.indexOf) {
	            index = tracker.indexOf(sRecordId);
	        }
	        // ...or do it the old-fashioned way
	        else {
	            for(var j=tracker.length-1; j>-1; j--) {
	                if(tracker[j] === sRecordId){
	                    index = j;
	                    break;
	                }
	            }
	        }
	        if(index > -1) {
	            tracker.splice(index,1);
	        }
	
	        if(bFound) {
	            // Update tracker
	            this._aSelections = tracker;
	
	            // Update the UI
	            YAHOO.util.Dom.removeClass(elRow, YAHOO.widget.DataTable.CLASS_SELECTED);
	
	            this.fireEvent("rowUnselectEvent", {record:oRecord, el:elRow});
	
	            return;
	        }
	
	        // Update the UI
	        YAHOO.util.Dom.removeClass(elRow, YAHOO.widget.DataTable.CLASS_SELECTED);
	        this.changeCheckboxValue(elRow,false);
	
	        this.fireEvent("rowUnselectEvent", {record:oRecord, el:elRow});
	    }
	},
	
	/**
	 * Gets the selected row data.
	 * @return {object} like columns definition.  
	 */
	 getSelectedRowData: function()
	 {
	 	return {};
	 },
	 
	 /**
	  * Gets the selected page id.
	  * @return {string} the page id.
	  */
	 getSelectedPageId: function()
	 {
	 	var pageId = null;
	 	if (this._oAnchorRecord)
	 	{
	 		pageId = this._oAnchorRecord.getData().pageId || null;
	 	}
	 	return pageId;
	 },
	 
	 /**
	  * Gets the selected format id.
	  * @return {string} the format id.
	  */
	 getSelectedFormatId: function()
	 {
	 	var formatId = null;
	 	if (this._oAnchorRecord)
	 	{
	 		formatId = this._oAnchorRecord.getData().formatId || null;
	 	}
	 	return formatId;
	 },
	 
	 /**
	  * Gets the selected template id.
	  * @return {string} the template id.
	  */
	 getSelectedTemplateId: function()
	 {
	 	var templateId = null;
	 	if (this._oAnchorRecord)
	 	{
	 		templateId = this._oAnchorRecord.getData().templateId || null;
	 	}
	 	return templateId;
	 },
	 
	 /**
	  * Gets the selected association id.
	  * @return {string} the association id.
	  */
	 getSelectedAssociationId: function()
	 {
	 	var associationId = null;
	 	if (this._oAnchorRecord)
	 	{
	 		associationId = this._oAnchorRecord.getData().associationId || null;
	 	}
	 	return associationId;
	 }
});