<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function testWriteAttributeValuesOfVariousTypes()
{
   // I'm using an MBean that I've defined in test config (spring bean name = "TestJmx") so that I can control the Java types of the attributes.
   var beanName = 'Alfresco:Name=TestJmx';
   var bean = getSingleNamedBean(beanName);
   
   
   // In the updates below, we make an assertion about the value before and after the call to save().
   // We do this in order to test the value that is cached in the ScriptMBeanAttribute object and also the value that is written to JMX.
   
   // Also, we have to call jmx.save(bean) so that we have permissions checks on the save() call.
   // I'd prefer to have bean.save(), but that's not currently possible.
   
   
   // Java primitives
   var attributeName = 'BooleanPrim';
   bean.attributes[attributeName].value = true;
   test.assertEquals(true, bean.attributes[attributeName].value);
   jmx.save(bean);
   test.assertEquals(true, bean.attributes[attributeName].value);
   
   var attributeName = 'CharPrim';
   bean.attributes[attributeName].value = 'x';
   test.assertEquals('x', bean.attributes[attributeName].value + ''); // Appending '' to make types the same
   jmx.save(bean);
   test.assertEquals('x', bean.attributes[attributeName].value + '');
   
   var attributeName = 'BytePrim';
   bean.attributes[attributeName].value = 44;
   test.assertEquals(44, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(44, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'IntPrim';
   bean.attributes[attributeName].value = 22;
   test.assertEquals(22, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(22, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'LongPrim';
   bean.attributes[attributeName].value = 1234567;
   test.assertEquals(1234567, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(1234567, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'FloatPrim';
   bean.attributes[attributeName].value = 9.5;
   test.assertEquals(9.5, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(9.5, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'DoublePrim';
   bean.attributes[attributeName].value = 17.5;
   test.assertEquals(17.5, bean.attributes[attributeName].value);
   jmx.save(bean);
   test.assertEquals(17.5, bean.attributes[attributeName].value);
   
   
   
   // Java Objects
   var attributeName = 'Boolean';
   bean.attributes[attributeName].value = true;
   test.assertEquals(true, bean.attributes[attributeName].value);
   jmx.save(bean);
   test.assertEquals(true, bean.attributes[attributeName].value);
   
   var attributeName = 'Char';
   bean.attributes[attributeName].value = 'x';
   test.assertEquals('x', bean.attributes[attributeName].value + ''); // Appending '' to make types the same
   jmx.save(bean);
   test.assertEquals('x', bean.attributes[attributeName].value + '');
   
   var attributeName = 'Byte';
   bean.attributes[attributeName].value = 44;
   test.assertEquals(44, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(44, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'Int';
   bean.attributes[attributeName].value = 22;
   test.assertEquals(22, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(22, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'Long';
   bean.attributes[attributeName].value = 1234567;
   test.assertEquals(1234567, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(1234567, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'Float';
   bean.attributes[attributeName].value = 9.5;
   test.assertEquals(9.5, bean.attributes[attributeName].value * 1.0);
   jmx.save(bean);
   test.assertEquals(9.5, bean.attributes[attributeName].value * 1.0);
   
   var attributeName = 'Double';
   bean.attributes[attributeName].value = 17.5;
   test.assertEquals(17.5, bean.attributes[attributeName].value);
   jmx.save(bean);
   test.assertEquals(17.5, bean.attributes[attributeName].value);
   
   var attributeName = 'String';
   bean.attributes[attributeName].value = 'updated';
   test.assertEquals('updated', bean.attributes[attributeName].value);
   jmx.save(bean);
   test.assertEquals('updated', bean.attributes[attributeName].value);
   
   // String Array attributes are not converted to JavaScript arrays, so we must use the Java array type.
   // We'll read the existing attribute value - which should be a Java array - and change its element values.
   var attributeName = 'StringArray';
   var stringArrayValue = bean.attributes[attributeName].value;
   test.assertEquals(2, stringArrayValue.length);
   stringArrayValue[0] = 'updated';
   stringArrayValue[1] = 'value';
   // Now write the updated value
   bean.attributes[attributeName].value = stringArrayValue;
   
   assertArrayEquals(bean.attributes[attributeName].value, ['updated', 'value']);
   jmx.save(bean);
   assertArrayEquals(bean.attributes[attributeName].value, ['updated', 'value']);
   
   
   var attributeName = 'StringList';
   bean.attributes[attributeName].value = ['updated', 'list'];
   
   assertArrayEquals(bean.attributes[attributeName].value, ['updated', 'list']);
   jmx.save(bean);
   assertArrayEquals(bean.attributes[attributeName].value, ['updated', 'list']);
   
   
   // Note the NativeDate doesn't implement equals(Object), so we must pass the time field through to Java for comparison
   var attributeName = 'Date';
   bean.attributes[attributeName].value = new Date(1999, 10, 4);
   test.assertEquals(new Date(1999, 10, 4).time, bean.attributes[attributeName].value.time);
   jmx.save(bean);
   test.assertEquals(new Date(1999, 10, 4).time, bean.attributes[attributeName].value.time);
}

function testWriteNullAttributeValue()
{
   // I'm using an MBean that I've defined in test config (spring bean name = "TestJmx") so that I can control the Java types of the attributes.
   var beanName = 'Alfresco:Name=TestJmx';
   var bean = getSingleNamedBean(beanName);
   
   // There shouldn't be any problem in writing a null-valued String
   var attributeName = 'String';
   bean.attributes[attributeName].value = null;
   test.assertNull(bean.attributes[attributeName].value, 'String attribute should have been null.');
   jmx.save(bean);
   test.assertNull(bean.attributes[attributeName].value, 'String attribute should have been null.');
   
   // We'll also write a null value for a primitive as it has a particular mapping between Java & JavaScript
   var attributeName = 'Double';
   bean.attributes[attributeName].value = null;
   test.assertNull(bean.attributes[attributeName].value, 'Double attribute should have been null.');
   jmx.save(bean);
   test.assertNull(bean.attributes[attributeName].value, 'Double attribute should have been null.');
}



// Execute tests
testWriteAttributeValuesOfVariousTypes();
testWriteNullAttributeValue();
