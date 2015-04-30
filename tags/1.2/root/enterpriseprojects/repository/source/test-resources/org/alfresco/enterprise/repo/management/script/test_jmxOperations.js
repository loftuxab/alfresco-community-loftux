<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function testGetOperationMetadata()
{
   // Get the bean & the attribute provided by the Java @Test method.
   var bean = getSingleNamedBean('Alfresco:Name=TestJmx');
   test.assertNotNull(bean, 'bean was null');
   
   var operations = bean.operations;
   test.assertNotNull(operations,          'operations was null');
   test.assertNotNull(operations.metadata, 'operations.metadata was null');
   
   test.assertEquals(6, operations.metadata.length);
   
   // String fetchStringArray()
   test.assertEquals('fetchStringArray', operations.metadata[0].name);
   test.assertEquals('[Ljava.lang.String;', operations.metadata[0].returnType);
   test.assertEquals(0, operations.metadata[0].parameters.length);
   
   // void multipleParams(String, int)
   test.assertEquals('multipleParams', operations.metadata[1].name);
   test.assertEquals('void', operations.metadata[1].returnType);
   test.assertEquals(2, operations.metadata[1].parameters.length);
   test.assertEquals('java.lang.String', operations.metadata[1].parameters[0].type);
   test.assertEquals('int', operations.metadata[1].parameters[1].type);
   
   // void noParams()
   test.assertEquals('noParams', operations.metadata[2].name);
   test.assertEquals('void', operations.metadata[2].returnType);
   test.assertEquals(0, operations.metadata[2].parameters.length);
   
   // void pushString(String)
   test.assertEquals('pushString', operations.metadata[3].name);
   test.assertEquals('void', operations.metadata[3].returnType);
   test.assertEquals(1, operations.metadata[3].parameters.length);
   test.assertEquals('java.lang.String', operations.metadata[3].parameters[0].type);
   
   // String reverseString(String)
   test.assertEquals('reverseString', operations.metadata[4].name);
   test.assertEquals('java.lang.String', operations.metadata[4].returnType);
   test.assertEquals(1, operations.metadata[4].parameters.length);
   test.assertEquals('java.lang.String', operations.metadata[4].parameters[0].type);
   
   // String throwException()
   test.assertEquals('throwException', operations.metadata[5].name);
   test.assertEquals('void', operations.metadata[5].returnType);
   test.assertEquals(0, operations.metadata[5].parameters.length);
}

function testInvokeOperations()
{
   // Get the bean & the attribute provided by the Java @Test method.
   var bean = getSingleNamedBean('Alfresco:Name=TestJmx');
   test.assertNotNull(bean, 'bean was null');
   
   // Now invoke the most simple operation - no parameters, no return type.
   // If no exceptions are thrown, then we'll assume all is well.
   bean.operations.noParams();
   
   // Next try passing a simple parameter in.
   bean.operations.pushString('parameter pushed');
   
   // Next try passing in and taking out parameters
   test.assertEquals('olleh', bean.operations.reverseString('hello'));
   
   // Next try passing in multiple parameters.
   bean.operations.multipleParams('hello', 42);
   
   
   // Some function calls with null parameters
   bean.operations.pushString(null);
   
   bean.operations.multipleParams(null, 42);
   
   // Ensure nothing untoward happens when a JMX operation throws an exception.
   var exceptionCaught = false;
   try
   {
      bean.operations.throwException();
   }
   catch(alfrescoRuntimeException)
   {
      exceptionCaught = true;
   }
   test.assertTrue(exceptionCaught, 'Expected exception was not caught.');
}

// Execute tests
testGetOperationMetadata();
testInvokeOperations();
