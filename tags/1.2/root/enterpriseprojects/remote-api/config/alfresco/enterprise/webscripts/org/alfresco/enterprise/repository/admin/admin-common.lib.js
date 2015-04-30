<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">

/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

/**
 * Repository Admin Console
 * 
 * Common JavaScript library functions. Extensions for JMX enterprise beans.
 * 
 * @author Kevin Roast
 */

(function() {

   Admin.enterprise = true;
   
   /**
    * Retrieve a single named MBean
    * 
    * @param bean       Bean name e.g. "Alfresco:Name=License"
    * @return the MBean object if found, else null
    */
   Admin.getMBean = function getMBean(bean)
   {
      var found = null,
          beans = jmx.queryMBeans(bean);
      if (beans.length === 1)
      {
         found = beans[0];
      }
      return found;
   }
   
   /**
    * Retrieve a map of MBean attribute names to attribute wrappers from a given MBean name.
    * 
    * @param bean       Bean name e.g. "Alfresco:Name=License"
    * @param attributes Hash of attribute names e.g. ["Subject", "LicenseMode"] or empty for all attributes
    * @return Map of MBean attribute names to attribute wrappers {"Subject"={...}, "LicenseMode"={...}}
    */
   Admin.getMBeanAttributes = function getMBeanAttributes(bean, attributes)
   {
      var attrs = {},
          mbean = Admin.getMBean(bean);
      if (mbean)
      {
         if (attributes && attributes.length !== 0)
         {
            for (var i=0; i<attributes.length; i++)
            {
               var attrName = attributes[i],
                   attribute = mbean.attributes[attrName];
               attrs[attrName] = convertAttribute(bean, attrName, attribute);
            }
         }
         else
         {
            var beanAttributes = mbean.attributes;
            for (var i=0; i<beanAttributes.length; i++)
            {
               attrs[beanAttributes[i].name] = convertAttribute(bean, null, beanAttributes[i]);
            }
         }
      }
      
      return attrs;
   }
   
   /**
    * Retrieve an array of MBean CompositeData properties from a given MBean name and attribute.
    * 
    * @param bean       Bean name e.g. "Alfresco:Name=ModuleService"
    * @param attribute  Attribute name e.g. "AllModules"
    * @param properties Hash of property names e.g. ["module.id", "module.version"]
    * @return Array of MBean CompositeData properties e.g. [0]=["module.id"]="module1",["module.version"]="1.1",[1]=["module.id"]="module2",["module.version"]="2.2",etc.
    */
   Admin.getCompositeDataAttributes = function getCompositeDataAttributes(bean, attribute, properties)
   {
      var values = [];
      var mbean = Admin.getMBean(bean);
      if (mbean && attribute)
      {
         var attr = mbean.attributes[attribute];
         if (attr)
         {
            var attrValue = attr.value;
            for (var i=0; i<attrValue.length; i++)
            {
               var props = [];
               var component = attrValue[i];
               for (var j=0; j<properties.length; j++)
               {
                  var propName = properties[j];
                  var propValue = component.dataMap[propName];
                  props[propName] = propValue;
               }
               values[i] = props;
            }
         }
      }
      
      return values;
   }
   
   /**
    * Retrieve an array of MBean TabularData properties from a given MBean name and attribute.
    * 
    * @param bean       Bean name e.g. "Alfresco:Name=ModuleService"
    * @param attribute  Attribute name e.g. "AllModules"
    * @param properties Hash of property names e.g. ["module.id", "module.version"]
    * @return Array of row data objects with name, type and value e.g. 
    *          [{
    *             "module.id": {
    *                "name": "module.id",
    *                "type": "java.lang.String",
    *                 "value": "module 1"
    *             },
    *             "module.version": {
    *                "name": "module.version",
    *                "type": "java.lang.Integer",
    *                 "value": "1.1"
    *             }
    *          },
    *          {
    *             "module.id": {
    *                "name": "module.id",
    *                "type": "java.lang.String",
    *                 "value": "module 2"
    *             },
    *             "module.version": {
    *                "name": "module.version",
    *                "type": "java.lang.Integer",
    *                 "value": "2.0"
    *             }
    *          }]
    *             
    */
   Admin.getTabularDataAttributes = function getTabularDataAttributes(bean, attribute, properties)
   {
      var rows = [];
      var mbean = Admin.getMBean(bean);
      if (mbean && attribute)
      {
         var attr = mbean.attributes[attribute];
         if (attr && attr.isTabular)
         {
            var attrValue = attr.value;
            for (var i=0; i<attrValue.size ;i++)
            {
               var row = {};
               var rowData = attrValue.rows[i];
               for (var j=0; j<rowData.dataMap.length; j++)
               {
                  if (properties.indexOf(attrValue.itemNames[j]) > -1)
                  {
                     row[attrValue.itemNames[j]] = {
                           "name": attrValue.itemNames[j],
                           "type": attrValue.itemTypes[j],
                           "value": rowData.dataMap[j]
                     };
                  }
               }
               rows[i] = row;
            }
         }
      }

      return rows;
   }
   
   /**
    * Initialise the template model with default objects
    * 
    * @param bean       Bean name e.g. "Alfresco:Name=License"
    * @param attributes Array of attribute names to collect or empty for all
    * @param tool       ID of the current console tool e.g. "admin-systemsummary"
    */
   Admin.initModel = function initModel(bean, attributes, tool)
   {
      model.attributes = Admin.getMBeanAttributes(bean, attributes);
      model.tools = Admin.getConsoleTools(tool);
      model.metadata = Admin.getServerMetaData();
   }
   
   /**
    * Persist POSTed JMX form data.
    * 
    * Walks the WebScript "formdata" model - looks for JMX fields of format: mbean:name|attribute:name
    */
   Admin.persistJMXFormData = function persistFormData()
   {
      var persistBeans = Admin.getBeansForJMXFormData();
      
      // save the modified bean list
      for each (var bean in persistBeans)
      {
         jmx.save(bean);
      }
   }
   
   /**
    * Optional callback when an attribute has been set on a bean
    * signature: fn(string:attributeName, string:value, object:mbean);
    */
   Admin.getBeansForJMXFormData = function getBeansForJMXFormData(fnCallBack)
   {
      // retrieve the posted mbean attributes
      var persistBeans = {};
      for each (var field in formdata.fields)
      {
         var sepIndex = field.name.indexOf("|");
         if (sepIndex !== -1)
         {
            // found an mbean+attribute field?
            var mbean = field.name.substring(0, sepIndex),
                attrname = field.name.substring(sepIndex + 1),
                attrvalue = field.value;
            
            // use the JMX API to set the attribute field value
            if (!persistBeans[mbean])
            {
               var beans = jmx.queryMBeans(mbean);
               if (beans.length === 1)
               {
                  // cache this bean as dirty ready for save later
                  persistBeans[mbean] = beans[0];
               }
            }
            
            // check again to ensure we found a bean with the specified name
            if (persistBeans[mbean])
            {
               // found bean - set attribute value - bean will still need saving later
               persistBeans[mbean].attributes[attrname].value = attrvalue;
               
               if (fnCallBack) fnCallBack.call(this, attrname, attrvalue, persistBeans[mbean]);
            }
         }
      }
      return persistBeans;
   }
   
   /**
    * Helper to convert a MBean API Attribute object to simple JavaScript object
    */
   function convertAttribute(bean, attrName, attribute)
   {
      if (attribute)
      {
         return {
            qname: bean + "|" + attribute.name,
            name:  attribute.name,
            value: attribute.value,
            description: attribute.description,
            type: attribute.className,
            readonly: !attribute.isWritable
         };
      }
      else
      {
         return {
            qname: "",
            name:  attrName,
            value: "",
            description: "",
            type: "",
            readonly: true
         };
      }
   }

})();

/**
 * END Repository Admin Console - Common JavaScript library functions.
 */
