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
   var operations = bean.operations;
   test.assertNotNull(operations,          'operations was null');
   test.assertNotNull(operations.metadata, 'operations.metadata was null');
   
   // There should be 5 methods on this bean
   test.assertEquals(5, operations.metadata.length);
   
   // We can just pluck them from the JavaScript array.
   test.assertEquals('overloadedMethodVariesByParamCount', operations.metadata[0].name);
   test.assertEquals('java.lang.String', operations.metadata[0].returnType);
   test.assertEquals(0, operations.metadata[0].parameters.length);
   
   test.assertEquals('overloadedMethodVariesByParamCount', operations.metadata[1].name);
   test.assertEquals('java.lang.String', operations.metadata[1].returnType);
   test.assertEquals(1, operations.metadata[1].parameters.length);
   test.assertEquals('java.lang.String', operations.metadata[1].parameters[0].type);
   
   test.assertEquals('overloadedMethodVariesByParamCount', operations.metadata[2].name);
   test.assertEquals('java.lang.String', operations.metadata[2].returnType);
   test.assertEquals(2, operations.metadata[2].parameters.length);
   test.assertEquals('java.lang.String', operations.metadata[2].parameters[0].type);
   test.assertEquals('java.lang.String', operations.metadata[2].parameters[1].type);
   
   
   test.assertEquals('overloadedMethodVariesByParamType', operations.metadata[3].name);
   test.assertEquals('java.lang.String', operations.metadata[3].returnType);
   test.assertEquals(1, operations.metadata[3].parameters.length);
   test.assertEquals('int', operations.metadata[3].parameters[0].type);
   
   test.assertEquals('overloadedMethodVariesByParamType', operations.metadata[4].name);
   test.assertEquals('java.lang.String', operations.metadata[4].returnType);
   test.assertEquals(1, operations.metadata[4].parameters.length);
   test.assertEquals('java.lang.String', operations.metadata[4].parameters[0].type);
}

function testInvokeUnambiguousOperations()
{
   test.assertEquals('void',        bean.operations.overloadedMethodVariesByParamCount());
   test.assertEquals('hello',       bean.operations.overloadedMethodVariesByParamCount('hello'));
   test.assertEquals('hello,world', bean.operations.overloadedMethodVariesByParamCount('hello', 'world'));
}

function testInvokeUnambiguousOperationsWithTypeInfo()
{
   test.assertEquals('void',        bean.operations.overloadedMethodVariesByParamCount_([]));
   test.assertEquals('hello',       bean.operations.overloadedMethodVariesByParamCount_(['java.lang.String'], 'hello'));
   test.assertEquals('hello,world', bean.operations.overloadedMethodVariesByParamCount_(['java.lang.String', 'java.lang.String'], 'hello', 'world'));
}

function testInvokeAmbiguousOperations()
{
   // These operations should not be available for invocation in this form
   try
   {
      bean.operations.overloadedMethodVariesByParamType('hello');
   }
   catch (exception)
   {
      test.assertContains('' + exception, 'Cannot invoke operation \'overloadedMethodVariesByParamType\'. Please use type-aware', '' + exception);
   }
   
   try
   {
      bean.operations.overloadedMethodVariesByParamType(42);
   }
   catch (exception)
   {
      test.assertContains('' + exception, 'Cannot invoke operation \'overloadedMethodVariesByParamType\'. Please use type-aware', '' + exception);
   }
}

function testInvokeAmbiguousOperationsWithTypeInfo()
{
   test.assertEquals('hello', bean.operations.overloadedMethodVariesByParamType_(['java.lang.String'], 'hello'));
   test.assertEquals('int: 42', bean.operations.overloadedMethodVariesByParamType_(['int'], 42));
}



// Get the bean & the attribute provided by the Java @Test method.
var bean = getSingleNamedBean('Alfresco:Name=TestOverloadedJmx');
test.assertNotNull(bean, 'bean was null');

// Execute tests
testGetOperationMetadata();
testInvokeUnambiguousOperations();
testInvokeUnambiguousOperationsWithTypeInfo();
testInvokeAmbiguousOperations();
testInvokeAmbiguousOperationsWithTypeInfo();
