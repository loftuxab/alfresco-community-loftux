<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page isELIgnored="false" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<table cellspacing="0" cellpadding="2" width="100%">
   <tr>
      <%-- Top level toolbar and company logo area --%>
      <td width="100%">
         <table cellspacing="0" cellpadding="0" width="100%">
            <tr>
               <td><img src="<%=request.getContextPath()%>/images/parts/titlebar_begin.gif" width="28" height="30" /></td>
               <td width="100%" style="background-image: url(<%=request.getContextPath()%>/images/parts/titlebar_bg.gif)">
                  <span class="topToolbarTitle">System Error</span>
               </td>
               <td><img src="<%=request.getContextPath()%>/images/parts/titlebar_end.gif" width=4 height=30></td>
            </tr>
         </table>
      </td>
   </tr>
   <tr>
      <td>
         <p><r:systemError styleClass="errorMessage" detailsStyleClass="mainSubTextSmall" /></p>
      </td>
   </tr>
   <tr>
      <td>
         <p><a href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="org.apache.myfaces.portlet.MyFacesGenericPortlet.VIEW_ID" value="last-jsf-page"/></portlet:renderURL>">
            Return to application
         </a></p>
      </td>
   </tr>
</table>


