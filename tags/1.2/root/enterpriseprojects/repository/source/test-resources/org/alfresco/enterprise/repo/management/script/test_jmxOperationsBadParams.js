<import resource="classpath:org/alfresco/enterprise/repo/management/script/jmxTestUtilFunctions.js">

/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
function testInvokeOperations()
{
   // Get the bean & the attribute provided by the Java @Test method.
   var bean = getSingleNamedBean('Alfresco:Name=TestJmx');
   test.assertNotNull(bean, 'bean was null');
   
   // Invoke an operation and give it more parameters than the Java MBean expects.
   // The same exception would be thrown if we invoke an operation with too few parameters.
   bean.operations.noParams('this param should not be here.');
}

// Execute tests
testInvokeOperations();
