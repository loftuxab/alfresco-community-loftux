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
   <f:loadBundle basename="messages" var="msg"/>
   
   <%-- set the form name here --%>
   <h:form id="advsearch">
   
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
                           <td width=32>
                              <img src="<%=request.getContextPath()%>/images/icons/search_large.gif" width=32 height=32>
                           </td>
                           <td>
                              <div class="mainSubTitle"/><h:outputText value='#{NavigationBean.nodeProperties.name}' /></div>
                              <div class="mainTitle"><h:outputText value="#{msg.advanced_search}" /></div>
                              <div class="mainSubText"><h:outputText value="#{msg.advancedsearch_description}" /></div>
                           </td>
                           <td bgcolor="#465F7D" width=1></td>
                           <td width=80 style="padding-left:2px">
                              <%-- Current object actions --%>
                              <h:outputText style="padding-left:20px" styleClass="mainSubTitle" value="#{msg.actions}" /><br>
                              <a:actionLink value="#{msg.resetall}" image="/images/icons/delete.gif" padding="4" actionListener="#{AdvancedSearchBean.reset}" />
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
                              
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table cellpadding="2" cellspacing="2" border="0">
                                 
                                 <tr>
                                    <td>Look for:&nbsp;<h:inputText value="#{AdvancedSearchBean.text}" size="35" maxlength="255" />&nbsp;*</td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>Look in:</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <h:selectOneRadio value="#{AdvancedSearchBean.lookin}" layout="pageDirection">
                                          <f:selectItem itemValue="all" itemLabel="All Spaces" />
                                          <f:selectItem itemValue="other" itemLabel="Specify Space:" />
                                       </h:selectOneRadio>
                                       <%-- TODO: just this space, or children checkbox --%>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td style="padding-left:26px">
                                       <r:spaceSelector label="Click here to select a Space" value="#{AdvancedSearchBean.location}" initialSelection="#{NavigationBean.currentNodeId}" style="border: 1px dashed #cccccc; padding: 2px;"/>
                                    </td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>Show me results for:</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <h:selectOneRadio value="#{AdvancedSearchBean.mode}" layout="pageDirection">
                                          <f:selectItem itemValue="all" itemLabel="All Items" />
                                          <f:selectItem itemValue="files_text" itemLabel="File names and contents" />
                                          <f:selectItem itemValue="files" itemLabel="File names only" />
                                          <f:selectItem itemValue="folders" itemLabel="Space names only" />
                                       </h:selectOneRadio>
                                    </td>
                                 </tr>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>Show me results in the categories:</td>
                                 </tr>
                                 <tr>
                                    <td style="padding-left:8px">
                                       <r:categorySelector label="Click here to select a Category" value="#{AdvancedSearchBean.category}" style="border: 1px dashed #cccccc; padding: 2px;"/>
                                    </td>
                                 </tr>
                                 
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Search" action="#{AdvancedSearchBean.search}" styleClass="wizardButton" />
                                    </td>
                                 </tr>
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
               
               <%-- Error Messages --%>
               <tr valign="top">
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width="4"></td>
                  <td>
                     <%-- messages tag to show messages not handled by other specific message tags --%>
                     <h:messages globalOnly="true" styleClass="errorMessage" layout="table" />
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