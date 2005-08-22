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

<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="alfresco.messages" var="msg"/>
   
   <h:form id="invite-users">
   
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
                  <td bgcolor="#EEEEEE">
                  
                     <%-- Status and Actions inner contents table --%>
                     <%-- Generally this consists of an icon, textual summary and actions for the current object --%>
                     <table cellspacing="4" cellpadding="0" width="100%">
                        <tr valign="top">
                           <td width="32">
                              <h:graphicImage id="wizard-logo" url="/images/icons/users_large.gif" />
                           </td>
                           <td>
                              <div class="mainSubTitle"><h:outputText value='#{NavigationBean.nodeProperties["name"]}' /></div>
                              <div class="mainTitle"><h:outputText value="#{InviteUsersWizard.wizardTitle}" /></div>
                              <div class="mainSubText"><h:outputText value="#{InviteUsersWizard.wizardDescription}" /></div>
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
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
                              <h:outputText styleClass="mainSubTitle" value="Steps"/><br>
                              <a:modeList itemSpacing="3" iconColumnWidth="2" selectedStyleClass="statusListHighlight"
                                    value="1" disabled="true">
                                 <a:listItem value="1" label="1. #{msg.invite_step_1}" />
                                 <a:listItem value="2" label="2. #{msg.invite_step_2}" />
                              </a:modeList>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "blue"); %>
                           </td>
                           
                           <td width="100%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table cellpadding="2" cellspacing="2" border="0" width="100%">
                                 <tr>
                                    <td class="mainSubTitle"><h:outputText value="#{InviteUsersWizard.stepTitle}" /></td>
                                 </tr>
                                 <tr>
                                    <td class="mainSubText"><h:outputText value="#{InviteUsersWizard.stepDescription}" /></td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td><h:outputText value="#{msg.i_want_to}" /></td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <h:selectOneRadio value="#{InviteUsersWizard.invite}" layout="pageDirection">
                                          <f:selectItem itemValue="all" itemLabel="#{msg.invite_all}" />
                                          <f:selectItem itemValue="users" itemLabel="#{msg.invite_users}" />
                                       </h:selectOneRadio>
                                    </td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="mainSubTitle"><h:outputText value="#{msg.specify_usersgroups}" /></td>
                                 </tr>
                                 <tr>
                                    <td class="mainSubText"><h:outputText value="1. #{msg.select_usersgroups}" /></td>
                                 </tr>
                                 <tr>
                                    <%-- Picker to select Users/Groups --%>
                                    <td><a:genericPicker id="picker" showAddButton="false" filters="#{InviteUsersWizard.filters}" queryCallback="#{InviteUsersWizard.pickerCallback}" /></td>
                                 </tr>
                                 <tr>
                                    <td><h:outputText value="#{msg.role}" /></td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <div style="padding:4px">
                                          <h:selectOneListbox id="roles" style="width:250px" size="4">
                                             <f:selectItems value="#{InviteUsersWizard.roles}" />
                                          </h:selectOneListbox>
                                       </div>
                                    </td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="mainSubText"><h:outputText value="2. #{msg.click_add}" /></td>
                                 </tr>
                                 <tr>
                                    <td><h:commandButton value="#{msg.add}" actionListener="#{InviteUsersWizard.addSelection}" styleClass="wizardButton" /></td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="mainSubText"><h:outputText value="3. #{msg.selected_usersgroups}" /></td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <table border=0 cellspacing=4 cellpadding=0>
                                          <tr valign="top">
                                             <td>
                                                <h:selectManyListbox id="selection" style="width:250px" size="4">
                                                   <f:selectItems value="#{InviteUsersWizard.selectedItems}" />
                                                </h:selectManyListbox>
                                             </td>
                                             <td>
                                                <h:commandButton value="#{msg.remove}" actionListener="#{InviteUsersWizard.removeSelection}" styleClass="wizardButton" />
                                             </td>
                                          </tr>
                                       </table>
                                    </td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td><h:outputText value="#{InviteUsersWizard.stepInstructions}" /></td>
                                 </tr> 
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="#{msg.next}" action="#{InviteUsersWizard.next}" styleClass="wizardButton" />
                                    </td>
                                 </tr>
                                 <tr><td class="wizardButtonSpacing"></td></tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="#{msg.cancel}" action="#{InviteUsersWizard.cancel}" styleClass="wizardButton" />
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