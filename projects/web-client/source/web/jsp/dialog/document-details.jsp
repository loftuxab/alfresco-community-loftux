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
   
   <h:form id="document-details">
   
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
                              <h:graphicImage id="wizard-logo" url="/images/icons/file_large.gif" />
                           </td>
                           <td>
                              <h:outputText value="#{BrowseBean.actionSpace.name}" styleClass="mainSubTitle"/><br/>
                              <div class="mainTitle">Details of '<h:outputText value="#{BrowseBean.document.properties.title}" />'</div>
                              <div class="mainSubText"><h:outputText value="#{msg.documentdetails_description}" /></div>
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=110 style="padding-left:2px">
                              <%-- Current object actions --%>
                              <h:outputText style="padding-left:20px" styleClass="mainSubTitle" value="#{msg.actions}" /><br>
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
                              <a:panel label="Preview" id="preview-panel" progressive="true"
                                       border="white" bgcolor="white" titleBorder="greyround" titleBgcolor="#eaeaea">
                                 <a:actionLink value="#{BrowseBean.document.name}" href="#{BrowseBean.document.properties.url}" />
                              </a:panel>
                              <br/>
                              <a:panel label="Properties" id="properties-panel" progressive="true"
                                       border="white" bgcolor="white" titleBorder="greyround" titleBgcolor="#eaeaea"
                                       action="editDocProperties" linkIcon="/images/icons/Change_details.gif"
                                       actionListener="#{EditDocPropsDialog.startWizardForEdit}">
                                 <r:propertySheetGrid id="general-props" value="#{BrowseBean.document}" var="generalProps" 
                                                      columns="1" mode="view" labelStyleClass="propertiesLabel">
                                    <r:property name="name" displayLabel="File Name" />
                                    <r:property name="mimetype" displayLabel="Content Type" />
                                    <r:property name="title" displayLabel="Title" />
                                    <r:property name="description" displayLabel="Description" />
                                    <r:property name="creator" displayLabel="Author" />
                                    <r:property name="created" displayLabel="Created" readOnly="true" />
                                    <r:property name="modified" displayLabel="Modified" readOnly="true" />
                                 </r:propertySheetGrid>
                                 <h:messages styleClass="errorMessage" />
                              </a:panel>
                              <br/>
                              <a:panel label="Workflow" id="workflow-panel" progressive="true" expanded="false"
                                       border="white" bgcolor="white" titleBorder="greyround" titleBgcolor="#eaeaea">
                                 Workflow options
                              </a:panel>
                              <br/>
                              <a:panel label="Categories" id="categories-panel" progressive="true" expanded="false"
                                       border="white" bgcolor="white" titleBorder="greyround" titleBgcolor="#eaeaea">
                                 Categories
                              </a:panel>
                              <br/>
                              <a:panel label="Version History" id="version-history-panel" progressive="true" expanded="false"
                                       border="white" bgcolor="white" titleBorder="greyround" titleBgcolor="#eaeaea">
                                 Version History
                              </a:panel>
                              <br/>
                           </td>
                           
                           <td valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Close" action="browse" styleClass="wizardButton" />
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