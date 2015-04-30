/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.users;

import org.alfresco.service.namespace.QName;

/**
 * Workflow Model for a Cloud Account password rest
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public interface WorkflowModelResetPassword
{
    // namespace
    public static final String NAMESPACE_URI = "http://www.alfresco.org/model/workflow/cloud/resetpassword/1.0";
    
    // process name
    public static final String WORKFLOW_DEFINITION_NAME = "activiti$resetPassword";
    
    // task names
    public static final String TASK_RESET_PASSWORD = "resetPasswordTask";
    
    // timers
    public static final QName WF_PROP_TIMER_END = QName.createQName(NAMESPACE_URI, "endTimer");

    // workflow properties
    public static final QName WF_PROP_USERNAME = QName.createQName(NAMESPACE_URI, "userName");
    public static final String WF_PROP_USERNAME_ACTIVITI = "resetpasswordwf_userName";
    
    public static final QName WF_PROP_KEY = QName.createQName(NAMESPACE_URI, "key");
    public static final String WF_PROP_KEY_ACTIVITI = "resetpasswordwf_key";
    
    public static final QName WF_PROP_PASSWORD = QName.createQName(NAMESPACE_URI, "password");
    public static final String WF_PROP_PASSWORD_ACTIVITI = "resetpasswordwf_password";
    
    // workflow state
    public static final String WF_USERNAME_STATUS = "resetpasswordwf_usernameStatus";
    public static final String WF_USERNAME_STATUS_NONE = "None";
    public static final String WF_USERNAME_STATUS_REGISTERED = "Registered";
    public static final String WF_USERNAME_STATUS_ACTIVATED = "Activated";
}
