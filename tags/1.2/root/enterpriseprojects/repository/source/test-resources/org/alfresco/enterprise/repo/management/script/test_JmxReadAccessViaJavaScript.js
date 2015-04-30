<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function testQueryForNonExistentMBean()
{
   var beans = jmx.queryMBeans('Alfresco:Name=ThisBeanWillNotExist');
   assertArrayEquals([], beans, 'Query for non-existent bean misbehaved.')
}

function testQueryForSingleMBeanAndReadSimpleAttributes()
{
   var globalPropsBeanName = 'Alfresco:Name=GlobalProperties';
   var globalPropsBean = getSingleNamedBean(globalPropsBeanName);
   
   // Validate the bean's object-name properties. These are built in to the MBean ObjectName.
   test.assertEquals(globalPropsBeanName,      globalPropsBean.name);
   test.assertEquals('Alfresco',               globalPropsBean.domain);
   test.assertEquals('Property Dynamic MBean', globalPropsBean.description);
   test.assertEquals('org.alfresco.enterprise.repo.management.PropertiesDynamicMBean', globalPropsBean.className);
   
   // Validate the bean's attributes.
   test.assertNotNull(globalPropsBean.attributes,                     'attributes was null');
   test.assertNotNull(globalPropsBean.attributes['dir.contentstore'], "attributes['...'] was null");
   // Make sure we've retrieved all the attributes on this bean.
   // I'll cheat and assert it's more than "a large number". (545 attributes at time of writing.)
   test.assertTrue(globalPropsBean.attributes.length > 500);
   
   
   // Examine a read-only String attribute
   test.assertEquals('dir.contentstore',          globalPropsBean.attributes['dir.contentstore'].name);
   test.assertEquals('java.lang.String',          globalPropsBean.attributes['dir.contentstore'].className);
   test.assertEquals('${dir.root}/contentstore',  globalPropsBean.attributes['dir.contentstore'].value);
   test.assertEquals('Property dir.contentstore', globalPropsBean.attributes['dir.contentstore'].description);
   test.assertTrue(globalPropsBean.attributes['dir.contentstore'].isReadable);
   test.assertFalse(globalPropsBean.attributes['dir.contentstore'].isWritable);
   
   
   
   // Examine a writable String attribute (although we won't actually write to it in this function)
   var oooJodBeanName = 'Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default';
   var oooJodBean = getSingleNamedBean(oooJodBeanName);
   test.assertEquals('jodconverter.taskExecutionTimeout', oooJodBean.attributes['jodconverter.taskExecutionTimeout'].name);
   test.assertEquals('java.lang.String',                  oooJodBean.attributes['jodconverter.taskExecutionTimeout'].className);
   test.assertEquals('120000',                            oooJodBean.attributes['jodconverter.taskExecutionTimeout'].value);
   test.assertTrue(oooJodBean.attributes['jodconverter.taskExecutionTimeout'].isReadable);
   test.assertTrue(oooJodBean.attributes['jodconverter.taskExecutionTimeout'].isWritable);
}

/**
 * Query for all MBeans matching a pattern
 */
function testQueryForBeansByPattern()
{
   var beanPattern = 'Alfresco:Type=Configuration,*';
   var matchingBeans = jmx.queryMBeans(beanPattern);
   
   // There should be a list of matching beans. At the time of writing there are 23.
   test.assertTrue(matchingBeans.length > 20, 'Expected more MBeans');
}

/**
 * This function ensures that we are able to read the values of JMX attributes of various Java types.
 */
