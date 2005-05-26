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
   <h:form id="new-space-from-scratch">
   
   <%-- Main outer table --%>
   <table cellspacing="0" cellpadding="2">
      
      <%-- Title bar --%>
      <tr>
         <td colspan="2">
            <%@ include file="../../parts/titlebar.jsp" %>
         </td>
      </tr>
      
      <%-- Main area --%>
      <tr valign="top">
         <%-- Shelf --%>
         <td>
            <%@ include file="../../parts/shelf.jsp" %>
         </td>
         
         <%-- Work Area --%>
         <td width="100%">
            <table cellspacing="0" cellpadding="0" width="100%">
               <%-- Breadcrumb --%>
               <%@ include file="../../parts/breadcrumb.jsp" %>
               
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
                              <div class="mainSubTitle"/><h:outputText value='#{NavigationBean.nodeProperties["name"]}' /></div>
                              <div class="mainTitle"><h:outputText value="#{NewSpaceWizard.wizardTitle}" /></div>
                              <div class="mainSubText"><h:outputText value="#{NewSpaceWizard.wizardDescription}" /></div>
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
                           <td width="20%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                              <h:outputText styleClass="mainSubTitle" value="Steps"/><br>
                              <a:modeList itemSpacing="3" iconColumnWidth="2" selectedStyleClass="statusListHighlight"
                                    value="2" disabled="true">
                                 <a:listItem value="1" label="1. Starting Space" />
                                 <a:listItem value="2" label="2. Space Options" />
                                 <a:listItem value="3" label="3. Space Details" />
                                 <a:listItem value="4" label="4. Summary" />
                              </a:modeList>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "blue"); %>
                           </td>
                           
                           <td width="100%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table cellpadding="2" cellspacing="2" border="0" width="100%">
                                 <tr>
                                    <td class="mainSubTitle"><h:outputText value="#{NewSpaceWizard.stepTitle}" /></td>
                                 </tr>
                                 <tr>
                                    <td class="mainSubText"><h:outputText value="#{NewSpaceWizard.stepDescription}" /></td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="wizardSectionHeading">Space Type</td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>Select the type of space you want to create.</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                                       <a:imagePickerRadio columns="4" spacing="4" value="#{NewSpaceWizard.spaceType}"
                                                             onclick="javascript:itemSelected(this);">
                                          <a:listItem value="container" label="Container" tooltip="Container"
                                                            image="/images/icons/space.gif" />
                                          <a:listItem value="wiki" label="Wiki" tooltip="Wiki"
                                                            image="/images/icons/wiki.gif" />
                                          <a:listItem value="discussion" label="Discussion" tooltip="Discussion"
                                                            image="/images/icons/discussion.gif" />
                                       </a:imagePickerRadio>
                                       <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "blue"); %>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                                       <table border='0'>
                                          <tr>
                                             <td valign='top'>
                                                <h:graphicImage id="info-icon" url="/images/icons/info_icon.gif" />
                                             </td>
                                             <td valign='top' align='left'>
                                                <a:dynamicDescription selected="#{NewSpaceWizard.spaceType}">
                                                   <a:description controlValue="container" 
                                                      text="A place for keeping and organising documents and other spaces." />
                                                   <a:description controlValue="discussion" 
                                                      text="A place for discussing the subject of this space." />
                                                   <a:description controlValue="wiki" 
                                                      text="A place to store wiki pages." />
                                                </a:dynamicDescription>
                                             </td>
                                          </tr>
                                       </table>
                                       
                                       <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>
                                       Note:<br/>If you can only see one type of space then other space
                                       types may not be enabled. See your System Administrator for further help.
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td><h:outputText value="#{NewSpaceWizard.stepInstructions}" /></td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Next" action="#{NewSpaceWizard.next}" styleClass="wizardButton" />
                                    </td>
                                 </tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Back" action="#{NewSpaceWizard.back}" styleClass="wizardButton" />
                                    </td>
                                 </tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Finish" action="#{NewSpaceWizard.finish}" styleClass="wizardButton"
                                                        disabled="true" />
                                    </td>
                                 </tr>
                                 <tr><td class="button-group-separator"></td></tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Cancel" action="#{NewSpaceWizard.cancel}" styleClass="wizardButton" />
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