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
                  <td bgcolor="#EEEEEE">
                  
                     <%-- Status and Actions inner contents table --%>
                     <%-- Generally this consists of an icon, textual summary and actions for the current object --%>
                     <table cellspacing="4" cellpadding="0" width="100%">
                        <tr valign="top">
                           <td width="32">
                              <h:graphicImage id="wizard-logo" url="/images/icons/details_large.gif" />
                           </td>
                           <td>
                              <div class="mainSubTitle"/><h:outputText value="#{BrowseBean.actionSpace.name}" /></div>
                              <div class="mainTitle">Details of '<h:outputText value="#{BrowseBean.actionSpace.name}" />'</div>
                              <div class="mainSubText"><h:outputText value="#{msg.location}" />: <r:nodePath value="#{BrowseBean.actionSpace.nodeRef}" breadcrumb="true" actionListener="#{BrowseBean.clickSpacePath}" /></div>
                              <div class="mainSubText"><h:outputText value="#{msg.spacedetails_description}" /></div>
                           </td>
                           <td bgcolor="#465F7D" width=1></td>
                           <td width=100 style="padding-left:2px">
                              <%-- Current object actions --%>
                              <h:outputText style="padding-left:20px" styleClass="mainSubTitle" value="#{msg.actions}" /><br>
                              
                              <a:actionLink value="#{msg.cut}" image="/images/icons/cut.gif" padding="4" actionListener="#{ClipboardBean.cutNode}">
                                 <f:param name="id" value="#{BrowseBean.actionSpace.id}" />
                              </a:actionLink>
                              <a:actionLink value="#{msg.copy}" image="/images/icons/copy.gif" padding="4" actionListener="#{ClipboardBean.copyNode}">
                                 <f:param name="id" value="#{BrowseBean.actionSpace.id}" />
                              </a:actionLink>
                              <a:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" padding="4" action="deleteSpace" actionListener="#{BrowseBean.setupSpaceAction}">
                                 <f:param name="id" value="#{BrowseBean.actionSpace.id}" />
                              </a:actionLink>
                              <a:menu itemSpacing="4" label="More..." image="/images/icons/more.gif" tooltip="More Actions for this Space" menuStyleClass="moreActionsMenu" style="padding-left:20px">
                                 <a:actionLink value="#{msg.create_shortcut}" image="/images/icons/shortcut.gif" actionListener="#{UserShortcutsBean.createShortcut}">
                                    <f:param name="id" value="#{BrowseBean.actionSpace.id}" />
                                 </a:actionLink>
                              </a:menu>
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
                              <a:panel label="Properties" id="properties-panel" border="white" bgcolor="white" 
                                       titleBorder="blue" titleBgcolor="#D3E6FE"
                                       progressive="true" action="editSpaceProperties" linkTooltip="Modify"
                                       actionListener="#{EditSpaceDialog.startWizardForEdit}"
                                       linkIcon="/images/icons/Change_details.gif">
                                 <r:propertySheetGrid id="space-props" value="#{BrowseBean.actionSpace}" var="spaceProps" 
                                                      columns="1" mode="view" labelStyleClass="propertiesLabel" 
                                                      externalConfig="true" />
                                 <h:messages styleClass="errorMessage" layout="table" />
                              </a:panel>
                              <br/>
                              <a:panel label="Rules" id="rules-panel" progressive="true" expanded="false"
                                       border="white" bgcolor="white" titleBorder="blue" titleBgcolor="#D3E6FE"
                                       action="manageRules" linkTooltip="Modify" linkIcon="/images/icons/Change_details.gif">
                                 <a:richList id="rulesList" viewMode="details" value="#{RulesBean.rules}" var="r"
                                          styleClass="recordSet" headerStyleClass="recordSetHeader" rowStyleClass="recordSetRow" 
                                          altRowStyleClass="recordSetRowAlt" width="100%" pageSize="10"
                                          initialSortColumn="title" initialSortDescending="true">
                        
                                    <%-- Primary column for details view mode --%>
                                    <a:column id="col1" primary="true" width="200" style="padding:2px;text-align:left">
                                       <f:facet name="header">
                                          <a:sortLink label="Title" value="title" mode="case-insensitive" styleClass="header"/>
                                       </f:facet>
                                       <h:outputText id="title" value="#{r.title}" />
                                    </a:column>
                                    
                                    <%-- Description columns --%>
                                    <a:column id="col2" style="text-align:left">
                                       <f:facet name="header">
                                          <a:sortLink label="Description" value="description" styleClass="header"/>
                                       </f:facet>
                                       <h:outputText id="description" value="#{r.description}" />
                                    </a:column>
                                    
                                    <%-- Created Date column for details view mode --%>
                                    <a:column id="col3" style="text-align:left">
                                       <f:facet name="header">
                                          <a:sortLink label="Created Date" value="createdDate" styleClass="header"/>
                                       </f:facet>
                                       <h:outputText id="createddate" value="#{r.createdDate}">
                                          <a:convertXMLDate dateStyle="long" />
                                       </h:outputText>
                                    </a:column>
                                    
                                    <%-- Modified Date column for details/icons view modes --%>
                                    <a:column id="col4" style="text-align:left">
                                       <f:facet name="header">
                                          <a:sortLink label="Modified Date" value="modifiedDate" styleClass="header"/>
                                       </f:facet>
                                       <h:outputText id="modifieddate" value="#{r.modifiedDate}">
                                          <a:convertXMLDate dateStyle="long" />
                                       </h:outputText>
                                    </a:column>
                                    
                                    <a:dataPager/>
                                 </a:richList>
                              </a:panel>
                              <br/>
                              <a:panel label="Preferences" id="preferences-panel" progressive="true" expanded="false"
                                       border="white" bgcolor="white" titleBorder="blue" titleBgcolor="#D3E6FE">
                                 <div>[TBD]</div>
                              </a:panel>
                              <br/>
                              <a:panel label="Space Members" id="members-panel" progressive="true" expanded="false"
                                       border="white" bgcolor="white" titleBorder="blue" titleBgcolor="#D3E6FE">
                                 <div>[TBD]</div>
                              </a:panel>
                              <br/>
                           </td>
                           
                           <td valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
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