/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.registration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.acegisecurity.providers.encoding.PasswordEncoder;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.accounts.exceptions.AccountNotFoundException;
import org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts.AccountSignupPost;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.person.PersonReplicationComponent;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.authentication.TicketComponent;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.view.ImportPackageHandler;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterContentCache;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO9075;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Implementation of Cloud Registration Service
 * 
 * @since Alfresco Cloud Module
 */
public class RegistrationServiceImpl implements RegistrationService, OnCreateNodePolicy, OnCreateDocumentLibraryPolicy
{
    private static final Log logger = LogFactory.getLog(RegistrationServiceImpl.class);

    private static final String DOC_LIBRARY = "documentLibrary";
    private static final int GUID_LENGTH = 36;

    // CLOUD-1159 - special case (new user login direct to profile page)
    public final static String SAML_DIRECT_SIGNUP = "samlDirectSignup";

    /*package*/ final static String GROUP_INTERNAL_USERS = "GROUP_INTERNAL_USERS"; 
    /*package*/ final static String GROUP_NETWORK_ADMINS = "GROUP_NETWORK_ADMINS";
    
    private EmailAddressService emailAddressService;
    private DirectoryService directoryService;
    private AccountService accountService;
    private CloudPersonService cloudPersonService;
    private AuthorityService authorityService;
    private WorkflowService workflowService;
    private PersonReplicationComponent personReplicationComponent;
    private SiteService siteService;
    private ImporterService importerService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private HomeSiteSurfConfig homeSiteSurfConfig;
    private ActionService actionService;
    private SAMLConfigAdminService samlConfigAdminService;
    private TicketComponent ticketComponent;
    
    private String timerRemind3 = "P3D";
    private String timerRemind7 = "R3/P7D";
    private String timerEnd = "P28D";
    
    private PolicyComponent policyComponent;
    public PasswordEncoder passwordEncoder;
    
    private ClassPolicyDelegate<OnCreateDocumentLibraryPolicy> onCreateDocumentLibraryDelegate;

    
	public void setPasswordEncoder(PasswordEncoder passwordEncoder)
	{
		this.passwordEncoder = passwordEncoder;
	}

    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

