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
package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Mail action executor implementation.
 * 
 * @author Roy Wetherall
 */
public class MailActionExecutor extends RuleActionExecutorAbstractBase 
{
    private static Logger logger = Logger.getLogger(MailActionExecutor.class);
    
	/**
	 * Action executor constants
	 */
	public static final String NAME = "mail";
    public static final String PARAM_TO = "to";
    public static final String PARAM_SUBJECT = "subject";
    public static final String PARAM_TEXT = "text";
    
    /**
     * From address
     */
    public static final String FROM_ADDRESS = "alfresco_repository@alfresco.org";
	
	/**
	 * The java mail sender
	 */
	private JavaMailSender javaMailSender;
	
	/**
	 * Set the java mail sender
	 * 
	 * @param javaMailSender  the java mail sender
	 */
	public void setMailService(JavaMailSender javaMailSender) 
	{
		this.javaMailSender = javaMailSender;
	}
	
    /**
     * Execute the rule action
     */
	@Override
	protected void executeImpl(
			RuleAction ruleAction,
			NodeRef actionableNodeRef, NodeRef actionedUponNodeRef) 
	{
        // Create the simple mail message
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo((String)ruleAction.getParameterValue(PARAM_TO));
		simpleMailMessage.setSubject((String)ruleAction.getParameterValue(PARAM_SUBJECT));
		simpleMailMessage.setText((String)ruleAction.getParameterValue(PARAM_TEXT));
        simpleMailMessage.setFrom(FROM_ADDRESS);
			
        try
        {
           // Send the message
           javaMailSender.send(simpleMailMessage);
        }
        catch (Throwable e)
        {
           // don't stop the action but let admins know email is not getting sent
           logger.error("Failed to send email to " + (String)ruleAction.getParameterValue(PARAM_TO), e);
        }
	}

    /**
     * Add the parameter definitions
     */
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_TO, ParameterType.STRING, true, getParamDisplayLabel(PARAM_TO)));
        paramList.add(new ParameterDefinitionImpl(PARAM_SUBJECT, ParameterType.STRING, true, getParamDisplayLabel(PARAM_SUBJECT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_TEXT, ParameterType.STRING, true, getParamDisplayLabel(PARAM_TEXT)));
	}

}
