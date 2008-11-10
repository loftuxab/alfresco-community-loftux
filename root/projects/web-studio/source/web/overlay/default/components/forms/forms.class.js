
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.Form = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;
	
	this.defaultFormSelector = 'div[id=AlfrescoForm]';
	this.defaultElementsConfig = {
		Caption: {
			selector: '.alf-form-caption'
        },
		Body: {
			selector: '.alf-form-body'
		},
        BodyContent: {
			selector: '.alf-form-body-content'
		},
		Footer: {
			selector: '.form-footer'
		},
        FooterContent: {
			selector: '.alf-footer-content'
		},
		AlfFormFieldText:{
			selector: '#AlfFormFieldText',
			remove:true
		},
		AlfFormFieldTextArea:{
			selector: '#AlfFormFieldTextArea',
			remove:true
		},
		AlfFormFieldGrid:{
			selector: '#AlfFormFieldGrid',
			remove:true
		},
		AlfFormRadio:{
			selector: '#AlfFormRadio',
			remove:true
		},
		AlfFormCombo:{
			selector: '#AlfFormCombo',
			remove:true
		}
	};
	this.formType = null;
	this.radioBtns = [];
	this.grid = null;
	this.comboEls = [];
	
	this.focusElement = null;
}

WebStudio.Form.prototype = new WebStudio.AbstractTemplater('WebStudio.Form');

WebStudio.Form.prototype.activate = function()
{
		this.buildGeneralLayer();
		return this;
}

WebStudio.Form.prototype.getFormData = function() 
{
	var elements = [];
	
	var inputs = this.BodyContent.el.getElementsBySelector("input,textarea");
	for (var i = 0; i < inputs.length; i++) {	
		var o = elements[i] = {};
		o.name = inputs[i].name;
		o.value = inputs[i].value;	
	}
	
	for(var i = 0, ln = this.radioBtns.length; i < ln; i++)
	{
		var el = {};
		el.name = this.radioBtns[i].getGroupName();
		el.value = this.radioBtns[i].getCheckedValue();
		elements.push(el);
	}
	for(var i = 0, ln = this.comboEls.length; i < ln; i++)
	{
		var el = {};
		el.name = this.comboEls[i].getName();
		el.value = this.comboEls[i].getSelectedItemValue();
		elements.push(el);
	}
	return elements;
}

WebStudio.Form.prototype.buildFormFields = function(data)
{
	for(var field in data)
	{
		this.buildFormCurrentField(data[field]);
	}
}

