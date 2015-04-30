<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function testUpdateArrayElement()
{
   var bean = getSingleNamedBean(beanName);
   
   var attributeValue = bean.attributes[attributeName].value;
   
   // This attribute is a String[]
   // Let's update one element of that array.
   attributeValue[0] = newElementValue;
   
   // and don't forget to save
   jmx.save(bean);
}


// Execute tests
testUpdateArrayElement();
