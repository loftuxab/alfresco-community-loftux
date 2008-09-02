package org.alfresco.extranet;

import org.alfresco.web.site.exception.RequestContextException;
import org.alfresco.extranet.database.DatabaseInvitedUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Utility class for the admin-tools functionality.
 * This way we have something to unit test.
 *
 * @author jsant
 */
public class AdminUtil {
    HttpServletRequest request;

    public AdminUtil(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Check if this user is authorized to see the admin-tools section or not
     * @return true if authorized with admin status, false otherise
     * @throws RequestContextException
     */
    public boolean isAuthorizedAdmin() throws RequestContextException
    {
        org.alfresco.connector.User user = org.alfresco.web.site.RequestUtil.getRequestContext(request).getUser();
        return user != null && user.isAdmin();
    }


    /**
     * assign the value to null if it has no characters
     * @param value the value to check
     * @return the original no-zero-length value, or null
     */
    public String nullAssert(String value)
	{
	    if(value != null && value.length() == 0)
	    {
	        value = null;
	    }

	    return value;
	}


    /**
     * Generates a Network invitation based on the parameters found in the request
     * @return a String containing a reults message to be displyed in the browser
     * @throws ParseException
     */
    public String inviteUser() throws ParseException
    {
        String result = "";
        // get services
        InvitationService invitationService = ExtranetHelper.getInvitationService(request);

        // command processor
        String command = request.getParameter(Constants.ADMIN_TOOLS_COMMAND);
        if(Constants.ADMIN_TOOLS_COMMAND_INVITE_USER.equals(command))
        {
            result = "A problem occured generating the invitation. See error log.";
            
            String userId = request.getParameter(Constants.ADMIN_TOOLS_USER_ID);
            String firstName = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_FIRST_NAME));
            String lastName = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_LAST_NAME));
            String email = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_EMAIL));
            String whdUserId = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_WHD_USER_ID));
            String alfrescoUserId = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_ALFRESCO_USER_ID));

            String subscriptionStart = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_SUBSCRIPTION_START));
            String subscriptionEnd = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_SUBSCRIPTION_END));

            // we only handle enterprise and employee invitation types for the moment
            String invitationType = nullAssert(request.getParameter(Constants.ADMIN_TOOLS_INVITATION_TYPE));
            if(Constants.ADMIN_TOOLS_INVITATION_ENTERPRISE.equals(invitationType) || Constants.ADMIN_TOOLS_INVITATION_EMPLOYEE.equals(invitationType))
            {
                // build date objects
                Date subscriptionStartDate = null;
                if(subscriptionStart != null)
                {
                    subscriptionStartDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(subscriptionStart);
                    System.out.println("Original Start Date: " + subscriptionStart);
                    System.out.println("New Start Date: " + subscriptionStartDate);
                }
                else
                {
                    result = Constants.ADMIN_TOOLS_SUBSCRIPTION_START + " was null";
                }

                Date subscriptionEndDate = null;
                if(subscriptionEnd != null)
                {
                    subscriptionEndDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(subscriptionEnd);
                    System.out.println("Original End Date: " + subscriptionEnd);
                    System.out.println("New End Date: " + subscriptionEndDate);
                }
                else
                {
                    result = Constants.ADMIN_TOOLS_SUBSCRIPTION_END + " was null";
                }


                // invite the user
                DatabaseInvitedUser invitedUser = invitationService.inviteUser(userId, firstName, lastName, email, whdUserId, alfrescoUserId, Constants.ADMIN_TOOLS_INVITATION_ENTERPRISE, subscriptionStartDate, subscriptionEndDate);
                if(invitedUser != null)
                {
                    result = "Invitation was created!";
                }
            }
        }
        return result;
    }

    /**
     * Adds the given entity to the database
     *
     * @param entityType the type of entity
     * @param entityTitle the title of this entity
     * @param propertyNames the property names of the entity
     * @return  message to be displayed in the JSP
     */
    public String addEntity(String entityType, String entityTitle, String[] propertyNames)
    {
        String result = "";

        // get the appropriate entity service
        EntityService entityService = ExtranetHelper.getEntityService(request, entityType);
        String entityClassName = ExtranetHelper.getEntityClassName(entityType);

        // command processing
        String command = request.getParameter(Constants.ADMIN_TOOLS_COMMAND);
        if(Constants.ADMIN_TOOLS_COMMAND_SAVE.equals(command))
        {
            String userId = request.getParameter(Constants.ADMIN_TOOLS_USER_ID);

            // new entity
            Entity entity = ExtranetHelper.newEntity(entityType, userId);

            // set properties
            for(int i = 0; i < propertyNames.length; i++)
            {
                String value = request.getParameter(propertyNames[i]);
                entity.setProperty(propertyNames[i], value);
            }

            // add
            entityService.insert(entity);

            StringBuilder resultStringBuilder = new StringBuilder();
            resultStringBuilder.append(entityTitle).append( " added!").append("<br/>/n");
            resultStringBuilder.append("<a href='?p=admin-tools&dispatchTo=admin-entities'>Entities</a>");
            result = resultStringBuilder.toString();

        }
        return result;
    }

}
