/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

/**
 * This function retrieves an MBean based on the supplied beanName.
 * This beanName must uniquely identify exactly one MBean.
 */
function getSingleNamedBean(beanName)
{
   var matchingBeans = jmx.queryMBeans(beanName);
   
   // There should be one and only one matching bean
   test.assertEquals(1, matchingBeans.length, 'Single bean not found: ' + beanName);
   
   // Take it out of the array
   var result = matchingBeans[0];
   return result;
}

/** This function compares two arrays returning true if they are of equal length and have equal elements.
 *  This will work even if one array is a JS-native array (e.g. ['hello', 'world']) and the other is a Java array.
 */
function assertArrayEquals(array1, array2, message)
{
   test.assertEquals(array1.length, array2.length, 'array length was wrong');
   for (var i = 0; i < array1.length; i++)
   {
      test.assertEquals(array1[i], array2[i], 'array element was wrong');
   }
}

