<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<%@ page import="com.activiti.web.PanelGenerator"%>

<%@ page isELIgnored="false" %>

<script language="JavaScript1.2" src="<%=request.getContextPath()%>/scripts/menu.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/wizard.css" TYPE="text/css">
   
<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <h:form id="new-space-wizard-step1">
      
   <%-- Main outer table --%>
   <table cellspacing="0" cellpadding="4" border="1">
      
      <%-- Title bar area --%>
      <tr>
         <td>
            <table cellspacing=0 cellpadding=4 border=1 width=100%>
               <tr>
                  <%-- Top level toolbar and company logo area --%>
                  <td width=100%>
                     Company Space Home
                  </td>
                  <%-- Help area --%>
                  <td width=80>
                     Help
                  </td>
                  <%-- Search area --%>
                  <td width=180>
                     Search
                  </td>
               </tr>
            </table>
         </td>
      </tr>
      
      <%-- Main area --%>
      <tr>
         <%-- Shelf area --%>
         <td width=210>
            Shelf
         </td>
         <%-- Work area --%>
         <td width=100%>
            <table cellspacing=0 cellpadding=4 border=1 width=100%>
               <%-- Breadcrumb --%>
               <tr>
                  <td>
                     Breadcrumb here
                  </td>
               </tr>
               <%-- Status and Actions --%>
               <tr>
                  <td>
                     Status and actions
                  </td>
               </tr>
               <%-- Toolbar --%>
               <tr>
                  <td>
                     <table cellpadding="3" cellspacing="1" border="0">
                        <tr>
                           <td rowspan="3" valign="top">
                              <h:graphicImage id="wizard-logo" url="/images/icons/folder_large.png" />
                           </td>
                           <td class="mainSubTitle">Interface Design</td>
                        </tr>
                        <tr>
                           <td class="mainTitle">New Space</td>
                        </tr>
                        <tr>
                           <td class="mainSubText">Use this wizard to create a new space.</td>
                        </tr>
                     </table>
                  </td>
               </tr>
               <%-- Content --%>
               <tr>
                  <td>
                     <table cellspacing="0" cellpadding="3" border="0" width="100%">
                        <tr>
                           <td width="20%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table cellpadding="1" cellspacing="1" border="0" class="steps-panel">
                                 <tr>
                                    <td class="steps-heading">Steps</td>
                                 </tr>
                                 <tr>
                                    <td class="unselected-step">1. Starting Space</td>
                                 </tr>
                                 <tr>
                                    <td class="unselected-step">2. Space Options</td>
                                 </tr>
                                 <tr>
                                    <td class="unselected-step">3. Space Details</td>
                                 </tr>
                                 <tr>
                                    <td class="selected-step">4. Summary</td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td width="*" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table cellpadding="1" cellspacing="2" border="0" class="details-panel">
                                 <tr>
                                    <td class="details-heading">Step 4 - Summary</td>
                                 </tr>
                                 <tr>
                                    <td class="details-description">The information you entered is shown below.</td>
                                 </tr>
                                 <tr>
                                    <td><h:outputText value="#{NewSpaceWizard.summary}" escape="false"/></td>
                                 </tr>
                                 <tr>
                                    <td class="details-hints">To close this wizard and create your space click Finish.</td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td width="20%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                              <table cellpadding="1" cellspacing="1" border="0" class="buttons-panel">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Finish" action="#{NewSpaceWizard.finish}" styleClass="button" />
                                    </td>
                                 </tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Back" action="#{NewSpaceWizard.back}" styleClass="button" />
                                    </td>
                                 </tr>
                                 <tr><td class="button-group-separator"></td></tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Cancel" action="#{NewSpaceWizard.cancel}" styleClass="button" />
                                    </td>
                                 </tr>
                                 <tr><td class="button-group-separator"></td></tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Minimise To Shelf" action="#{NewSpaceWizard.minimise}" 
                                                        styleClass="long-button" />
                                    </td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "blue"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>
          </td>
       </tr>
    </table>
    
    </h:form>
    
</f:view>
