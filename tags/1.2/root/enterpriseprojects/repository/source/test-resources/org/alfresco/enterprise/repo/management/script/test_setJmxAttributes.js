<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function updateAttributes()
{
   var bean = getSingleNamedBean(beanName);
   
   for (var i = 0; i < attributeNames.length; i++)
   {
      Packages.java.lang.System.out.println('Setting value ' + i + ' to: ' + attributeValues[i]);
      bean.attributes[attributeNames[i]].value = attributeValues[i];
   }
   
   // and we must explicitly 'save' this change.
   jmx.save(bean);
}


// Execute tests
updateAttributes();
