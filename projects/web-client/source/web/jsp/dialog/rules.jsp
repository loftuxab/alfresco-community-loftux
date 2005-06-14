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
                              <div class="mainSubTitle"/><h:outputText value='#{NavigationBean.nodeProperties["name"]}' /></div>
                              <div class="mainTitle">Content Rules</div>
                              <div class="mainSubText">This view shows you all the rules to be applied to content in this space.</div>
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=110 style="padding-left:2px">
                              <%-- Current object actions --%>
                              <h:outputText style="padding-left:20px;" styleClass="mainSubTitle" value="#{msg.actions}" /><br/>
                              <a:actionLink value="#{msg.create_rule}" image="/images/icons/space_small.gif" padding="4" action="createRule" actionListener="#{NewRuleWizard.startWizard}" />
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=110 style="padding-left:2px">
                              <%-- Filters --%>
                              <h:outputText style="padding-left:26px;padding-bottom:4px;" styleClass="mainSubTitle" value="#{msg.filter_contents}" /><br/>
                              <a:modeList itemSpacing="3" iconColumnWidth="20" selectedStyleClass="statusListHighlight"
                                    value="#{RulesBean.viewMode}" actionListener="#{RulesBean.viewModeChanged}"
                                    selectedImage="/images/icons/Details.gif">
                                 <a:listItem value="local" label="Local" />
                                 <a:listItem value="inherited" label="Inherited [TBD]" />
                              </a:modeList>
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
               
               <%-- Toolbar --%>
               <%-- NOTE: removed toolbar until multi-select implemented
               <tr style="padding-top:4px">
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td>
                     <table cellspacing=0 cellpadding=4>
                        <tr>
                           <td>
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "bluetoolbar", "#E9F0F4"); %>
                                 <table cellspacing=0 cellpadding=0><tr>
                                    <td><a:actionLink value="#{msg.cut}" image="/images/icons/cut.gif" showLink="false" /></td><td>&nbsp;|&nbsp;</td>
                                    <td><a:actionLink value="#{msg.copy}" image="/images/icons/copy.gif" showLink="false" /></td><td>&nbsp;|&nbsp;</td>
                                    <td><a:actionLink value="#{msg.paste}" image="/images/icons/paste.gif" showLink="false" /></td><td>&nbsp;|&nbsp;</td>
                                    <td><a:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" showLink="false" /></td>
                                 </tr></table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "bluetoolbar"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               --%>
               
               <%-- Details --%>
               <tr valign=top>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width="4"></td>
                  <td>
                     <table cellspacing="0" cellpadding="3" border="0" width="100%">
                        <tr>
                           <td width="100%" valign="top">
                              <a:richList id="rulesList" viewMode="details" value="#{RulesBean.rules}" var="r"
                                          styleClass="recordSet" headerStyleClass="recordSetHeader" rowStyleClass="recordSetRow" 
                                          altRowStyleClass="recordSetRowAlt" width="100%" pageSize="10"
                                          initialSortColumn="title" initialSortDescending="true">
                        
                                 <%-- Primary column for details view mode --%>
                                 <a:column primary="true" width="200" style="padding:2px;text-align:left">
                                    <f:facet name="header">
                                       <a:sortLink label="Title" value="title" mode="case-insensitive" styleClass="header"/>
                                    </f:facet>
                                    <f:facet name="small-icon">
                                       <a:actionLink value="#{r.title}" image="/images/icons/space_small.gif" actionListener="#{RulesBean.clickRule}" showLink="false">
                                          <f:param name="id" value="#{r.id}" />
                                       </a:actionLink>
                                    </f:facet>
                                    <a:actionLink value="#{r.title}" actionListener="#{RulesBean.clickRule}">
                                       <f:param name="id" value="#{r.id}" />
                                    </a:actionLink>
                                 </a:column>
                                 
                                 <%-- Description column --%>
                                 <a:column style="text-align:left">
                                    <f:facet name="header">
                                       <a:sortLink label="Description" value="description" styleClass="header"/>
                                    </f:facet>
                                    <h:outputText value="#{r.description}" />
                                 </a:column>
                                 
                                 <%-- Created Date column for details view mode --%>
                                 <a:column style="text-align:left">
                                    <f:facet name="header">
                                       <a:sortLink label="Created Date" value="createdDate" styleClass="header"/>
                                    </f:facet>
                                    <h:outputText value="#{r.createdDate}">
                                       <a:convertXMLDate dateStyle="long" />
                                    </h:outputText>
                                 </a:column>
                                 
                                 <%-- Modified Date column for details/icons view modes --%>
                                 <a:column style="text-align:left">
                                    <f:facet name="header">
                                       <a:sortLink label="Modified Date" value="modifiedDate" styleClass="header"/>
                                    </f:facet>
                                    <h:outputText value="#{r.modifiedDate}">
                                       <a:convertXMLDate dateStyle="long" />
                                    </h:outputText>
                                 </a:column>
                                 
                                 <%-- Actions column --%>
                                 <a:column actions="true" style="text-align:left">
                                    <f:facet name="header">
                                       <h:outputText value="#{msg.actions}"/>
                                    </f:facet>
                                    <a:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" showLink="false" styleClass="inlineAction">
                                       <f:param name="id" value="#{r.id}" />
                                    </a:actionLink>
                                    <a:actionLink value="#{msg.change_details}" image="/images/icons/Change_details.gif" showLink="false" styleClass="inlineAction">
                                       <f:param name="id" value="#{r.id}" />
                                    </a:actionLink>
                                 </a:column>
                              </a:richList>
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