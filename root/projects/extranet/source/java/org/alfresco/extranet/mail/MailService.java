/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.extranet.mail;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.alfresco.extranet.ApplicationProperties;
import org.alfresco.extranet.database.DatabaseInvitedUser;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.tools.URLUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * The Class MailService.
 * 
 * @author muzquiano
 */
public class MailService implements ApplicationContextAware  
{
    protected ApplicationContext applicationContext;
    protected JavaMailSender mailSender;
    public String adminEmailAddress;
    
    public String inviteUserEmailTemplateUrl;
    public String resetPasswordEmailTemplateUrl;
    
    /**
     * Sets the mail sender.
     * 
     * @param mailSender the new mail sender
     */
    public void setMailSender(JavaMailSender mailSender) 
    {
        this.mailSender = mailSender;
    }
    
    /**
     * Sets the invite user email template url.
     * 
     * @param inviteUserEmailTemplateUrl the new invite user email template url
     */
    public void setInviteUserEmailTemplateUrl(String inviteUserEmailTemplateUrl)
    {
        this.inviteUserEmailTemplateUrl = inviteUserEmailTemplateUrl;
    }
    
    /**
     * Sets the reset user email template url
     * 
     * @param resetPasswordEmailTemplateUrl
     */
    public void setResetPasswordEmailTemplateUrl(String resetPasswordEmailTemplateUrl)
    {
        this.resetPasswordEmailTemplateUrl = resetPasswordEmailTemplateUrl;
    }
    
    /**
     * Sets the admin email address.
     * 
     * @param adminEmailAddress the new admin email address
     */
    public void setAdminEmailAddress(String adminEmailAddress)
    {
        this.adminEmailAddress = adminEmailAddress;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }    

    /**
     * Invite user.
     * 
     * @param invitedUser the invited user
     */
    public void inviteUser(DatabaseInvitedUser invitedUser)
    {
        inviteUser(invitedUser, inviteUserEmailTemplateUrl);
    }
    
    /**
     * Invite user.
     * 
     * @param invitedUser the invited user
     * @param bodyUri the uri from which to load the body
     */
    public void inviteUser(final DatabaseInvitedUser invitedUser, String bodyUri)
    {
        // pull out application properties
        ApplicationProperties props = (ApplicationProperties) this.applicationContext.getBean("application.properties");
        final String hostPort = props.getHostPort();
        final String webapp = props.getWebapp();
        final String webappUrl = hostPort + webapp;
        
        // build the email body
        final StringBuffer buffer = new StringBuffer();
        try
        {
            // load the email from the url connection
            String bodyString = URLUtil.get(bodyUri);
            if(bodyString != null)
            {
                buffer.append(bodyString);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        // preparator
        MimeMessagePreparator preparator = new MimeMessagePreparator() 
        {
            public void prepare(MimeMessage mimeMessage) 
                throws MessagingException 
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(invitedUser.getEmail()));
                mimeMessage.setFrom(new InternetAddress(adminEmailAddress));
                mimeMessage.setSubject("An invitation to Alfresco Network");
                
                // do variable substitution
                String body = buffer.toString();
                body = fullReplace(body, "${user.firstName}", invitedUser.getFirstName());
                body = fullReplace(body, "${user.middleName}", invitedUser.getMiddleName());
                body = fullReplace(body, "${user.lastName}", invitedUser.getLastName());
                body = fullReplace(body, "${user.companyId}", invitedUser.getCompanyId());
                body = fullReplace(body, "${user.expirationDate}", invitedUser.getExpirationDate().toString());
                body = fullReplace(body, "${user.hash}", invitedUser.getHash());
                body = fullReplace(body, "${user.userId}", invitedUser.getUserId());
                body = fullReplace(body, "${user.whdUserId}", invitedUser.getWebHelpdeskUserId());
                
                // dates
                body = fullReplace(body, "${user.expirationDate}", formatDate(invitedUser.getExpirationDate()));
                
                // webapp
                body = fullReplace(body, "${webapp.url}", webappUrl);
                
                // set email text
                mimeMessage.setText(body);
            }
        };
        
        // send the mail
        try
        {
            mailSender.send(preparator);
        }
        catch (MailException ex)
        {
            //log it and go on
            ex.printStackTrace();
        }
    }
    
    /**
     * Full replace.
     * 
     * @param body the body
     * @param token the token
     * @param value the value
     * 
     * @return the string
     */
    public static String fullReplace(String body, String token, String value)
    {
        int x = -1;
        do
        {
            x = body.indexOf(token);
            if(x > -1)
            {
                body = body.substring(0, x) + value + body.substring(x + token.length());
            }
        }
        while(x > -1);
        
        return body;
    }
    
    public static String formatDate(Date date)
    {
        String formattedDate = "Unknown";
        
        if(date != null)
        {
            formattedDate = SimpleDateFormat.getInstance().format(date);
        }
        
        return formattedDate;
    }
    
    /**
     * Resets the user password
     * 
     * @param user the user
     */
    public void resetUserPassword(DatabaseUser user, String newPassword)
    {
        resetUserPassword(user, newPassword, resetPasswordEmailTemplateUrl);
    }
    
    /**
     * Resets the user password
     * 
     * @param invitedUser the invited user
     * @param bodyUri the uri from which to load the body
     */
    public void resetUserPassword(final DatabaseUser user, final String newPassword, String bodyUri)
    {
        // pull out application properties
        ApplicationProperties props = (ApplicationProperties) this.applicationContext.getBean("application.properties");
        final String hostPort = props.getHostPort();
        final String webapp = props.getWebapp();
        final String webappUrl = hostPort + webapp;
        
        // build the email body
        final StringBuffer buffer = new StringBuffer();
        try
        {
            // load the email from the url connection
            String bodyString = URLUtil.get(bodyUri);
            if(bodyString != null)
            {
                buffer.append(bodyString);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        // preparator
        MimeMessagePreparator preparator = new MimeMessagePreparator() 
        {
            public void prepare(MimeMessage mimeMessage) 
                throws MessagingException 
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
                mimeMessage.setFrom(new InternetAddress(adminEmailAddress));
                mimeMessage.setSubject("Your login information for Alfresco Network");
                
                // do variable substitution
                String body = buffer.toString();
                body = fullReplace(body, "${user.firstName}", user.getFirstName());
                body = fullReplace(body, "${user.middleName}", user.getMiddleName());
                body = fullReplace(body, "${user.lastName}", user.getLastName());
                body = fullReplace(body, "${user.email}", user.getEmail());
                body = fullReplace(body, "${user.userId}", user.getUserId());
                body = fullReplace(body, "${user.id}", user.getUserId());
                body = fullReplace(body, "${user.description}", user.getDescription());
                body = fullReplace(body, "${user.level}", user.getLevel());
                                
                // dates
                body = fullReplace(body, "${user.subscriptionStart}", formatDate(user.getSubscriptionStart()));
                body = fullReplace(body, "${user.subscriptionEnd}", formatDate(user.getSubscriptionEnd()));
                
                // webapp
                body = fullReplace(body, "${webapp.url}", webappUrl);
                
                // password
                body = fullReplace(body, "${password}", newPassword);
                
                // set email text
                mimeMessage.setText(body);
            }
        };
        
        // send the mail
        try
        {
            mailSender.send(preparator);
        }
        catch (MailException ex)
        {
            //log it and go on
            ex.printStackTrace();
        }
    }
    
}