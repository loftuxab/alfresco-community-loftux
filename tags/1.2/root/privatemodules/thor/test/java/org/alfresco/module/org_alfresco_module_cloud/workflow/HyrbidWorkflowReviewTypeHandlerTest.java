package org.alfresco.module.org_alfresco_module_cloud.workflow;

import java.io.Serializable;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.NamespaceServiceMemoryImpl;
import org.alfresco.service.namespace.QName;
import org.mockito.Mockito;

/**
 * Test for {@link ReviewHybridWorkflowHandler}.
 * @author Frederik Heremans
 *
 */
public class HyrbidWorkflowReviewTypeHandlerTest extends TestCase {

	/**
	 * WOR-153, WOR-134
	 */
	public void testGetResultsAndValidate() throws Exception {
		// In-mem namespace-service
		NamespaceServiceMemoryImpl prefixResolver = new NamespaceServiceMemoryImpl();
		prefixResolver.registerNamespace(NamespaceService.BPM_MODEL_PREFIX, NamespaceService.BPM_MODEL_1_0_URI);
		prefixResolver.registerNamespace(NamespaceService.WORKFLOW_MODEL_PREFIX, NamespaceService.WORKFLOW_MODEL_1_0_URI);
		prefixResolver.registerNamespace(HybridWorkflowModel.HYBRID_WORKFLOW_MODEL_PREFIX, HybridWorkflowModel.HYBRID_WORKFLOW_MODEL_URI);
		
		// Mock service-registry used to fetch namespace-service
		ServiceRegistry registry = Mockito.mock(ServiceRegistry.class);
		Mockito.when(registry.getNamespaceService()).thenReturn(prefixResolver);
		
		// Fill the execution with variables that are expected after review is completed
		DummyDelegateExecution delegateExecution = new DummyDelegateExecution();
		delegateExecution.setVariable("wf_requiredApprovePercent", 70);
		delegateExecution.setVariable("wf_actualPercent", 75);
		
		// Get the properties to set on the content
		ReviewHybridWorkflowHandler handler = new ReviewHybridWorkflowHandler();
		handler.setServiceRegistry(registry);
		Map<QName, Serializable> propertiesToSet = handler.getResultsAndValidate(delegateExecution);
		
		// Validate that all required properties are present
		assertEquals(70, propertiesToSet.get(HybridWorkflowModel.PROP_REQUIRED_APPROVAL_PERCENTAGE));
		assertEquals(75, propertiesToSet.get(HybridWorkflowModel.PROP_ACTUAL_APPROVAL_PERCENTAGE));
		assertEquals("Approve", propertiesToSet.get(HybridWorkflowModel.PROP_RESULT));
	}
}