function testReadAttributeValuesOfVariousTypes()
{
   // I'm using an MBean that I've defined in test config (spring bean name = "TestJmx") so that I can control the Java types of the attributes.
   var beanName = 'Alfresco:Name=TestJmx';
   var bean = getSingleNamedBean(beanName);
   
   
   
   // Java primitives
   var attributeName = 'BooleanPrim';
   test.assertEquals('boolean', bean.attributes[attributeName].className);
   test.assertEquals(true,      bean.attributes[attributeName].value);
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'CharPrim';
   test.assertEquals('char', bean.attributes[attributeName].className);
   test.assertEquals('n',    bean.attributes[attributeName].value + ''); // I add "+ ''" to turn a char into a String - JavaScript only has Strings
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'BytePrim';
   test.assertEquals('byte', bean.attributes[attributeName].className);
   test.assertEquals(127,    bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn a byte into a double - JavaScript only has doubles
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'IntPrim';
   test.assertEquals('int', bean.attributes[attributeName].className);
   test.assertEquals(65535, bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn an int into a double
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'LongPrim';
   test.assertEquals('long', bean.attributes[attributeName].className);
   test.assertEquals(65535,  bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn a long into a double
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'FloatPrim';
   test.assertEquals('float', bean.attributes[attributeName].className);
   test.assertEquals(2.5,     bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn a float into a double
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'DoublePrim';
   test.assertEquals('double', bean.attributes[attributeName].className);
   test.assertEquals(3.5,      bean.attributes[attributeName].value);
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   
   // Java Objects
   var attributeName = 'Boolean';
   test.assertEquals('java.lang.Boolean', bean.attributes[attributeName].className);
   test.assertEquals(true,                bean.attributes[attributeName].value);
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'Char';
   test.assertEquals('java.lang.Character', bean.attributes[attributeName].className);
   test.assertEquals('n',                   bean.attributes[attributeName].value + ''); // I add "+ ''" to turn a char into a String - JavaScript only has Strings
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'Byte';
   test.assertEquals('java.lang.Byte', bean.attributes[attributeName].className);
   test.assertEquals(127,              bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn a byte into a double - JavaScript only has doubles
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'Int';
   test.assertEquals('java.lang.Integer', bean.attributes[attributeName].className);
   test.assertEquals(65535,               bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn an int into a double
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'Long';
   test.assertEquals('java.lang.Long', bean.attributes[attributeName].className);
   test.assertEquals(65535,            bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn a long into a double
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'Float';
   test.assertEquals('java.lang.Float', bean.attributes[attributeName].className);
   test.assertEquals(2.5,               bean.attributes[attributeName].value * 1.0); // I add "* 1.0" to turn a float into a double
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'Double';
   test.assertEquals('java.lang.Double', bean.attributes[attributeName].className);
   test.assertEquals(3.5,                bean.attributes[attributeName].value);
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   var attributeName = 'String';
   test.assertEquals('java.lang.String', bean.attributes[attributeName].className);
   test.assertEquals('Hello',            bean.attributes[attributeName].value);
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   
   // A JMX attribute value of type String[] (Java array of Strings) will not be converted
   var attributeName = 'StringArray';
   test.assertEquals('[Ljava.lang.String;', bean.attributes[attributeName].className); // '[Ljava.lang.String;' is the correct type for a Java String[],
                                                                                       // which is the MBean 'className'.
                                                                                       // We would not expect this to be 'scriptified' however we treated the value.
   var attrValue = bean.attributes[attributeName].value;
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   test.assertEquals(2, attrValue.length)
   test.assertEquals('Hello', attrValue[0])
   test.assertEquals('World', attrValue[1])
   
   var attributeName = 'StringList';
   test.assertEquals('java.util.List',   bean.attributes[attributeName].className);
   var attrValue = bean.attributes[attributeName].value;
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
   test.assertEquals(2, attrValue.length)
   test.assertEquals('Hello', attrValue[0])
   test.assertEquals('World', attrValue[1])
   
   var attributeName = 'Date';
   test.assertEquals('java.util.Date',           bean.attributes[attributeName].className);
   test.assertEquals(new Date(2010, 6, 15).time, bean.attributes[attributeName].value.time);
   test.assertFalse(bean.attributes[attributeName].isComposite);
   test.assertFalse(bean.attributes[attributeName].isTabular);
}

/**
 * This function retrieves ContentStore MBeans from JMX - without any knowledge of the types or names of these ContentStores.
 */
function testQueryForContentStores()
{
   // jmx.queryMBeans("Alfresco:Name=ContentStore*")
   //     returns an empty array
   //
   // jmx.queryMBeans("Alfresco:Name=ContentStore,Type=org.alfresco.repo.content.filestore.FileContentStore,Root=*")
   //     works but it requires you to put the ContentStore type into the query, which you may not in general know.
   //
   
   var beans = jmx.queryMBeans("Alfresco:Name=ContentStore,*");
   Packages.java.lang.System.err.println('beans count= ' + beans.length);
   for (var i = 0; i < beans.length; i++) {
      Packages.java.lang.System.err.println('' + beans[i]);
   }
}

/**
 * This function ensures that we can read attributes whose names are not valid JavaScript/Java identifiers.
 */
function testReadAttributesWithNonJavaScriptNames()
{
   var attributeName = 'alpha.beta*gamma';
   var bean = getSingleNamedBean('Alfresco:Name=TestJmxOddNames');
   
   var attribute = bean.attributes[attributeName];
   test.assertNotNull(attribute, 'Attribute was null');
   
   var attributeValue = attribute.value;
   test.assertNotNull(attributeValue, 'Attribute value was null');
   test.assertEquals('initial value', attributeValue, 'Attribute value was wrong.');
}


// Execute tests
testQueryForNonExistentMBean();
testQueryForSingleMBeanAndReadSimpleAttributes();
testQueryForBeansByPattern();
testReadAttributeValuesOfVariousTypes();
testQueryForContentStores();
testReadAttributesWithNonJavaScriptNames();
