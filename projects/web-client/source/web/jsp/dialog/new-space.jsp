<%--
  Copyright (C) 2005 Alfresco, Inc.

  Licensed under the GNU Lesser General Public License as
  published by the Free Software Foundation; either version
  2.1 of the License, or (at your option) any later version.
  You may obtain a copy of the License at

    http://www.gnu.org/licenses/lgpl.txt

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  either express or implied. See the License for the specific
  language governing permissions and limitations under the
  License.
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="32kb" %>
<%@ page isELIgnored="false" %>
<%@ page import="org.alfresco.web.ui.common.PanelGenerator" %>

<r:page>

<script language="JavaScript1.2">
   function checkButtonState()
   {
      if (document.getElementById("new-space:name").value.length == 0 )
      {
         document.getElementById("new-space:ok-button").disabled = true;
      }
      else
      {
         document.getElementById("new-space:ok-button").disabled = false;
      }
   }

</script>

<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="alfresco.messages" var="msg"/>
   
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
                  <td bgcolor="#EEEEEE">
                  
                     <%-- Status and Actions inner contents table --%>
                     <%-- Generally this consists of an icon, textual summary and actions for the current object --%>
                     <table cellspacing="4" cellpadding="0" width="100%">
                        <tr valign="top">
                           <td width="32">
                              <h:graphicImage id="wizard-logo" url="/images/icons/create_space_large.gif" />
                           </td>
                           <td>
                              <div class="mainSubTitle"/><h:outputText value="#{BrowseBean.actionSpace.name}" /></div>
                              <div class="mainTitle">Create Space</div>
                              <div class="mainSubText"><h:outputText value="#{msg.newspace_description}" /></div>
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
                                       <h:inputText id="name" value="#{NewSpaceDialog.name}" size="35" maxlength="1024" 
                                                    onkeyup="javascript:checkButtonState();" onchange="javascript:checkButtonState();"/>&nbsp;*
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
                                       <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
                                       <a:imagePickerRadio columns="6" spacing="4" value="#{NewSpaceDialog.icon}">
                                          <a:listItem value="space-icon-default" image="/images/icons/space-icon-default.gif" />
                                          <a:listItem value="space-icon-star" image="/images/icons/space-icon-star.gif" />
                                          <a:listItem value="space-icon-doc" image="/images/icons/space-icon-doc.gif" />
                                          <a:listItem value="space-icon-pen" image="/images/icons/space-icon-pen.gif" />
                                          <a:listItem value="space-icon-cd" image="/images/icons/space-icon-cd.gif" />
                                          <a:listItem value="space-icon-image" image="/images/icons/space-icon-image.gif" />
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
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton id="ok-button" value="Create Space" action="#{NewSpaceDialog.finish}" 
                                                        styleClass="wizardButton" disabled="true" />
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