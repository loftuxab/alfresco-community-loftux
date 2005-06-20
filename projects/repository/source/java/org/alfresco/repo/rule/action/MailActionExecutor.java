package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Mail action executor implementation.
 * 
 * @author Roy Wetherall
 */
public class MailActionExecutor extends RuleActionExecutorAbstractBase 
{
	/**
	 * Action executor constants
	 */
	public static final String NAME = "mail";
    public static final String PARAM_TO = "to";
    public static final String PARAM_SUBJECT = "subject";
    public static final String PARAM_TEXT = "text";
	
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
			
        // Send the message
		javaMailSender.send(simpleMailMessage);
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
