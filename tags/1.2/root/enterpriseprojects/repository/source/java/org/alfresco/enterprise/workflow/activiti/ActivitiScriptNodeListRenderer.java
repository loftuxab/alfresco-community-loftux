/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import org.activiti.explorer.ui.variable.VariableRenderer;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Class capable of showing human-readable representation of a
 * {@link ActivitiScriptNodeList} in the Activiti admin UI.
 * 
 * @author Frederik Heremans
 * @since 4.0
 */
public class ActivitiScriptNodeListRenderer implements VariableRenderer {

	@Override
	public String getStringRepresentation(Object scriptNode) {
		if (scriptNode instanceof ActivitiScriptNodeList) {
			// Concatenate all node paths
			StringBuffer result = new StringBuffer();
			for (NodeRef node : ((ActivitiScriptNodeList) scriptNode)
					.getNodeReferences()) {
				if(result.length() > 0) {
					result.append(", ");
				}
				result.append(node.toString());
			}

			return result.toString();
		} else {
			throw new IllegalArgumentException(
					"Invalid type of variable passed: "
							+ scriptNode.getClass().getName()
							+ ", while expecting "
							+ ActivitiScriptNodeList.class.getName());
		}
	}

	@Override
	public Class<?> getVariableType() {
		return ActivitiScriptNodeList.class;
	}

}
