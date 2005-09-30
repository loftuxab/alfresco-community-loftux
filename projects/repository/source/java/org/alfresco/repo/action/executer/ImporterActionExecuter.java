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
package org.alfresco.repo.action.executer;

import java.util.List;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.ImporterService;

/**
 * Importer action executor
 * 
 * @author Roy Wetherall
 */
public class ImporterActionExecuter extends ActionExecuterAbstractBase
{
    public static final String NAME = "import";
    public static final String PARAM_ENCODING = "encoding";
    public static final String PARAM_DESTINATION_FOLDER = "destination";
    
    /**
     * The importer service
     */
    private ImporterService importerService;
	
    /**
     * Sets the ImporterService to use
     * 
     * @param importerService The ImporterService
     */
	public void setImporterService(ImporterService importerService) 
	{
		this.importerService = importerService;
	}

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef)
    {
        // TODO: execute the Importer 
    }

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		//paramList.add(new ParameterDefinitionImpl(PARAM_DESCRIPTION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_DESCRIPTION)));
	}

}
