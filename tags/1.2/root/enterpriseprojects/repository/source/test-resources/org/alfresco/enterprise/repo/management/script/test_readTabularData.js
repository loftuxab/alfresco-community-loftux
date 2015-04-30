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
   // Get the bean & the attribute provided by the Java @Test method.
   var bean = getSingleNamedBean(beanName);
   test.assertNotNull(bean, 'bean was null');
   
   var attr = bean.attributes[attributeName];
   
   // Some basic sanity checks on the attribute itself i.e. its metadata.
   test.assertNotNull(attr, 'attr was null');
   test.assertEquals('ScriptMBeanAttribute', attr.getClass().simpleName);
   test.assertEquals('javax.management.openmbean.TabularData', attr.className);
   test.assertEquals('TabularData', attr.name);
   test.assertFalse(attr.isComposite, 'Attribute was not expected to be composite.');
   test.assertTrue(attr.isTabular, 'Attribute was expected to be tabular.');
   
   // Basic sanity checks on the attribute value.
   var attrVal = attr.value;
   test.assertNotNull(attrVal, 'attr.value was null');
   
   // The attrVal will be an instance of ScriptMBeanAttributeTabularValue...
   // ...which is a data container...
   test.assertFalse(attrVal.isEmpty(), 'Tabular data not expected to be empty.');
   // ...exposing rows of data
   test.assertEquals(2, attrVal.size,  'Wrong number of rows (size) in tabular data set.');
   
   // Obviously we can get at these rows. They're a JS array
   var row0 = attrVal.rows[0];
   var row1 = attrVal.rows[1];
   test.assertNotNull(row0, 'row 0 was null');
   test.assertNotNull(row1, 'row 1 was null');
   
   // The row objects themselves are ScriptCompositeValue objects...
   test.assertEquals('javax.management.openmbean.CompositeData', row0.componentJavaClassName);
   test.assertEquals('Row typename',                             row0.componentJavaTypeName);
   test.assertEquals('Row description',                          row0.componentDescription);
   
   // ...and we can access the dataMap - which holds the item values.
   test.assertNotNull(row0.dataMap, 'row0.dataMap was null');
   test.assertEquals(2,       row0.dataMap.length);
   test.assertEquals('Alpha', row0.dataMap[0]);
   test.assertEquals('Beta',  row0.dataMap[1]);
   
   // But the metadata about those rows of data are in...
   test.assertNotNull(attrVal.itemNames, 'attrVal.itemNames was null');
   test.assertEquals(2, attrVal.itemNames.length);
   test.assertEquals('item1', attrVal.itemNames[0]);
   test.assertEquals('item2', attrVal.itemNames[1]);
   
   //... and the Java types of those items...
   test.assertNotNull(attrVal.itemTypes, 'attrVal.itemTypes was null');
   test.assertEquals(2, attrVal.itemTypes.length);
   test.assertEquals('java.lang.String', attrVal.itemTypes[0]);
   test.assertEquals('java.lang.String', attrVal.itemTypes[1]);
}

// Execute tests
test_();
