if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
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
};

WebStudio.Form.prototype = new WebStudio.AbstractTemplater('WebStudio.Form');

WebStudio.Form.prototype.activate = function()
{
		this.buildGeneralLayer();
		return this;
};

WebStudio.Form.prototype.getFormData = function() 
{
	var elements = [];
	
	var i = 0;
	var el = null;
	
	var inputs = this.BodyContent.el.getElementsBySelector("input,textarea");
	for (i = 0; i < inputs.length; i++) 
	{	
		elements[i] = {};
		var o = elements[i];
		o.name = inputs[i].name;
		o.value = inputs[i].value;	
	}	
	for(i = 0, ln = this.radioBtns.length; i < ln; i++)
	{
		el = {};
		el.name = this.radioBtns[i].getGroupName();
		el.value = this.radioBtns[i].getCheckedValue();
		elements.push(el);
	}
	for(i = 0, ln = this.comboEls.length; i < ln; i++)
	{
		el = {};
		el.name = this.comboEls[i].getName();
		el.value = this.comboEls[i].getSelectedItemValue();
		elements.push(el);
	}
	
	return elements;
};

WebStudio.Form.prototype.buildFormFields = function(data)
{
	// Create table that will contain the form elements.
	var formTable = document.createElement("table");	
	formTable.setAttribute("id", "wizard_form_table");
	WebStudio.util.setStyle(formTable, "width", "100%");	
	var formTBody = document.createElement("tbody");	
	WebStudio.util.injectInside(formTable, formTBody);
	
	// Add table to container.
	WebStudio.util.injectInside(this.BodyContent.el, formTable);	
			
	// Loop through fields for form and add them to the table.
	for(var field in data)
	{
		if(data.hasOwnProperty(field))
		{
			this.buildFormCurrentField(data[field], formTBody);
		}
	}	
};

