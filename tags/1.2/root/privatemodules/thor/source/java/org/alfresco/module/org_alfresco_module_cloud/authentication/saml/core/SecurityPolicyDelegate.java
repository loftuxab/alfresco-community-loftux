/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.util.List;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.provider.BasicSecurityPolicy;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SecurityPolicyDelegate implements SecurityPolicy
{

    private final BasicSecurityPolicy basicSecurityPolicy;

    public SecurityPolicyDelegate(List<SecurityPolicyRule> securityPolicyRules)
    {
        basicSecurityPolicy = new BasicSecurityPolicy();
        basicSecurityPolicy.getPolicyRules().addAll(securityPolicyRules);
    }

    @Override
    public void evaluate(MessageContext messageContext) throws SecurityPolicyException
    {
        basicSecurityPolicy.evaluate(messageContext);
    }

    @Override
    public List<SecurityPolicyRule> getPolicyRules()
    {
        return basicSecurityPolicy.getPolicyRules();
    }

}