	public void setEmailAddressService(EmailAddressService service)
    {
        this.emailAddressService = service;
    }

    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }

    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public void setCloudPersonService(CloudPersonService service)
    {
        this.cloudPersonService = service;
    }
    
    public void setAuthorityService(AuthorityService service)
    {
        this.authorityService = service;
    }

    public void setWorkflowService(WorkflowService service)
    {
        this.workflowService = service;
    }
    
    public void setPersonReplicationComponent(PersonReplicationComponent service)
    {
        this.personReplicationComponent = service;
    }
    
    public void setSiteService(SiteService service)
    {
        this.siteService = service;
    }
    
    public void setImporterService(ImporterService service)
    {
        this.importerService = service;
    }
    
    public void setNamespaceService(NamespaceService service)
    {
        this.namespaceService = service;
    }
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    
    public void setActionService(ActionService service)
    {
        this.actionService = service;
    }
    
    public void setHomeSiteSurfConfig(HomeSiteSurfConfig service)
    {
        this.homeSiteSurfConfig = service;
    }
    
    public void setTimerRemind3(String timerRemind3)
    {
        this.timerRemind3 = timerRemind3;
    }
    
    public void setTimerRemind7(String timerRemind7)
    {
        this.timerRemind7 = timerRemind7;
    }
    
    public void setTimerEnd(String timerEnd)
    {
        this.timerEnd = timerEnd;
    }
    
    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }
    
    public void setTicketComponent(TicketComponent ticketComponent)
    {
        this.ticketComponent = ticketComponent;
    }
    
    public void init()
    {
        onCreateDocumentLibraryDelegate = policyComponent.registerClassPolicy(OnCreateDocumentLibraryPolicy.class);
        
        this.policyComponent.bindClassBehaviour(OnCreateNodePolicy.QNAME,
                ContentModel.TYPE_FOLDER,
                new JavaBehaviour(this, "onCreateNode"));
        
        this.policyComponent.bindClassBehaviour(OnCreateDocumentLibraryPolicy.QNAME,
                ContentModel.TYPE_FOLDER,
                new JavaBehaviour(this, "onCreateDocumentLibrary", NotificationFrequency.TRANSACTION_COMMIT));
    }
    
	private String hashPassword(String password)
    {
    	try
    	{
            String salt = null;
    		return passwordEncoder.encodePassword(password, salt);
    	}
    	catch(DataAccessException e)
    	{
    		throw new AlfrescoRuntimeException("", e);
    	}
    }

	@Override
    public Registration registerEmail(String email, String source, String sourceUrl, String message) throws InvalidEmailAddressException
    {
        Map<String, Serializable> emptyMap = Collections.emptyMap();
        return registerEmail(email, null, null, null, source, sourceUrl, message, emptyMap);
    }

    @Override
    public List<InvalidEmail> registerEmails(List<String> emails, String source, String sourceUrl, String message,
                                             Map<String, Serializable> optionalAnalyticData) throws InvalidEmailAddressException
    {
        return registerEmails(emails, source, sourceUrl, message, optionalAnalyticData, true);
    }

    @Override
    public List<InvalidEmail> registerEmails(List<String> emails, String source, String sourceUrl, String message,
            Map<String, Serializable> optionalAnalyticData, boolean checkSameDomain) throws InvalidEmailAddressException
    {
        String currentDomain = TenantUtil.getCurrentDomain();
        List<String> validEmails = new ArrayList<String>();
        List<InvalidEmail> ret = new ArrayList<InvalidEmail>();

        // remove invalid emails
        for (String email : emails)
        {
            if (!emailAddressService.isAcceptedAddress(email) || (checkSameDomain && !emailAddressService.sameDomain(currentDomain, email)))
            {
                ret.add(new InvalidEmail(email, INVALID_EMAIL_TYPE.INCORRECT_DOMAIN));
            }
            else
            {
            	email = emailAddressService.getAddress(email);
                if (directoryService.userExists(email))
                {
                    ret.add(new InvalidEmail(email, INVALID_EMAIL_TYPE.USER_EXISTS));
                }
                else
                {
                    validEmails.add(email);
                }
            }
        }

        // register each valid email
        for (String email : validEmails)
        {
            registerEmail(email, null, null, null, source, sourceUrl, message, optionalAnalyticData, AuthenticationUtil.getFullyAuthenticatedUser());
        }

        return ret;
    }

    @Override
    public Registration registerEmail(String email, String firstName, String lastName, String password, String source, String sourceUrl, String message, Map<String, Serializable> optionalAnalyticData) throws InvalidEmailAddressException
    {
        return registerEmail(email, firstName, lastName, password, source, sourceUrl, message, optionalAnalyticData, null);
    }

    @Override
    public Registration registerEmail(String email, String firstName, String lastName, String password, String source, String sourceUrl, String message, Map<String, Serializable> optionalAnalyticData, final String initiatorEmailAddress) throws InvalidEmailAddressException
    {
        long start = System.currentTimeMillis();
        
        // validate email address
        if (!emailAddressService.isAcceptedAddress(email))
        {
            throw new InvalidEmailAddressException("Email address " + email + " is not a valid address");
        }

        email = emailAddressService.getAddress(email);
        email = email.toLowerCase();
        String domain = emailAddressService.getDomain(email);
        DomainValidityCheck check = emailAddressService.validateDomain(domain);
        FailureReason reason = check.getFailureReason();
        if (reason != null)
        {
            throw new InvalidEmailAddressException("Email domain " + domain + " is not accepted: " + reason);
        }
        // If the email address has already been registered (which by definition also means it is not yet activated)
        // then we do not want to start a new workflow. We want to reuse the existing workflow instance and send
        // a reminder email to the user asking them to activate their account
        
        // To do that, we need to get the id and key for this signup
        List<WorkflowTask> tasks = getRegistrations(email);
        if (tasks.size() > 0)
        {
            Map<QName, Serializable> taskProperties = tasks.get(0).getProperties();
            final String id = tasks.get(0).getPath().getInstance().getId();
            final String key = (String) taskProperties.get(WorkflowModelSelfSignup.WF_PROP_KEY);
            
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Reusing existing registration process <")
                   .append(id).append(',').append(key).append("> for email ").append(email);
                logger.debug(msg.toString());
            }
            
            // No registration analytics required here as the user has already registered.
            Registration reg = resendActivationRequest(id, key);
            return reg;
        }
        
        // But otherwise we must start a new workflow instance.
        
        // Get the (latest) workflow definition for self-signup.
        WorkflowDefinition signUpDefinition = workflowService.getDefinitionByName(WorkflowModelSelfSignup.WORKFLOW_DEFINITION_NAME);
        
        // create workflow properties
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, I18NUtil.getMessage("suwf_signup.selfsignup.workflow.description"));
        props.put(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS, email);
        props.put(WorkflowModelSelfSignup.WF_PROP_MESSAGE, message);
        String guid = GUID.generate();
        props.put(WorkflowModelSelfSignup.WF_PROP_KEY, guid);
        props.put(WorkflowModelSelfSignup.WF_PROP_TIMER_REMIND3, timerRemind3);
        props.put(WorkflowModelSelfSignup.WF_PROP_TIMER_REMIND7, timerRemind7);
        props.put(WorkflowModelSelfSignup.WF_PROP_TIMER_END, timerEnd);

        boolean isPreRegistered = isPreRegistered(email, firstName, lastName, password);
        props.put(WorkflowModelSelfSignup.WF_PROP_IS_PREREGISTERED, Boolean.valueOf(isPreRegistered));
        if(isPreRegistered)
        {
            props.put(WorkflowModelSelfSignup.WF_PROP_FIRST_NAME, firstName);
            props.put(WorkflowModelSelfSignup.WF_PROP_LAST_NAME, lastName);
        	props.put(WorkflowModelSelfSignup.WF_PROP_PASSWORD, hashPassword(password));
        }

        if (initiatorEmailAddress != null)
        {
            // Get profile information from the inviter person object. To do this, we must switch to a tenant which contains the person object for that user.
            // The person object for that user will be present in any tenant where the user is present, but we'll go to the user's home tenant as the person will always be there.
            Account initiatorHomeAccount = getHomeAccount(initiatorEmailAddress);
            String initiatorTenantId = initiatorHomeAccount.getTenantId();

            final Map<QName, Serializable> personProps = TenantUtil.runAsUserTenant(new TenantRunAsWork<Map<QName, Serializable>>()
            {
                @Override
                public Map<QName, Serializable> doWork() throws Exception
                {
                    NodeRef person = cloudPersonService.getPerson(initiatorEmailAddress, false);
                    return nodeService.getProperties(person);
                }
            }, initiatorEmailAddress, initiatorTenantId);

            // Populate workflow with initiator properties
            props.put(WorkflowModelSelfSignup.WF_PROP_INITIATOR_EMAIL_ADDRESS, initiatorEmailAddress);
            props.put(WorkflowModelSelfSignup.WF_PROP_INITIATOR_FIRST_NAME, personProps.get(ContentModel.PROP_FIRSTNAME));
            props.put(WorkflowModelSelfSignup.WF_PROP_INITIATOR_LAST_NAME, personProps.get(ContentModel.PROP_LASTNAME));
        }
        
        if ((source != null) && (source.equals(SAML_DIRECT_SIGNUP)))
        {
            // CLOUD-1159 - special case (new user login direct to profile page)
            props.put(WorkflowModelSelfSignup.WF_PROP_SAML_DIRECT_SIGNUP, true);
        }
        
        // start the workflow
        Registration registration;
        WorkflowPath path = workflowService.startWorkflow(signUpDefinition.getId(), props);
        if (path.isActive())
        {
            WorkflowTask startTask = workflowService.getStartTask(path.getInstance().getId());
            workflowService.endTask(startTask.getId(), null);
            registration = new RegistrationImpl(email, path.getInstance().getStartDate(), path.getInstance().getId(), guid);
        }
        else
        {
            registration = new RegistrationImpl(email, null, null, null);
        }
        
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Registered user ").append(email).append(" with ").append(path.getInstance().getId()).append(',').append(guid);
            logger.debug(msg.toString());
        }
        
        recordRegistrationAnalyticsEvent(email, source, sourceUrl, optionalAnalyticData);
        
        if (logger.isInfoEnabled())
        {
            logger.info("registerEmail: "+email+" in "+(System.currentTimeMillis()-start)+" ms");
        }
        
        return registration;
    }
    
    @Override
    public Registration getRegistration(String email)
    {
        List<WorkflowTask> tasks = getRegistrations(email);
        if (tasks.size() == 0)
        {
            return null;
        }

        Map<QName, Serializable> taskProperties = tasks.get(0).getProperties();
        WorkflowInstance instance = tasks.get(0).getPath().getInstance();
        String key = (String) taskProperties.get(WorkflowModelSelfSignup.WF_PROP_KEY);
        if (taskProperties.get(WorkflowModelSelfSignup.WF_PROP_INITIATOR_EMAIL_ADDRESS) != null)
        {
            return new RegistrationImpl(email, instance.getStartDate(), instance.getId(), key,
                    (String) taskProperties.get(WorkflowModelSelfSignup.WF_PROP_INITIATOR_EMAIL_ADDRESS),
                    (String) taskProperties.get(WorkflowModelSelfSignup.WF_PROP_INITIATOR_FIRST_NAME),
                    (String) taskProperties.get(WorkflowModelSelfSignup.WF_PROP_INITIATOR_LAST_NAME));
        }
        else
        {
            return new RegistrationImpl(email, instance.getStartDate(), instance.getId(), key);
        }
    }
    
    private void recordRegistrationAnalyticsEvent(String email, String source, String sourceUrl, Map<String, Serializable> optionalAnalyticData)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Recording registration analytics event for user " + email);
        }
        
        String ipAddress = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_IP_ADDRESS);
        Long landingTime = (Long) optionalAnalyticData.get(AccountSignupPost.PARAM_LANDING_TIME);
        String landingReferrer = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_LANDING_REFERRER);
        String landingPage = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_LANDING_PAGE);
        @SuppressWarnings("unchecked")
        List<String> landingKeywordsList = (List<String>) optionalAnalyticData.get(AccountSignupPost.PARAM_LANDING_KEYWORDS);
        String landingKeywords;
        if (landingKeywordsList == null)
        {
            landingKeywords = "";
        }
        else
        {
            // We want a "key,words,comma-delimited,no,spaces,no,braces" format instead of "[key, words, comma-delimited, spaces, and, braces]"
            StringBuilder sb = new StringBuilder();
            for (Iterator<String> iter = landingKeywordsList.iterator(); iter.hasNext(); )
            {
                sb.append(iter.next());
                if (iter.hasNext())
                {
                    sb.append(',');
                }
            }
            landingKeywords = sb.toString();
        }
        String utmSource = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_UTM_SOURCE);
        String utmMedium = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_UTM_MEDIUM);
        String utmTerm = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_UTM_TERM);
        String utmContent = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_UTM_CONTENT);
        String utmCampaign = (String) optionalAnalyticData.get(AccountSignupPost.PARAM_UTM_CAMPAIGN);
        Analytics.record_Registration(email, source, sourceUrl, ipAddress,
                                      landingTime, landingReferrer, landingPage, landingKeywords,
                                      utmSource, utmMedium, utmTerm, utmContent, utmCampaign);
    }
    
    @Override
    public boolean isRegisteredEmailAddress(String email)
    {
        List<WorkflowTask> tasks = getRegistrations(email);
        return tasks.size() > 0;
    }

    private boolean isPreRegistered(String email, String firstName, String lastName, String password)
    {
    	boolean preRegistered = true;

    	if(email == null || email.equals(""))
    	{
    		preRegistered = false;
    	}

    	if(password == null || password.equals(""))
    	{
    		preRegistered = false;
    	}
    	
    	return preRegistered;
    }

    public boolean isPreRegistered(String id, String key)
    {
        validateRegistrationKeyAndId(id, key);
        
        Map<QName, Serializable> pathProps = workflowService.getPathProperties(id);
        Boolean isPreRegistered = (Boolean) pathProps.get(WorkflowModelSelfSignup.WF_PROP_IS_PREREGISTERED);
        if(isPreRegistered != null)
        {
        	return isPreRegistered.booleanValue();
        }
        else
        {
            return false;
        }
    }

    /**
     * This method queries the workflow service for any in-progress registrations for the given email address.
     * @param email the email address whose in-progress registrations we are looking for.
     * 
     * @return there should either be one or no in-progress registrations for any email address.
     */
    private List<WorkflowTask> getRegistrations(String email)
    {
        // validate email address
        if (!emailAddressService.isAcceptedAddress(email))
        {
            throw new InvalidEmailAddressException("Email address " + email + " is not a valid address");
        }

        WorkflowTaskQuery query = new WorkflowTaskQuery();
        query.setActive(Boolean.TRUE);
        query.setTaskState(WorkflowTaskState.IN_PROGRESS);
        // This is instead of setProcessName(), which was used for jBPM workflows.
        query.setTaskName(WorkflowModelSelfSignup.WF_ACCOUNT_ACTIVATION_PENDING_TASK);
        
        HashMap<QName, Object> props = new HashMap<QName, Object>();
        props.put(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS, email);
        query.setProcessCustomProps(props);
        
        List<WorkflowTask> tasks = workflowService.queryTasks(query);
        return tasks;
    }
    
    @SuppressWarnings("synthetic-access")
    @Override public Registration resendActivationRequest(String id, String key)
    {
        validateRegistrationKeyAndId(id, key);
        
        List<WorkflowTask> tasks = getTaskForWorkflow(id);
        if (tasks == null || tasks.size() == 0)
        {
            throw new RegistrationServiceException("Invalid registration identifier: " + id + ", " + key);
        }
        final WorkflowTask task = tasks.get(0);
        // We can only allow restarts for workflows in the correct state.
        final String taskShortFormQName = WorkflowModelSelfSignup.WF_ACCOUNT_ACTIVATION_PENDING_TASK.toPrefixString(namespaceService);
        if ( !taskShortFormQName.equals(task.getName()))
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot restart registration <").append(id).append(',').append(key).append(">. Task=").append(task.getName());
                logger.debug(msg.toString());
            }
            throw new RegistrationServiceException("Invalid registration identifier: " + id + ", " + key);
        }
        
        Map<QName, Serializable> pathProps = workflowService.getPathProperties(id);
        
        // Send the workflow back to the email-sending
        final Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(WorkflowModelSelfSignup.WF_PROP_ACTIVATION_OUTCOME, WorkflowModelSelfSignup.WF_PROP_ACTIVATION_OUTCOME_RESEND_ACTIVATION_EMAIL);
        
        // End the task using the correct user. Possible that this method is called by a new user, which has
        // no secure-context associated
        String taskOwner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
        if(taskOwner == null) 
        {
            taskOwner = AuthenticationUtil.getSystemUserName();
        }
        
        if(taskOwner.equals(AuthenticationUtil.getRunAsUser())) 
        {
            workflowService.updateTask(task.getId(), props, null, null);
            workflowService.endTask(task.getId(), null);
        }
        else
        {
           AuthenticationUtil.runAs(new RunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    workflowService.updateTask(task.getId(), props, null, null);
                    workflowService.endTask(task.getId(), null);
                    return null;
                }
            }, taskOwner);
        }
        
        String email = (String)pathProps.get(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS);
        return new RegistrationImpl(email, task.getPath().getInstance().getStartDate(), id, key);
    }
    
    private boolean checkPassword(String registeredPassword, String password)
    {
    	return EqualsHelper.nullSafeEquals(registeredPassword, password, false);
    }
    
    private String getHomeSiteShortName(String email)
    {
    	return email.replace('@', '-').replace('.', '-');
    }

    @SuppressWarnings("synthetic-access")
    @Override
    public Registration activateRegistration(String id, String key, String firstName, String lastName, String password)
    {
        long start = System.currentTimeMillis();
        
        validateRegistrationKeyAndId(id, key);
        
        Map<QName, Serializable> pathProps = workflowService.getPathProperties(id);
        String email = (String)pathProps.get(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS);
        Boolean tmp = (Boolean)pathProps.get(WorkflowModelSelfSignup.WF_PROP_IS_PREREGISTERED);
        boolean isPreRegistered = false;
        if(tmp == null)
        {
        	logger.warn("Unexpected null isPreRegistered for " + email);
        }
        else 
        {
        	isPreRegistered = tmp.booleanValue();
        }
        // locate activate tasks
        List<WorkflowTask> tasks = getTaskForWorkflow(id);
        if (tasks == null || tasks.size() == 0)
        {
            throw new RegistrationServiceException("Invalid registration identifier: " + id + ", " + key);
        }
        WorkflowTask task = tasks.get(0);

        // activate user with provided user profile information
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();

        if(isPreRegistered)
        {
            ParameterCheck.mandatoryString("password", password);

        	String registeredPassword = (String)pathProps.get(WorkflowModelSelfSignup.WF_PROP_PASSWORD);
        	// check password matches
        	boolean passwordOk = checkPassword(registeredPassword, hashPassword(password));
        	if(passwordOk)
        	{
        		// given that the password entered for the activation is correct i.e. matches that in the registration
        		// workflow, replace the hashed password with the plaintext password
        		props.put(WorkflowModelSelfSignup.WF_PROP_PASSWORD, password);
        		props.put(WorkflowModelSelfSignup.WF_PROP_ACTIVATION_OUTCOME, WorkflowModelSelfSignup.WF_PROP_ACTIVATION_OUTCOME_PROCEED);
        	}
        	else
        	{
        		throw new UnauthorisedException("Password is incorrect");
        	}
        }
        else
        {
            ParameterCheck.mandatoryString("firstName", firstName);
            ParameterCheck.mandatoryString("lastName", lastName);
            ParameterCheck.mandatoryString("password", password);

	        props.put(WorkflowModelSelfSignup.WF_PROP_FIRST_NAME, firstName);
	        props.put(WorkflowModelSelfSignup.WF_PROP_LAST_NAME, lastName);
	        // Although we're setting the password value into the task here, we will remove that task from
	        // the Activiti History, so it is safe to do so.
	        props.put(WorkflowModelSelfSignup.WF_PROP_PASSWORD, password);
	        props.put(WorkflowModelSelfSignup.WF_PROP_ACTIVATION_OUTCOME, WorkflowModelSelfSignup.WF_PROP_ACTIVATION_OUTCOME_PROCEED);
        }
        
        final String taskId = task.getId();
        workflowService.updateTask(taskId, props, null, null);
        workflowService.endTask(taskId, null);
        
        
        // Remove the previous task from Activiti's history - so that the password will not be in the database.
        // See http://www.activiti.org/userguide/index.html#history for a description of how Activiti stores historical records of
        // processes, tasks and properties.
// BM-0005: temporarily disable deletion of historic task instance (causing deadlocks)
//        if (logger.isDebugEnabled())
//        {
//            logger.debug("Deleting historic task '" + taskId + "'");
//        }
//        final String activitiTaskId = taskId.replace("activiti$", "");
//        activitiHistoryService.deleteHistoricTaskInstance(activitiTaskId);
        
        if (logger.isInfoEnabled())
        {
            logger.info("activateRegistration: "+email+" in "+(System.currentTimeMillis()-start)+" ms");
        }
        
        return new RegistrationImpl(email, task.getPath().getInstance().getStartDate(), id, key);
    }
    
    /**
     * @param workflowId id of the workflow to get tasks from.
     * @return all tasks active in ALL paths in the given process. Use this to get task in process
     * instead of the "worklfowService.getTaskForPath(..)" since this ignored sub-scopes (eg. timer) and
     * can lead to tasks not being returned.
     */
    private List<WorkflowTask> getTaskForWorkflow(String workflowId)
    {
    	WorkflowTaskQuery processTaskQuery = new WorkflowTaskQuery();
    	processTaskQuery.setProcessId(workflowId);
        return workflowService.queryTasks(processTaskQuery, false);
    }
    
    /**
     * This method validates and id and key associated with a particular signup.
     * @param id the workflow id
     * @param key the unique key
     * @throws NoRegistrationWorkflowException if the signup workflow could not be found
     */
    private void validateRegistrationKeyAndId(String id, String key)
    {
        WorkflowInstance workflowInstance = null;
        try
        {
            workflowInstance = workflowService.getWorkflowById(id);
        }
        catch (WorkflowException ignored)
        {
            // Intentionally empty.
        }
        
        if (workflowInstance == null)
        {
            throw new NoRegistrationWorkflowException("Invalid registration identifier: " + id + ", " + key);
        }
        
        String recoveredKey;
        // The mechanism for retrieving the key depends on whether the workflow is active or not.
        if ( workflowInstance.isActive())
        {
            // If the workflow is active we will be able to read the path properties.
            Map<QName, Serializable> pathProps = workflowService.getPathProperties(id);
            
            recoveredKey = (String) pathProps.get(WorkflowModelSelfSignup.WF_PROP_KEY);
        }
        else
        {
            throw new NoRegistrationWorkflowException("Invalid registration identifier: " + id + ", " + key);
        }
        if (recoveredKey == null || !recoveredKey.equals(key))
        {
            throw new NoRegistrationWorkflowException("Invalid registration identifier: " + id + ", " + key);
        }
        
        return;
    }

    @Override
    public void cancelRegistration(String id, String key)
    {
        validateRegistrationKeyAndId(id, key);
        
        // locate activate tasks
        List<WorkflowTask> tasks = getTaskForWorkflow(id);
        if (tasks == null || tasks.size() == 0)
        {
            throw new RegistrationServiceException("Invalid registration identifier: " + id + ", " + key);
        }
        WorkflowTask task = tasks.get(0);
        workflowService.cancelWorkflow(task.getPath().getInstance().getId());
    }
    
    @Override
    public String getEmailSigningUp(final String id, final String key)
    {
        validateRegistrationKeyAndId(id, key);
        
        // We cannot get the email_address from workflowService.getPathProperties as that method will throw an exception
        // for completed workflows.
        WorkflowTask startTask = workflowService.getStartTask(id);
        Map<QName, Serializable> props = startTask.getProperties();
        
        String email = (String) props.get(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS);
        
        return email;
    }
    
    @Override
    public boolean isActivatedEmailAddress(String email)
    {
        return directoryService.userExists(email);
    }

    @Override
    public Account createUser(final String email, final String firstName, final String lastName, final String password)
    {
        long start = System.currentTimeMillis();
        
        // extract domain (to check whether IdP/SAML-enabled and also to locate account)
        final String domain = emailAddressService.getDomain(email);
        
        if ((domain != null) && (! domain.isEmpty()) && samlConfigAdminService.isEnabled(domain))
        {
            // Note: CLOUD-1079 ... new user that does not exist yet - check whether 'internal' ticket has been issued (as per SSO auth response flow)
            if (! ticketComponent.getUsersWithTickets(true).contains(email))
            {
                throw new RegistrationServiceException("User " + email + " cannot be created in a SAML-enabled network, unless pre-authenticated via SSO");
            }
        }
        
        // create user in directory
        final String userId = directoryService.createUser(email, firstName, lastName, password);
        
        Account account = accountService.getAccountByDomain(domain);
        
        final boolean userIsInPublicDomain = emailAddressService.isPublicDomain(domain);
        final int accountType = userIsInPublicDomain ? AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE : AccountType.FREE_NETWORK_ACCOUNT_TYPE;
        
        final boolean accountRequired = (account == null);
        if (accountRequired)
        {
            // there's no account, so create one
            
            // We create public domain tenants as disabled, which prevents logins to those tenants.
            boolean accountEnabled = !userIsInPublicDomain;
            account = accountService.createAccount(domain, accountType, accountEnabled);
        }
        
        // associate home account with user
        directoryService.setHomeAccount(userId, account.getId());
        if ( !userIsInPublicDomain)
        {
            directoryService.setDefaultAccount(email, account.getId());
        }
        else
        {
            // Intentionally empty.
            // Public Email Domain users will have their default account set as part of their initial invitation. see addUser() below
        }
        
        // create person profile
        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            @Override
            public Object doWork() throws Exception
            {
                AuthenticationUtil.runAs(new RunAsWork<Object>()
                {
                    @Override
                    @SuppressWarnings("synthetic-access")
                    public Object doWork() throws Exception
                    {
                        // create person (within tenant) for user
                        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
                        properties.put(ContentModel.PROP_USERNAME, userId);
                        properties.put(ContentModel.PROP_FIRSTNAME, firstName);
                        properties.put(ContentModel.PROP_LASTNAME, lastName);
                        properties.put(ContentModel.PROP_EMAIL, userId);
                        
                        cloudPersonService.createPerson(properties);
                        
                        // THOR-199 - commented-out (can be removed !) - already set by createPerson (via onCreateNode -> setPermissions(personRef, userName, userName))
                        //permissionService.setPermission(person, email, PermissionService.ALL_PERMISSIONS, true);
                        
                        // add user to tenant "internal users" group
                        authorityService.addAuthority(GROUP_INTERNAL_USERS, userId);
                        return null;
                    }
                }, AuthenticationUtil.getSystemUserName());
                
                if ( !userIsInPublicDomain)
                {
                    // create home site (within tenant) - note: ensure creator/owner permissions on surf-config elements elements
                    try
                    {
                        // note: runAs would cause auditable property "creator" to be "admin@xxx" instead of "user@xxx"
                        AuthenticationUtil.pushAuthentication();
                        AuthenticationUtil.setFullyAuthenticatedUser(email);
                        
                        final String siteShortName = AuthenticationUtil.runAs(new RunAsWork<String>()
                                {
                            @SuppressWarnings("synthetic-access")
                            public String doWork() throws Exception
                            {
                                // generate short name
                                String siteShortName = getHomeSiteShortName(email);
                                
                                for (int i = 1; i <= 10; i++)
                                {
                                    // check if site already exists (run as system to also check for private sites)
                                    if (siteService.getSite(siteShortName) == null)
                                    {
                                        break;
                                    }
                                    
                                    // assume site with short name has already been created (eg. by someone else) and try random suffix (eg. upto 10 times if needed)
                                    siteShortName = siteShortName + '-' + String.format("%05d", Math.round(Math.random()*10000));
                                }
                                
                                return siteShortName;
                            }
                                }, AuthenticationUtil.getSystemUserName());
                        
                        String siteTitle =  I18NUtil.getMessage("suwf_signup.selfsignup.homesite.title", firstName, lastName);
                        String siteDescription=  I18NUtil.getMessage("suwf_signup.selfsignup.homesite.description", firstName, lastName);
                        
                        long start = System.currentTimeMillis();
                        
                        siteService.createSite("sitePreset", siteShortName, siteTitle, siteDescription, SiteVisibility.PRIVATE);
                        importSite(siteShortName);

                        if (logger.isInfoEnabled())
                        {
                            logger.info("createSite: "+email+" in "+(System.currentTimeMillis()-start)+" ms");
                        }
                    }
                    finally
                    {
                        AuthenticationUtil.popAuthentication();
                    }
                    
                }
                
                return null;
            }
        }, account.getTenantId());
        
        // Once the user is successfully created, send an AnalyticsEvent.
        Analytics.record_Activation(email);
        
        if (logger.isInfoEnabled())
        {
            logger.info("createUser: "+email+" in "+(System.currentTimeMillis()-start)+" ms");
        }
        
        return account;
    }
    
    private void importSite(final String siteShortName)
    {
        ImportPackageHandler acpHandler = new HomeSiteImportPackageHandler(homeSiteSurfConfig, siteShortName);
        Location location = new Location(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        location.setPath("/app:company_home/st:sites/cm:" + ISO9075.encode(siteShortName));
        ImporterBinding binding = new ImporterBinding()
        {
            @Override
            public String getValue(String key)
            {
                if (key.equals("siteId"))
                {
                    return siteShortName;
                }
                return null;
            }
            
            @Override
            public UUID_BINDING getUUIDBinding()
            {
                return UUID_BINDING.CREATE_NEW;
            }
            
            @Override
            public QName[] getExcludedClasses()
            {
                return null;
            }
            
            @Override
            public boolean allowReferenceWithinTransaction()
            {
                return false;
            }
            
            @Override
            public ImporterContentCache getImportConentCache()
            {
                return null;
            }
        };
        importerService.importView(acpHandler, location, binding, (ImporterProgress)null);
    }
    
    @Override
    public Account addUser(Long accountId, String email)
    {
        // ensure account and user exist
        Account account = accountService.getAccount(accountId);
        if (account == null)
        {
            throw new AccountNotFoundException("Account " + accountId + " not found");
        }
        if (!directoryService.userExists(email))
        {
            throw new InvalidEmailAddressException("User " + email + " does not exist");
        }

        // check user is not already part of account
        Long homeAccountId = directoryService.getHomeAccount(email);
        if (accountId.equals(homeAccountId))
        {
            throw new RegistrationServiceException("User " + email + " is already an internal member of account " + accountId);
        }
        List<Long> secondaryAccountIds = directoryService.getSecondaryAccounts(email);
        if (secondaryAccountIds.contains(accountId))
        {
            throw new RegistrationServiceException("User " + email + " is already an external member of account " + accountId);
        }
        
        // copy person from home account to new account
        personReplicationComponent.copyPerson(email, accountService.getAccountTenant(homeAccountId), account.getTenantId());
        
        // add secondary account
        directoryService.addSecondaryAccount(email, accountId);
        
        // if the user has no default account, set it to this one. This is expected to happen for Public Email Domain users.
        if (directoryService.getDefaultAccount(email) == null)
        {
            directoryService.setDefaultAccount(email, accountId);
        }
        
        return account;
    }

    @Override
    public void removeExternalUser(final Long accountId, final String email)
    {
        // ensure account and user exist
        Account account = accountService.getAccount(accountId);
        if (account == null)
        {
            throw new AccountNotFoundException("Account " + accountId + " not found");
        }
        if (!directoryService.userExists(email))
        {
            throw new InvalidEmailAddressException("User " + email + " does not exist");
        }
        
        // check user is an external user to the account
        List<Long> secondaryAccountIds = directoryService.getSecondaryAccounts(email);
        if (!secondaryAccountIds.contains(accountId))
        {
            throw new CannotRemoveUserException("User " + email + " is not an external member of account " + accountId);
        }
        
        cloudPersonService.removeExternalUser(account, email);
    }
    
    @Override public void deleteUser(final String email)
    {
        if (!directoryService.userExists(email))
        {
            throw new InvalidEmailAddressException("User " + email + " does not exist");
        }
        
        Account usersHomeAccount = this.getHomeAccount(email);
        
        // We'll demote the user from a NetworkAdmin role.
        // If they are not a NetworkAdmin, this will be a no-op.
        // If they are a NetworkAdmin, they will be demoted prior to deletion, which shouldn't matter as they're being deleted anyway.
        // However - and this is the point - if they are the last NetworkAdmin for the network, the demotion will throw an exception
        demoteUserImpl(usersHomeAccount, email);
        
        // Remove the user from each account where they are an external user.
        for (Long secondaryAccountId : directoryService.getSecondaryAccounts(email))
        {
            Account secondaryAccount = accountService.getAccount(secondaryAccountId);
            cloudPersonService.removeExternalUser(secondaryAccount, email);
        }
        
        cloudPersonService.removePersonProfile(email, usersHomeAccount.getTenantId());
        directoryService.deleteUser(email);
    }
    
    @Override
    public void deleteSplittedPerson(String corruptedEmail)
    {
        String originalUserId = corruptedEmail.substring(0, corruptedEmail.length() - GUID_LENGTH);
        
        int idx1 = originalUserId.indexOf(TenantService.SEPARATOR);
        String domain = originalUserId.substring(idx1+1);
        
        cloudPersonService.removePersonProfile(corruptedEmail, domain);
        
        // if username in lowercase it may be used by primary person (among duplicates)
        // or can be already deleted when primary person was deleted
        if (!originalUserId.toLowerCase().equals(originalUserId))
        {
            directoryService.deleteCaseSensativeUser(originalUserId);
        }
    }
    
    @Override public void promoteUserToNetworkAdmin(Long accountId, final String email)
    {
        final String tenantDomain = accountService.getAccountTenant(accountId);
        
        if (tenantDomain == null)
        {
            throw new RegistrationServiceException("Cannot promote user in null account.");
        }
        
        if ( !directoryService.userExists(email))
        {
            throw new NoSuchUserException("User does not exist " + email);
        }
        
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
        	@Override public Void doWork()
            {
                final boolean userIsAlreadyAdmin = authorityService.getContainedAuthorities(AuthorityType.USER, GROUP_NETWORK_ADMINS, true).contains(email);
                if (userIsAlreadyAdmin)
                {
                    return null;
                }
                
                final NodeRef personNode = cloudPersonService.getPerson(email, false);
                
                // If the user is external within this tenant, then they cannot be promoted to NetworkAdmin.
                if (nodeService.hasAspect(personNode, CloudModel.ASPECT_EXTERNAL_PERSON))
                {
                    throw new IllegalNetworkAdminUserException("Cannot make external user network admin");
                }
                
                authorityService.addAuthority(GROUP_NETWORK_ADMINS, email);
                
                nodeService.addAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN, null);
                return null;
            }
        }, tenantDomain);
    }
    
    @Override public void demoteUserFromNetworkAdmin(Long accountId, final String email)
    {
        final Account account = accountService.getAccount(accountId);
        
        if (account == null)
        {
            throw new RegistrationServiceException("Cannot demote user in null account.");
        }
        
        if ( !directoryService.userExists(email))
        {
            throw new NoSuchUserException("User does not exist " + email);
        }
        
        demoteUserImpl(account, email);
    }
    
    /**
     * Helper method to perform demotion of user from NetworkAdmin role.
     * 
     * @param account the account in which the user is a NetworkAdmin.
     * @param userid  the userid of the NetworkAdmin user to be deleted.
     */
    private void demoteUserImpl(final Account account, final String userid)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork()
            {
                Set<String> existingNetworkAdmins = authorityService.getContainedAuthorities(AuthorityType.USER, GROUP_NETWORK_ADMINS, true);
                final boolean userIsAlreadyNetworkAdmin = existingNetworkAdmins.contains(userid);
                if ( !userIsAlreadyNetworkAdmin)
                {
                    return null;
                }
                else if (existingNetworkAdmins.size() == 1
                         && account.getType().getId() >= AccountType.STANDARD_NETWORK_ACCOUNT_TYPE)
                {
                    throw new CannotDemoteLastNetworkAdminException("Failed to demote last NetworkAdmin " + userid);
                }
                
                authorityService.removeAuthority(GROUP_NETWORK_ADMINS, userid);
                
                NodeRef personNode = cloudPersonService.getPerson(userid, false);
                nodeService.removeAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN);
                return null;
            }
        }, account.getTenantId());
    }
    
    @Override
    public Account getHomeAccount(String email)
    {
        Long accountId = directoryService.getHomeAccount(email);
        return accountId == null ? null : accountService.getAccount(accountId);
    }

    @Override
    public List<Account> getSecondaryAccounts(String email)
    {
        List<Long> secondaryAccountIds = directoryService.getSecondaryAccounts(email);
        List<Account> secondaryAccounts = new ArrayList<Account>(secondaryAccountIds.size());
        for (Long secondaryAccountId : secondaryAccountIds)
        {
            Account account = accountService.getAccount(secondaryAccountId);
            secondaryAccounts.add(account);
        }
        return Collections.unmodifiableList(secondaryAccounts);
    }
    
    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();

        String componentId = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        if (componentId == null)
        {
            logger.warn("Unable to import sample content: ComponentId is null");
            return;
        }

        if (componentId.equals(DOC_LIBRARY))
        {
            // double check that documentLibrary is being created in a site
            SiteInfo siteInfo = siteService.getSite(nodeRef);
            if (siteInfo == null)
            {
                logger.warn("Unable to import sample content: unable to find site");
                return;
            }

            // and that it is a home site
            String email = AuthenticationUtil.getFullyAuthenticatedUser();
            String siteShortName = getHomeSiteShortName(email);
            String name = (String) nodeService.getProperty(siteInfo.getNodeRef(), ContentModel.PROP_NAME);
            if (name != null && name.equals(siteShortName))
            {
                ChildAssociationRef child = nodeService.getPrimaryParent(nodeRef);
                if (child == null)
                {
                    return;
                }

                if (child.getParentRef().equals(siteInfo.getNodeRef()))
                {
                    OnCreateDocumentLibraryPolicy policy = onCreateDocumentLibraryDelegate.get(ContentModel.TYPE_FOLDER);
                    policy.onCreateDocumentLibrary(email, TenantUtil.getCurrentDomain(), child.getChildRef());
                }
            }
        }
    }
    
    @Override
    public void onCreateDocumentLibrary(final String user, String tenantId, final NodeRef documentLibrary)
    {
    	TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
		{
			@Override
			public Void doWork() throws Exception
			{
			    Action mail = actionService.createAction(HomeSiteContentImportActionExecutor.NAME);
			    mail.setParameterValue(HomeSiteContentImportActionExecutor.PARAM_USER, user);
			    actionService.executeAction(mail, documentLibrary, true, true);

				return null;
			}

		}, tenantId);
    }
    
    /**
     * Registration Data Object
     */
    private static class RegistrationImpl implements Registration
    {
        private String emailAddress;
        private Date registrationDate;
        private String id;
        private String key;
        private String initiatorEmailAddress;
        private String initiatorFirstName;
        private String initiatorLastName;
        
        private RegistrationImpl(String email, Date registrationDate, String id, String key)
        {
            this.emailAddress = email;
            this.registrationDate = registrationDate;
            this.id = id;
            this.key = key;
        }

        private RegistrationImpl(String email, Date registrationDate, String id, String key, String initiatorEmailAddress, String initiatorFirstName, String initiatorLastName)
        {
            this(email, registrationDate, id, key);
            this.initiatorEmailAddress = initiatorEmailAddress;
            this.initiatorFirstName = initiatorFirstName;
            this.initiatorLastName = initiatorLastName;
        }
        
        @Override
        public String getEmailAddress()
        {
            return emailAddress;
        }

        @Override
        public Date getRegistrationDate()
        {
            return registrationDate;
        }

        @Override
        public String getId()
        {
            return id;
        }

        @Override
        public String getKey()
        {
            return key;
        }

        @Override
        public String getInitiatorEmailAddress()
        {
            return initiatorEmailAddress;
        }

        @Override
        public String getInitiatorFirstName()
        {
            return initiatorFirstName;
        }

        @Override
        public String getInitiatorLastName()
        {
            return initiatorLastName;
        }
    }
    
    public static enum INVALID_EMAIL_TYPE
    {
        INCORRECT_DOMAIN, USER_EXISTS;

        public static INVALID_EMAIL_TYPE getType(String type)
        {
            if (type.toUpperCase().equals("INCORRECT_DOMAIN"))
            {
                return INCORRECT_DOMAIN;
            }
            else if (type.toUpperCase().equals("USER_EXISTS"))
            {
                return USER_EXISTS;
            }
            else
            {
                return null;
            }
        }
    };
    
    public static class InvalidEmail
    {
        private String email;
        private INVALID_EMAIL_TYPE type;

        public InvalidEmail(String email, INVALID_EMAIL_TYPE type)
        {
            super();
            this.email = email;
            this.type = type;
        }

        public String getEmail()
        {
            return email;
        }

        public INVALID_EMAIL_TYPE getType()
        {
            return type;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
            result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (!(obj instanceof InvalidEmail))
            {
                return false;
            }
            InvalidEmail other = (InvalidEmail) obj;
            if (this.email == null)
            {
                if (other.email != null)
                {
                    return false;
                }
            }
            else if (!this.email.equals(other.email))
            {
                return false;
            }
            if (this.type != other.type)
            {
                return false;
            }
            return true;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("InvalidEmail[");
            sb.append("email: ");
            sb.append(email);
            sb.append("type: ");
            sb.append(type);
            sb.append(']');
            return sb.toString();
        }
    }

}