WebStudio.Form.prototype.buildFormCurrentField = function(item, formTBody)
{
	var _this = this;
	
	// footer elements which appear at the bottom of the form
	if (item.content == "footer") 
	{
		if(!item.hidden)
		{
			var _t = new Date().getTime();
			var bid = "_id" + _t + "" + $random(0,10000);
			if (item.type == "button") 
			{
				var el = new Element('input', {
					id:bid,
					value :item.text,
					disabled:!item.enabled
				});
			}
			var r = this.FooterContent.el;
			var c1 = r.insertCell(-1);
			c1.appendChild(el);
			var yuibutton = new YAHOO.widget.Button(bid, 
			{
				onclick: { 
					fn: function() 
					{
						item.events.click((item.action ? item.action : ""), item.data);	
					}
				}
			});
			return true;
		}
	}
	
	// body elements which appear at the middle of the form
	if (item.content == "body")
	{
		if(item.type == 'text')
		{	
			// wrap in a function so that variables are kept in
			// a local scope
			var f1 = function()
			{	
				// Create TR element
				var tr = document.createElement("tr");
	
				// Add to Form's Table Body element.
				WebStudio.util.injectInside(formTBody, tr);
				
				// Create TD for field label
				var labelTD = document.createElement("td");			
				
				// Add to TR
				WebStudio.util.injectInside(tr, labelTD);
				
				// Create tD for form field element
				var fieldTD = document.createElement("td");
				
				// Add to TR
				WebStudio.util.injectInside(tr, fieldTD);
				
				// Create text node.
				var tn = document.createElement('text');			
				
				// Set text.
				WebStudio.util.pushHTML(tn, item.label);
				
				// Add to label TD.
				WebStudio.util.injectInside(labelTD, tn);				
						
				// Create input field node
				var ifn = document.createElement('input');
				
				// Set name of input field
				ifn.name = item.name;
				
				// Set input field value
				ifn.value = item.value;						
				
				// Add input field node to fieldTD.
				WebStudio.util.injectInside(fieldTD, ifn);
			};
			
			// call the function right away
			f1.bind(this).attempt();
		}
		if (item.type == 'textarea')
		{
			// wrap in a function so that variables are kept in
			// a local scope		
			var f2 = function()
			{
				// Create TR element
				var tr = document.createElement("tr");
	
				// Add to Form's Table Body element.
				WebStudio.util.injectInside(formTBody, tr);
				
				// Create TD for field label
				var labelTD = document.createElement("td");			
				
				// Add to TR
				WebStudio.util.injectInside(tr, labelTD);
				
				// Create tD for form field element
				var fieldTD = document.createElement("td");
				
				// Add to TR
				WebStudio.util.injectInside(tr, fieldTD);
				
				// Create text node.
				var tn = document.createElement('text');			
				
				// Set text.
				WebStudio.util.pushHTML(tn, item.label);
				
				// Add to label TD.
				WebStudio.util.injectInside(labelTD, tn);				
						
				// Create input field node
				var ifn = document.createElement('textarea');
				
				// Set name of input field
				ifn.name = item.name;
				
				// Set input field value
				ifn.value = item.value;						
				
				// Add input field node to fieldTD.
				WebStudio.util.injectInside(fieldTD, ifn);
			};
			
			// call the function right away
			f2.bind(this).attempt();
		}
		if(item.type == 'hidden')
		{
			// wrap in a function so that variables are kept in
			// a local scope		
			var f3 = function()
			{		
				var hf = document.createElement('input');
				hf.type = "hidden";
				hf.name = item.name;
				hf = $(hf);
				hf.value = item.value;
				WebStudio.util.injectInside(this.BodyContent.el, hf);
			};
			
			// call the function right away
			f3.bind(this).attempt();
		}
		if(item.type == 'radio')
		{
			// wrap in a function so that variables are kept in
			// a local scope		
			var f4 = function()
			{			
				if (item.controls)
				{
					var control = WebStudio.util.clone(this.AlfFormRadio.el);
	
					WebStudio.util.injectInside(this.BodyContent.el, control);
					
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
						WebStudio.util.pushHTML(label, item.name);	      			
		      		}
		      		else
		      		{
						WebStudio.util.pushHTML(label, "");	      			
		      		}
		      		
					var br = document.createElement("br");
					WebStudio.util.injectInside(this.BodyContent.el, br);				
				}
				this.radioBtns.push(radioGroup);
			};
			
			// call the function right away
			f4.bind(this).attempt();
		}
		if (item.type == 'combo')
		{		
			// wrap in a function so that variables are kept in
			// a local scope				
			var f5 = function()
			{	
				var br = document.createElement("br");
				WebStudio.util.injectInside(this.BodyContent.el, br);
				
				var control = WebStudio.util.clone(this.AlfFormCombo.el);			
				
				WebStudio.util.injectInside(this.BodyContent.el, control);
				
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
							fn: _this.comboButtonClick.bind(_this)
						}
					};
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
				};
				if(!item.emptytext)
				{
					combolist.label = "";
				}
				else
				{
					combolist.label = item.emptytext; 
				}
	
				WebStudio.util.pushHTML(label, "");
				
				if(item.name)
				{
					WebStudio.util.pushHTML(label, item.name);				
				}
				if(item.title)
				{
					WebStudio.util.pushHTML(label, item.title);				
				}
				var nm = new WebStudio.Combobox(combolist);
				
				this.comboEls.push(nm);
				
				// we have to walk back through the drop down list
				// rebind the click handlers so they point to our
				// combox instance
				for(var t = 0; t < dropdownlist.length; t++)
				{
					// menu item
					var mi = dropdownlist[t];
					mi["onclick"] = { fn: _this.comboButtonClick.bind(nm) };
				}
								
				var br9 = document.createElement("br");
				WebStudio.util.injectInside(this.BodyContent.el, br9);
			};
			
			// call the function right away
			f5.bind(this).attempt();
		}
		if (item.type == 'url')
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
			
			WebStudio.util.injectInside(this.BodyContent.el, fu);			
		}
		if('html' == item.type)
		{
			WebStudio.util.pushHTML(this.BodyContent.el, item.html);			
		}
		this.formType = item.type;
	}	
};

WebStudio.Form.prototype.getGrid = function()
{
	return (this.grid ? this.grid : null);
};

// this = nm
WebStudio.Form.prototype.comboButtonClick = function(p_sType, p_aArgs, p_oItem)
{
	var nm = this;
	nm.set("label", p_oItem.cfg.getProperty("text"));
};
