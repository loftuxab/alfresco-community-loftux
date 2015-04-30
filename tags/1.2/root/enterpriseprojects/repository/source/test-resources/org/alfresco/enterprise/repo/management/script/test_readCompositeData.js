<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function test_()
{
   // Get the bean & the attribute provided by the Java @Test method - in this case it's the 'Alfresco:Name=RunningActions' bean
   var bean = getSingleNamedBean(beanName);
   test.assertNotNull(bean, 'bean was null');
   
   // The attributeName again comes from the Java @Test method - in this case it's 'ActionStatistics'.
   var attr = bean.attributes[attributeName];
   
   // Some basic sanity checks on the attribute itself i.e. its metadata.
   test.assertNotNull(attr, 'attr was null');
   test.assertEquals('ScriptMBeanAttribute', attr.getClass().simpleName);
   // The className is the Java type as stored at runtime. For this attribute it's an array of CompositeData, hence the '[L*;' pattern.
   // This comes from Java.
   test.assertEquals('[Ljavax.management.openmbean.CompositeData;', attr.className);
   test.assertEquals('ActionStatistics', attr.name);
   test.assertTrue(attr.isComposite, 'Attribute was expected to be composite.');
   
   // Some basic sanity checks on the attribute value.
   var attrVal = attr.value;
   test.assertNotNull(attrVal, 'attr.value was null');
   
   // The attribute value will be an array of ScriptCompositeValue objects.
   test.assertTrue(attr.value.length >= 2, 'attr.value was not an array of length >= 2');
   
   // Extract the array elements for further validation.
   var component0 = attr.value[0];
   var component1 = attr.value[1];
   test.assertNotNull(component0, 'attr value component was null');
   test.assertNotNull(component1, 'attr value component was null');
   
   // Let's look a little closer at one of the array elements. They should both be broadly equivalent, so we don't
   // need to validate both.
   //
   // Starting with its metadata
   test.assertFalse(component0.componentIsArray, 'Expected a non-array type.');
   test.assertEquals('javax.management.openmbean.CompositeData', component0.componentJavaClassName);
   test.assertEquals('org.alfresco.repo.action.ActionStatistics', component0.componentJavaTypeName);
   test.assertEquals('org.alfresco.repo.action.ActionStatistics', component0.componentDescription);
   
   // And now going in to the actual data a little.
   // We retrieve the data from the dataMap by key. In order to get these values, we're using the keys that we know from looking
   // at the Alfresco MBeans - in this case from the ActionStatistics class.
   // The dataMap is the normal Alfresco ScriptableHashMap
   test.assertNotNull(component0.dataMap['actionName'], 'Expected a value for key=actionName');
   test.assertNotNull(component0.dataMap['invocationCount'], 'Expected a value for key=invocationCount');
   test.assertTrue(component0.dataMap['invocationCount'] > 0, 'invocationCount should be a positive number');
   
   // Note that the dataMap is a ScriptableHashMap, but that there is no way (that I can see) to get the keySet for that map.
   // The usual .keySet().iterator() doesn't get past Rhino - "keySet() is not a function".
   //
   // So a dedicated dataKeys() accessor has been provided on the ScriptCompositeValue object.
   var keys = component0.dataKeys;
   for (var i = 0; i < keys.length; i++)
   {
      // e.g.   "key[1]: averageTime = 32"
      Packages.java.lang.System.err.println('key[' + i + ']: ' + keys[i] + ' = ' + component0.dataMap[keys[i]]);
   }
}

function readCompositeDataReturnedFromOperation()
{
   // CompositeData can also be exposed not as an attribute value, but as the return value from a JMX operation.
   // An example of this is the bean below.
   var matchingBeans = jmx.queryMBeans("java.lang:type=Threading");
   var bean = matchingBeans[0];
   
   // Firstly, we'll look at getThreadInfo(p) which returns a CompositeData instance.
   // We'll choose thread 30 - pretty much at random.
   var threadInfo = bean.operations.getThreadInfo_(['long'], 30);
   
   // Make sure the result object was converted ok.
   test.assertEquals('ScriptCompositeValue', threadInfo.getClass().simpleName);
   
   // Now read some data from it - firstly by looping through the keys
   var keys = threadInfo.dataKeys;
   for (var i = 0; i < keys.length; i++)
   {
      // e.g.   "key[1]: averageTime = 32"
      Packages.java.lang.System.err.println('key[' + i + ']: ' + keys[i] + ' = ' + threadInfo.dataMap[keys[i]]);
   }
   
   // Alternatively we can just jump in and get data by key if we know the key.
   test.assertNotNull(threadInfo.dataMap['threadState']);
   
   
   
   
   // Secondly we'll look at dumpAllThreads(p, p) which returns a CompositeData[]
   // This is an array of the same data objects as are returned above.
   
   var threads = bean.operations.dumpAllThreads(true, true);
}

// Execute tests
test_();
readCompositeDataReturnedFromOperation();
