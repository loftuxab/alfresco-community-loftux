package org.alfresco.enterprise.repo.officeservices.dispatch;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.lists.AbstractListsService;
import com.xaldon.officeservices.lists.CheckinType;
import com.xaldon.officeservices.lists.ListChanges;
import com.xaldon.officeservices.lists.ListUpdatesBatch;
import com.xaldon.officeservices.lists.ListsGetListAndViewView;
import com.xaldon.officeservices.lists.ListsGetListContentTypeContentType;
import com.xaldon.officeservices.lists.ListsGetListContentTypesContentType;
import com.xaldon.officeservices.lists.ListsGetListList;
import com.xaldon.officeservices.lists.UpdateListItemsResults;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.protocol.SoapParameter;

public class ListsService extends AbstractListsService
{

    private static final long serialVersionUID = 6923578592507345851L;

    @Override
    protected boolean isWSS3List(UserData userData, String listName)
    {
        return true;
    }

    @Override
    protected int getListType(UserData userData, String listName)
    {
        return ListsService.LISTTYPE_UNKNOWN;
    }

    @Override
    protected boolean isValidListName(UserData userData, String listName)
    {
        return false;
    }

    @Override
    protected String getAlternateServerURL(UserData userData, String listName, HttpServletRequest request)
    {
        String protocol = request.isSecure() ? "https://" : "http://";
        int defaultPort = request.isSecure() ? 443 : 80;
        String portString = (request.getLocalPort() != defaultPort) ? ":" + Integer.toString(request.getLocalPort()) : "";
        return protocol + request.getServerName() + portString;
    }

    @Override
    protected String getRootFolder(UserData userData, String listName)
    {
        return "/";
    }

    @Override
    protected ListChanges getListItemChanges(UserData userData, String listName, String since, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    protected ListChanges getListItems(UserData userData, String listName, SoapParameter query, SoapParameter queryOptions, SoapParameter viewFields, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    protected UpdateListItemsResults updateListItems(UserData userData, String listName, ListUpdatesBatch batch, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    protected ListChanges getListChangesSinceToken(UserData userData, String listName, SoapParameter viewFields, String rowLimit, SoapParameter queryOptions, String changeToken, SoapParameter contains, HttpServletRequest request)
            throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    protected boolean checkoutFile(UserData userData, String documentName, boolean checkoutToLocal, String lastModified) throws AuthenticationRequiredException
    {
        return false;
    }

    @Override
    protected boolean checkinFile(UserData userData, String documentName, String comment, CheckinType checkinType) throws AuthenticationRequiredException
    {
        return false;
    }

    @Override
    protected boolean undoCheckOut(UserData userData, String documentName) throws AuthenticationRequiredException
    {
        return false;
    }

    @Override
    protected ListsGetListContentTypeContentType getListContentType(UserData userData, String listName, String contentTypeId, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    protected ListsGetListContentTypesContentType[] getListContentTypes(UserData userData, String listName, String contentTypeId, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    public List<?> getListCollection(SimpleSoapParser parser)
    {
        return null;
    }

    @Override
    protected ListsGetListList getList(UserData userData, String listName, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    @Override
    protected ListsGetListAndViewView getView(UserData userData, String listName, String viewName, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    protected class ListsServiceUserData implements UserData
    {

        public String getUsername()
        {
            return "";
        }

    }

    protected UserData userData = new ListsServiceUserData();

    public UserData negotiateAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        return userData;
    }

    public void requestAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // nothing to do here
    }

    public void invalidateAuthentication(UserData userData, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // nothing to do here
    }

}
