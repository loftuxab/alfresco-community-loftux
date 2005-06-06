<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="32kb" %>
<%@ page isELIgnored="false" %>
<%@ page import="org.alfresco.web.ui.common.PanelGenerator" %>

<r:page>
   
<script language="JavaScript1.2" src="<%=request.getContextPath()%>/scripts/menu.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <%-- REPLACE ME: set the form name here --%>
   <h:form id="new-space">
   
   <%-- Main outer table --%>
   <table cellspacing="0" cellpadding="2">
      
      <%-- Title bar --%>
      <tr>
         <td colspan="2">
            <%@ include file="../parts/titlebar.jsp" %>
         </td>
      </tr>
      
      <%-- Main area --%>
      <tr valign="top">
         <%-- Shelf --%>
         <td>
            <%@ include file="../parts/shelf.jsp" %>
         </td>
         
         <%-- Work Area --%>
         <td width="100%">
            <table cellspacing="0" cellpadding="0" width="100%">
               <%-- Breadcrumb --%>
               <%@ include file="../parts/breadcrumb.jsp" %>
               
               <%-- Status and Actions --%>
               <tr>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_4.gif)" width="4"></td>
                  <td bgcolor="#ECE9E1">
                  
                     <%-- Status and Actions inner contents table --%>
                     <%-- Generally this consists of an icon, textual summary and actions for the current object --%>
                     <table cellspacing="4" cellpadding="0" width="100%">
                        <tr valign="top">
                           <td width="26">
                              <h:graphicImage id="wizard-logo" url="/images/icons/folder_large.png" />
                           </td>
                           <td>
                              <div class="mainSubTitle"/><h:outputText value="#{BrowseBean.actionSpace.name}" /></div>
                              <div class="mainTitle">Create Space</div>
                              <div class="mainSubText">Use this dialog to create a new space.</div>
                           </td>
                        </tr>
                     </table>
                     
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_6.gif)" width="4"></td>
               </tr>
               
               <%-- separator row with gradient shadow --%>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/statuspanel_7.gif" width="4" height="9"></td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_8.gif)"></td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/statuspanel_9.gif" width="4" height="9"></td>
               </tr>
               
               <%-- Details --%>
               <tr valign=top>                  
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width="4"></td>
                  <td>
                     <table cellspacing="0" cellpadding="3" border="0" width="100%">
                        <tr>
                           <td width="100%" valign="top">
                              
                              <a:errors message="#{msg.error_create_space_dialog}" styleClass="errorMessage" />
                              
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table cellpadding="2" cellspacing="2" border="0" width="100%">
                                 <tr>
                                    <td colspan="2" class="wizardSectionHeading">Space Properties</td>
                                 </tr>
                                 <tr>
                                    <td>Name:</td>
                                    <td>
                                       <h:inputText id="name" value="#{NewSpaceDialog.name}" size="35" maxlength="1024" />&nbsp;*
                                    </td>
                                 </tr>
                                 <tr>
                                    <td>Description:</td>
                                    <td>
                                       <h:inputText value="#{NewSpaceDialog.description}" size="35" maxlength="1024" />
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td colspan="2" class="wizardSectionHeading">&nbsp;Other Options</td>
                                 </tr>
                                 <tr>
                                    <td>Choose space icon:</td>
                                    <td>
                                       <table border="0" cellpadding="0" cellspacing="0"><tr><td>
                                       <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                                       <a:imagePickerRadio columns="7" spacing="4" value="#{NewSpaceDialog.icon}">
                                          <a:listItem value="space-icon-default" image="/images/icons/space-icon-default.gif" />
                                          <a:listItem value="space-icon-cd" image="/images/icons/space-icon-cd.gif" />
                                          <a:listItem value="space-icon-www" image="/images/icons/space-icon-www.gif" />
                                          <a:listItem value="space-icon-world" image="/images/icons/space-icon-world.gif" />
                                          <a:listItem value="space-icon-spanner" image="/images/icons/space-icon-spanner.gif" />
                                          <a:listItem value="space-icon-public" image="/images/icons/space-icon-public.gif" />
                                          <a:listItem value="space-icon-orange-ball" image="/images/icons/space-icon-orange-ball.gif" />
                                          <a:listItem value="space-icon-lock" image="/images/icons/space-icon-lock.gif" />
                                          <a:listItem value="space-icon-library" image="/images/icons/space-icon-library.gif" />
                                          <a:listItem value="space-icon-images" image="/images/icons/space-icon-images.gif" />
                                          <a:listItem value="space-icon-id" image="/images/icons/space-icon-id.gif" />
                                          <a:listItem value="space-icon-glasses" image="/images/icons/space-icon-glasses.gif" />
                                          <a:listItem value="space-icon-download" image="/images/icons/space-icon-download.gif" />
                                          <a:listItem value="space-icon-documents" image="/images/icons/space-icon-documents.gif" />
                                       </a:imagePickerRadio>
                                       <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "blue"); %>
                                       </td></tr></table>
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td colspan="2">To create your space click Create Space.</td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Create Space" action="#{NewSpaceDialog.finish}" styleClass="wizardButton" />
                                    </td>
                                 </tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Cancel" action="#{NewSpaceDialog.cancel}" styleClass="wizardButton" />
                                    </td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "blue"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width="4"></td>
               </tr>
               
               <%-- separator row with bottom panel graphics --%>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_7.gif" width="4" height="4"></td>
                  <td width="100%" align="center" style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_8.gif)"></td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_9.gif" width="4" height="4"></td>
               </tr>
               
            </table>
          </td>
       </tr>
    </table>
    
    </h:form>
    
</f:view>

</r:page>