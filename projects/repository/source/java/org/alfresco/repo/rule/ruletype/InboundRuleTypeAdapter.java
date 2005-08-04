/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule.ruletype;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Inbound rule type adapter class.
 * 
 * @author Roy Wetherall
 */
public class InboundRuleTypeAdapter extends RuleTypeAdapterAbstractBase
{
	public static final String NAME = "inbound";
	private static final String DISPLAY_LABEL = "inbound.display-label";
	
    /**
     * @see org.alfresco.repo.action.rule.ruletype.RuleTypeAdapter#registerPolicyBehaviour()
     */
    public void registerPolicyBehaviour()
    {
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));    	
    	this.policyComponent.bindClassBehaviour(
    			QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
    			this,
    			new JavaBehaviour(this, "onUpdateProperties"));
    }

	/**
	 * onCreateChildAssociation policy behaviour
	 * 
	 * @param childAssocRef		the child association reference created
	 */
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
        executeRules(childAssocRef.getParentRef(), childAssocRef.getChildRef());
    }
    
    /**
     * onUpdateProperties policy behaviour
     * 
     * @param nodeRef	the node reference
     * @param before	the values of the properties before they where changed
     * @param after 	the values of the properties after they where changed
     */
    public void onUpdateProperties(
    		NodeRef nodeRef, 
    		Map<QName, Serializable> before, 
    		Map<QName, Serializable> after)
    {
    	// Check that the contentUrl property is present and that it has been set for the first time
    	if (after.containsKey(ContentModel.PROP_CONTENT_URL) == true &&
    		before.get(ContentModel.PROP_CONTENT_URL) == null &&
    		after.get(ContentModel.PROP_CONTENT_URL) != null)
    	{
    		// Execute the rules of the parent of the node (if they are present)
    		executeParentRules(nodeRef);
    	}
    }

	@Override
	protected String getDisplayLabel() 
	{
		return this.properties.getProperty(DISPLAY_LABEL);
	}
}
