<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function updateSimpleStringAttribute()
{
   var bean = getSingleNamedBean(beanName);
   
   Packages.java.lang.System.out.println('Setting value to: ' + newValue);
   
   bean.attributes[attributeName].value = newValue;
   
   // and we must explicitly 'save' this change.
   jmx.save(bean);
}



// Execute tests
updateSimpleStringAttribute();
