/***********************************************************************
 *
 * Wizard Core
 * For Alfresco Dynamic Website
 *
 ***********************************************************************/

function DialogManager()
{
	this.map = new Object();
	this.getDialog = getDialog;
	this.addDialog = addDialog;
	this.clearDialogs = clearDialogs;
	this.removeDialog = removeDialog;
	
	function getDialog(name) { return this.map[name]; } 
	function addDialog(name, dialog) { this.map[name] = dialog; }
	function clearDialogs() { this.map = new Object(); }
	function removeDialog(name) { this.map[name] = null; }
}

var dialogManager = new DialogManager();









/***********************************************************************
 *
 * Methods for working with the internal JSON data object
 *
 ***********************************************************************/

function getColumnFormatValue(data, id, propertyName)
{
	var array = data.columnformats;
	if(array != null)
	{
		for(var i = 0; i < array.length; i++)
		{
			var _id = array[i].id;
			if(_id == id)
				return array[i][propertyName];
		}
	}
	return null;
}

function getElementFormatValue(data, name, propertyName)
{
	var array = data.elementformats;
	if(array != null)
	{
		for(var i = 0; i < array.length; i++)
		{
			var _name = array[i].name;
			if(_name == name)
				return array[i][propertyName];
		}
	}
	return null;
}

function getElementSelectionValues(data, elementId)
{
	var array = data.elementvalues;
	if(array != null)
	{
		var values = array[elementId];
		return values;
	}
	return null;
}

function getPage(data, id)
{
	var pageObject = null;
	var array = data.pages;
	if(array != null)
	{
		for(var i = 0; i < array.length; i++)
		{
			var _id = array[i].id;
			if(_id == id)
				pageObject = array[i];
		}
	}
	return pageObject;
}

function getDefaultJSONRequest(windowId, defaultJson)
{
	var json = {};
	json["windowId"] = windowId;
	json["schema"] = "adw10";
	
	if(defaultJson != null)
	{
		for(key in defaultJson)
		{
			var val = defaultJson[key];
			json[key] = val;
		}
	}
	
	return json;
}

/***********************************************************************
 *
 * Methods for working with the internal JSON data object
 *
 ***********************************************************************/


function dialogButtonHandler(item, e) 
{
	var windowId = item.initialConfig.windowId;
	var dialog = dialogManager.getDialog(windowId);
	
	var action = item.action;
	var actionData = item.actionData;
	var currentPageId = item.currentPageId;
	dialog.processAction(action, actionData, currentPageId);
}







/****************************************
 **  DEFAULT DIALOG (FOR BASIC FORMS)
 ****************************************/

