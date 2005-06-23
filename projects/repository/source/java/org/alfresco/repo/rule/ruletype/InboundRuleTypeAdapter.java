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

import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
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
     * @see org.alfresco.repo.rule.ruletype.RuleTypeAdapter#registerPolicyBehaviour()
     */
    public void registerPolicyBehaviour()
    {
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));
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

	@Override
	protected String getDisplayLabel() 
	{
		return this.properties.getProperty(DISPLAY_LABEL);
	}
}
