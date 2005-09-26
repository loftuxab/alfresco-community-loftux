/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule.ruletrigger;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.namespace.QName;

/**
 * We use this specialised trigger for create node beaucse of a problem with the CIFS integration.
 * <p>
 * The create node trigger will only be fired if the object is NOT a sub-type of content.
 * 
 * @author Roy Wetherall
 */
public class CreateNodeRuleTrigger extends SingleChildAssocRefPolicyRuleTrigger
{
    DictionaryService dictionaryService;
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void policyBehaviour(ChildAssociationRef childAssocRef)
    {
        QName type = this.nodeService.getType(childAssocRef.getChildRef());
        if (this.dictionaryService.isSubClass(type, ContentModel.TYPE_CONTENT) == false)
        {
            triggerRules(childAssocRef.getParentRef(), childAssocRef.getChildRef());
        }
    }
}