function DefaultDialog(id, initialUri)
{
	this.id = id;
	this.window = null;
	this.data = null;
	this.init = init;
	this.execute = execute;
	this.processAction = processAction;
	this.initialUri = initialUri;
	this.defaultJson = null;
	this.registeredComponents = null;
	this.onFinish = onFinish;
	this.onFinishFunction = null;
	this.callSuccess = callSuccess;
	this.callFailure = callFailure;
	this.makeWindowConfiguration = makeWindowConfiguration;
	this.makeWindow = makeWindow;
	
	
	this.setDefaultJson = setDefaultJson;
	function setDefaultJson(someJson) { this.defaultJson = someJson; }	
	
	function init()
	{
		var json = getDefaultJSONRequest(this.id, this.defaultJson);
		
		// tell the remote session to refresh
		json["refreshSession"] = true;
		
		var remoteURL = getAdsWebScriptURL(this.initialUri);
		Ext.Ajax.request({
				url: remoteURL,
				success: callSuccess,
				failure: callFailure,
				disableCaching: true,
				method: 'GET',				
				params: {
					'json' : json.toJSONString()
				}
		});
	}
	
	function execute(data)
	{
		this.data = data;
		current = data.current;
		if(current.code == "finish")
		{
			if(this.window != null)
			{
				// if we have a finalizer function, call it
				try {
					if(this.onFinishFunction != null)
						this.onFinishFunction();
				}catch(err) { }
			
				this.window.hide();
				this.window.close();
			}
		}
		else
		{
			var oldWindow = null;
			if(this.window != null)
			{
				oldWindow = this.window;
			}
			this.window = makeWindow(data);
			if(oldWindow != null)
			{
				this.window.setPosition(oldWindow.getPosition());
				this.window.setSize(oldWindow.getSize());
				oldWindow.close();
			}
			this.window.show();
		}	


		if(current.cacheInvalidateAll)
		{
			refreshCache();		
		}
		
		if(current.reload)
		{
			doWindowReload();
		}
	}
	
	
	function processAction(action, actionData, currentPageId)
	{
		action = action.toLowerCase();
		if("close" == action || "cancel" == action)
		{
			if(this.window != null)
			{
				this.window.hide();
				this.window.close()
			}
			return;
		}
		
		// TRANSITION TO A NEW WIZARD STATE
		if("transition" == action)
		{		
			// otherwise, it is a "submit" of some kind (to another state)			
			var newPage = getPage(this.data, actionData);

			// is this a "save" state?
			if(newPage.finish)
			{
				Ext.MessageBox.wait("Saving Changes...");			
			}		

			// fire the ajax request
			var json = getDefaultJSONRequest(this.id, this.defaultJson);

			// dummy this up for the moment
			if(this.window.items != null)
			{
				var fp = this.window.items.item(0);
				if(fp != null && fp.items != null)
				{
					if(fp.items.length > 0)
					{
						json.elements = new Array();
						var elementIndex = 0;
						for(var i = 0; i < fp.items.length; i++)
						{
							var elementName = fp.items.item(i).name;
							var elementType = fp.items.item(i).xtype;
							var elementValue = fp.items.item(i).getValue();
							

							if("radio" == elementType)
							{
								// is this currently selected?
								if(elementValue)
								{
									var text = fp.items.item(i).boxLabel;
									var val = fp.items.item(i).inputValue;

									json.elements[elementIndex] = {};
									json.elements[elementIndex].name = elementName;
									json.elements[elementIndex].value = val;
									elementIndex = elementIndex + 1;
								}
							}
							else
							{
								json.elements[elementIndex] = {};
								json.elements[elementIndex].name = elementName;		
								json.elements[elementIndex].value = elementValue;
								elementIndex = elementIndex + 1;
							}
						}
					}
				}
			}

			// load up some wizard page transition values			
			json["currentPageId"] = currentPageId;
			json["requestedPageId"] = newPage.id;

			var remoteURL = getAdsWebScriptURL(newPage.uri);
			Ext.Ajax.request({
					url: remoteURL,
					success: callSuccess,
					failure: callFailure,
					disableCaching: true,
					method: 'GET',
					params: {
						'json' : json.toJSONString()
					}
			});
		}
	}	
	
	function onFinish(f)
	{
		this.onFinishFunction = f;
	}


	function callSuccess(responseObject)
	{
		var data = responseObject.responseText.parseJSON();
		var dialog = dialogManager.getDialog(data.windowId);
		var current = data.current;

		// show messages (TODO: allow to be disabled)
		if(current.message != null)
		{
			Ext.Msg.show({
			   title:'Message',
			   msg: current.message,
			   buttons: Ext.Msg.OK,
			   fn: function(btn, text) {
				if(btn == 'ok')
				{
					// tell the dialog to execute
					dialog.execute(data);
				}
			   }
			});

		}
		else
		{
			// tell the dialog to execute
			dialog.execute(data);
		}
	}

	function callFailure(responseObject)
	{
		alert("web script failed: " + responseObject);
		var data = responseObject.responseText.parseJSON();
	}
	
	
	function makeWindowConfiguration(current, data)
	{
		var i = 0;
		var _windowId = data.windowId;
		var _title = current.title;

		// build the config
		var config = {};
		config["windowId"] = _windowId;
		config["title"] = _title;
		config["height"] = 300;
		config["minHeight"] = 200;
		config["closable"] = false;
		config["plain"] = true;
		config["modal"] = true;
		config["bodyStyle"] = 'padding: 5px';
		config["layout"] = 'fit';
		config["buttonAlign"] = 'center';
		config["width"] = 500;
		config["minWidth"] = 300;
		config["resizable"] = false;

		// buttons variable
		var _buttons = null;
		if(data.buttons != null)
		{
			for( i = 0; i < data.buttons.length; i++ )
			{
				var buttonId = data.buttons[i].id;
				var buttonText = data.buttons[i].text;
				var buttonAction = data.buttons[i].action;
				var buttonData = data.buttons[i].data;
				var buttonEnabled = data.buttons[i].enabled;

				if(_buttons == null)
					_buttons = new Array();

				_buttons[i] = {};
				_buttons[i]["windowId"] = _windowId;
				_buttons[i]["handler"] = dialogButtonHandler;
				_buttons[i]["id"] = buttonId;
				_buttons[i]["text"] = buttonText;
				_buttons[i]["action"] = buttonAction;
				_buttons[i]["actionData"] = buttonData;
				_buttons[i]["currentPageId"] = current.id;

				if(buttonEnabled == false)
					_buttons[i]["disabled"] = true;
			}		
			config.buttons = _buttons;
		}

		/**
			Various types of windows that we can render
		**/

		if(current.dialogtype == "form")
		{
			var panelConfig = { };

			config["items"] = new Ext.form.FormPanel(panelConfig);
			var _items = null;
						
			if(data.elements != null)
			{			
				var formElementIndex = 0;
				for( i = 0; i < data.elements.length; i++)
				{
					if(_items == null)
						_items = new Array();
					_items[formElementIndex] = {};


					// retrieve common properties

					var elementName = data.elements[i].name;
					var elementValue = data.elements[i].value;
					elementValue = unescape(elementValue);
					var elementType = getElementFormatValue(data, elementName, "type");
					var elementLabel = getElementFormatValue(data, elementName, "label");
					var elementWidth = getElementFormatValue(data, elementName, "width");
					if(elementWidth == null)
						elementWidth = "290";
					var elementHeight = getElementFormatValue(data, elementName, "height");
					var elementHidden = getElementFormatValue(data, elementName, "hidden");

					// common handling

					if(elementName != null)
						_items[formElementIndex]["name"] = elementName;
					if(elementValue != null)
						_items[formElementIndex]["value"] = elementValue;
					if(elementType != null)
						_items[formElementIndex]["xtype"] = elementType;
					if(elementLabel != null)
						_items[formElementIndex]["fieldLabel"] = elementLabel;
					else
						_items[formElementIndex]["hideLabel"] = true;
					if(elementWidth != null)
						_items[formElementIndex]["width"] = elementWidth;
					if(elementHeight != null)
						_items[formElementIndex]["height"] = elementHeight;
					if(elementHidden == true)
					{
						_items[formElementIndex]["xtype"] = "hidden";
					}
					_items[formElementIndex]["disabled"] = false;
					var inputType = getElementFormatValue(data, elementName, "inputType");
					if(inputType != null)
						_items[formElementIndex]["inputType"] = inputType;



					// special settings for combo boxes

					if(elementType == "combo")
					{
						var comboData = getElementSelectionValues(data, elementName);
						var store = new Ext.data.SimpleStore({
							fields: ['id', 'value'],
							data: comboData
						});
						_items[formElementIndex]["store"] = store;
						_items[formElementIndex]["displayField"] = "value";
						_items[formElementIndex]["valueField"] = "id";
						_items[formElementIndex]["selectOnFocus"] = true;
						_items[formElementIndex]["typeAhead"] = true;
						_items[formElementIndex]["mode"] = "local";
						_items[formElementIndex]["triggeraction"] = "all";

						var emptyText = getElementFormatValue(data, elementName, "emptyText");
						if(emptyText != null)
							_items[formElementIndex]["emptyText"] = emptyText;
						var title = getElementFormatValue(data, elementName, "title");
						if(title != null)
							_items[formElementIndex]["title"] = title;
						var allowBlank = getElementFormatValue(data, elementName, "allowBlank");
						if(allowBlank != null)
							_items[formElementIndex]["allowBlank"] = allowBlank;
						var editable = getElementFormatValue(data, elementName, "editable");
						if(editable == null)
							editable = false;
						_items[formElementIndex]["editable"] = editable;
						_items[formElementIndex]["value"] = elementValue;


						_items[formElementIndex]["allowBlank"] = true;
						_items[formElementIndex]["typeAhead"] = false;
						_items[formElementIndex]["editable"] = true;
					}


					// special settings for "radio" buttons

					if(elementType == "radio")
					{
						var radioData = getElementSelectionValues(data, elementName);
						for(var xx = 0; xx < radioData.length; xx++)
						{
							var radioRowValue = radioData[xx][0];
							var radioRowText = radioData[xx][1];

							//_items[formElementIndex] = null;
							_items[formElementIndex] = { };

							_items[formElementIndex]["name"] = elementName;
							_items[formElementIndex]["value"] = radioRowValue;
							_items[formElementIndex]["xtype"] = "radio";
							_items[formElementIndex]["boxLabel"] = radioRowText;
							_items[formElementIndex]["hideLabel"] = true;
							_items[formElementIndex]["inputValue"] = radioRowValue;

							if(elementValue == radioRowValue)
								_items[formElementIndex]["checked"] = true;

							if(xx + 1 < radioData.length)
								formElementIndex = formElementIndex + 1;
						}					
					}

					formElementIndex = formElementIndex + 1;
				}
				
				panelConfig["frame"] = true;			
				panelConfig["items"] = _items;
				config["items"] = new Ext.form.FormPanel(panelConfig);
			}
			else
			{
				config["items"] = null;
			}
		}

		if(current.dialogtype == "url")
		{
			current.url = toBrowser(current.url);
			config["items"] = new Ext.ux.ManagedIframePanel({defaultSrc: current.url, iframeStyle: "iframeStyle1" });
		}

		if(current.dialogtype == "html")
		{
			config["html"] = current.html;
		}


		if(current.dialogtype == "checkboxgrid")
		{
			var sm = new Ext.grid.CheckboxSelectionModel();
			var gridPanelId = "gridPanel_" + _windowId;

			// set up the grid toolbar
			var gridToolbar = null;
			if(data.grid.toolbar != null)
			{
				gridToolbar = new Array();
				for( i = 0; i < data.grid.toolbar.length; i++)
				{				
					if("-" == data.grid.toolbar[i])
					{
						gridToolbar[i] = "-";
					}
					else
					{
						gridToolbar[i] = { };
						gridToolbar[i]["id"] = data.grid.toolbar[i].id;
						if(data.grid.toolbar[i].text != null)
							gridToolbar[i]["text"] = data.grid.toolbar[i].text;
						if(data.grid.toolbar[i].tooltip != null)
							gridToolbar[i]["tooltip"] = data.grid.toolbar[i].tooltip;
						if(data.grid.toolbar[i].iconCls != null)
						{
							gridToolbar[i]["cls"] = "x-btn-text-icon";
							if("add" == data.grid.toolbar[i].iconCls)
							{
								gridToolbar[i]["iconCls"] = 'dialog-grid-icon-addbutton';
							}
							if("remove" == data.grid.toolbar[i].iconCls)
							{
								gridToolbar[i]["iconCls"] = 'dialog-grid-icon-deletebutton';
							}
							if("delete" == data.grid.toolbar[i].iconCls)
							{
								gridToolbar[i]["iconCls"] = 'dialog-grid-icon-deletebutton';
							}
							if("edit" == data.grid.toolbar[i].iconCls)
							{
								gridToolbar[i]["iconCls"] = 'dialog-grid-icon-editbutton';
							}
							if("option" == data.grid.toolbar[i].iconCls)
							{
								gridToolbar[i]["iconCls"] = 'dialog-grid-icon-optionsbutton';
							}
							if("options" == data.grid.toolbar[i].iconCls)
							{
								gridToolbar[i]["iconCls"] = 'dialog-grid-icon-optionsbutton';
							}
						}

						gridToolbar[i]["handler"] = buttonHandler;
						gridToolbar[i]["gridPanelId"] = gridPanelId;
					}
				}
			}
			var gridColumns = [ sm ];		
			var gridReaderItems = null;
			if(data.grid.columns != null)
			{
				for( i = 0; i < data.grid.columns.length; i++)
				{
					var columnId = data.grid.columns[i].id;
					var columnText = data.grid.columns[i].text;
					var columnDataIndex = data.grid.columns[i].dataIndex;				
					var columnWidth = getColumnFormatValue(data, columnId, "width");
					var columnSortable = getColumnFormatValue(data, columnId, "sortable");

					var x = gridColumns.length;
					gridColumns[x] = { };
					gridColumns[x]["id"] = columnId;
					gridColumns[x]["header"] = columnText;
					gridColumns[x]["dataIndex"] = columnDataIndex;
					gridColumns[x]["width"] = columnWidth;
					gridColumns[x]["sortable"] = columnSortable;

					if(gridReaderItems == null)
						gridReaderItems = new Array();

					var grix = gridReaderItems.length;
					gridReaderItems[grix] = { };
					gridReaderItems[grix]["name"] = columnId;
				}
			}

			// load from data
			var gridData = data.grid.griddata;

			if(gridData != null)
			{
				var gridPanelConfig = { };
				gridPanelConfig["viewConfig"] = { forceFit: true };
				gridPanelConfig["cls"] = "x-panel-blue";
				gridPanelConfig["width"] = 600;
				gridPanelConfig["height"] = 300;
				gridPanelConfig["frame"] = true;
				gridPanelConfig["iconCls"] = "icon-grid";		
				if(gridData != null)
				{
					var gridReader = new Ext.data.ArrayReader({}, gridReaderItems);
					gridPanelConfig["store"] = new Ext.data.Store({ reader: gridReader, data: gridData });
				}
				if(gridColumns != null)
					gridPanelConfig["columns"] = gridColumns;
				if(gridToolbar != null)
					gridPanelConfig["tbar"] = gridToolbar;
				gridPanelConfig["id"] = gridPanelId;

				var gp = new Ext.grid.GridPanel(gridPanelConfig);
				config["items"] = gp;

				// if there was already a grid panel with this id, release id
				if(Ext.ComponentMgr.get(gridPanelId) != null)
					Ext.ComponentMgr.unregister(Ext.ComponentMgr.get(gridPanelId));
				Ext.ComponentMgr.register(gp);
			}
			else
			{
				config["tbar"] = gridToolbar;
				config["html"] = "";
				if(data.grid.nodatamessage != null)
					config["html"] = data.grid.nodatamessage;
			}
		}

		return config;
	}

	function makeWindow(data)
	{
		var config = makeWindowConfiguration(data.current, data);
		var window = new Ext.Window(config);
		//focusWindow(data, window);
		return window;
	}	
}