WebStudio.Form.prototype.buildFormCurrentField = function(item)
{
	var _this = this;
	
	// footer elements which appear at the bottom of the form
	if (item.content == "footer") 
	{
		if(!item.hidden)
		{
			var bid = "_id" + (new Date().getTime()) + $random(0,10000);
			if (item.type == "button") {
				var el = new Element('input', {
					id:bid,
					value :item.text,
					disabled:!item.enabled
				});
			}
			var r = this.FooterContent.el;
			var c1 = r.insertCell(-1);
			c1.appendChild(el);
			var yuibutton = new YAHOO.widget.Button(bid, {onclick: { fn: function() {
				item.events.click((item.action) ? item.action : "", item.data);	
			}
			}});
			return true;
		}
	}
	
	// body elements which appear at the middle of the form
	if (item.content == "body")
	{
		if(item.type == 'text')
		{
			var control = this.AlfFormFieldText.el.clone();
			control.injectInside(this.BodyContent.el);
			var label = control.getElementsBySelector(".alf-form-label")[0];
			var field = control.getElementsBySelector(".alf-form-field")[0];
			
			label.innerHTML = item.label;
			field.value = item.value;
			field.name = item.name;
			
			var br = document.createElement("br");
			br.injectInside(this.BodyContent.el);
			
			if(!this.focusElement)
			{
				this.focusElement = field;
			}
		}
		if (item.type == 'textarea')
		{
			var control = this.AlfFormFieldTextArea.el.clone();
			control.injectInside(this.BodyContent.el);
			
			var label = control.getElementsBySelector(".alf-form-label")[0];
			var field = control.getElementsBySelector(".alf-form-field")[0];
			
			label.innerHTML = item.label;
			field.value = item.value;
			field.name = item.name;
			
			var br = document.createElement("br");
			br.injectInside(this.BodyContent.el);

			if(!this.focusElement)
			{
				this.focusElement = field;
			}			
		}
		if(item.type == 'hidden')
		{
			var hf = document.createElement('input');
			hf.type = "hidden";
			hf = $(hf);
			hf.value = item.value;
			hf.name = item.name;
			hf.injectInside(this.BodyContent.el);

			var br = document.createElement("br");
			br.injectInside(this.BodyContent.el);			
		}
		if(item.type == "grid")
		{
			var gt = this.AlfFormFieldGrid.el.clone();
			gt.injectInside(this.BodyContent.el);

			//var gridTemplate = gt.getElementsBySelector('div[id=gridTemplate]')[0];
			var toolPanel = gt.getElementsBySelector('div[id=gridToolbar]')[0];
			var gridDataTable = gt.getElementsBySelector('div[id=gridDataTable]')[0];
			item.dataTable = gridDataTable;

			if(item.dataSource)
			{
				var table = new WebStudio.SelectSingleGrid("gridDataTable", item.columnFormats, item.columns, item.dataSource, {scrollable:true, width:"30em", height:"10em"});
				// Subscribe to events for row selection
				table.subscribe("rowMouseoverEvent", table.onEventHighlightRow);
				table.subscribe("rowMouseoutEvent", table.onEventUnhighlightRow);
				table.subscribe("rowClickEvent", table.onEventSelectRow);
				this.grid = table;
			}
			else
			{
				if (item.nodatamessage)
				{
					gridDataTable.setHTML(item.nodatamessage);
				}
			}
			
			var _grid = this.grid;

			if(item.toolbar)
			{
				item.toolbar.each(function(it)
				{
					var el = new Element('input',{
						id: it.id,
						type: "button"
					});				
					toolPanel.appendChild(el);

					new YAHOO.widget.Button(it.id,{
						type: "button",
						title: it.tooltip,
						label: it.text,
						onclick:
						{
							fn: function()
							{
								var clickedId = it.id;
								WebStudio.app.gridButtonClick(it.id, _grid);
								
								//item.toolbarhandler(it.id,null);
							}
						}
					});
				});
			}
			
			var br = document.createElement("br");
			br.injectInside(this.BodyContent.el);			
		}
		if('radio' == item.type)
		{
			if (item.controls)
			{
				var control = this.AlfFormRadio.el.clone();
				control.injectInside(this.BodyContent.el);
				var label = control.getElementsBySelector(".alf-form-label")[0];
				var container = control.getElementsBySelector(".alf-form-field-container")[0];

				// build the radio group				
			    var radioGroup = new WebStudio.ButtonGroup(
	   			{
	         		id:  "buttongroup",
	         		name:  item.name,
					container:  container
	        	});
	
				// copy in radio group items
	      		item.controls.each(function(it)
	      		{
					radioGroup.addButton(
					{
						label: it.label,
						value: it.value,
						checked: it.checked || false
					});
	      		});
	      		
	      		// set up label
	      		if(item.name)
	      		{
	      			label.innerHTML = item.name;
	      		}
	      		else
	      		{
	      			label.innerHTML = "";
	      		}
	      		
				var br = document.createElement("br");
				br.injectInside(this.BodyContent.el);
			}
			this.radioBtns.push(radioGroup);
		}
		if ('combo' == item.type)
		{
			var control = this.AlfFormCombo.el.clone();
			control.injectInside(this.BodyContent.el);
			var label = control.getElementsBySelector(".alf-form-label")[0];
			var container = control.getElementsBySelector(".alf-form-field-container")[0];
					
			// build the dropdown list	
			var dropdownlist = [];			
			for (var i = 0; i < item.value.length; i++)
			{
				var menuitem = 
				{
					text: item.value[i][1],
					value: item.value[i][0],
					onclick:
					{
						fn: function(p_sType, p_aArgs, p_oItem)
						{
							nm.set("label", p_oItem.cfg.getProperty("text"));
						}
					}
				}
				dropdownlist.push(menuitem);
			}
			
			// build the combo list
			var combolist = 
			{
				type: "menu",
				name: item.name,
				menu: dropdownlist,
				width: item.width,
				container: container
			}
			if(!item.emptytext)
			{
				combolist.label = "";
			}
			else
			{
				combolist.label = item.emptytext; 
			}
			label.innerHTML = "";
			if(item.name)
			{
				label.innerHTML = item.name;
			}
			if(item.title)
			{
				label.innerHTML = item.title;
			}
			var nm = new WebStudio.Combobox(combolist);
			this.comboEls.push(nm);
			
			var br = document.createElement("br");
			br.injectInside(this.BodyContent.el);
		}
		if ('url' == item.type)
		{
			var superUrl = WebStudio.url.studio(item.url+"?contextPath="+WebStudio.url.studio(''));			
			var fu = new Element('iframe',
			{
				src: superUrl,
				styles: {
					width: 470,
					height: 200,
					border: '0px',
					frameborder: '0'
				}
			});	
			
			fu.injectInside(this.BodyContent.el);
			
			//var br = document.createElement("br");
			//br.injectInside(this.BodyContent.el);			
		}
		if('html' == item.type)
		{
			this.BodyContent.el.innerHTML = item.html;
		}
		this.formType = item.type;
	}	
}

WebStudio.Form.prototype.getGrid = function()
{
	return (this.grid)? this.grid : null;
}