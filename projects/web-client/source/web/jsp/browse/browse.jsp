<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<%@ page isELIgnored="false" %>

<%@ page import="org.alfresco.web.PanelGenerator" %>

<script language="JavaScript1.2" src="<%=request.getContextPath()%>/scripts/menu.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <h:form id="browse">
   
   <%-- Main outer table --%>
   <table cellspacing=0 cellpadding=2>
      
      <%-- Title bar --%>
      <tr>
         <td colspan=2>
            <%@ include file="../parts/titlebar.jsp" %>
         </td>
      </tr>
      
      <%-- Main area --%>
      <tr valign=top>
         <%-- Shelf --%>
         <td>
            <%@ include file="../parts/shelf.jsp" %>
         </td>
         
         <%-- Work Area --%>
         <td width=100%>
            <table cellspacing=0 cellpadding=0 width=100%>
               <%-- Breadcrumb --%>
               <%@ include file="../parts/breadcrumb.jsp" %>
               
               <%-- Status and Actions --%>
               <tr>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_4.gif)" width=4></td>
                  <td bgcolor="#ECE9E1">
                  
                     <%-- Status and Actions inner contents table --%>
                     <%-- Generally this consists of an icon, textual summary and actions for the current object --%>
                     <table cellspacing=4 cellpadding=0 width=100%>
                        <tr valign=top>
                           <td width=30>
                              <img src="<%=request.getContextPath()%>/images/ball_big.gif" width=26 height=27>
                           </td>
                           <td>
                              <%-- Summary --%>
                              <div class="mainSubTitle"><h:outputText value="#{msg.product_name}" /></div>
                              <div class="mainTitle"><h:outputText value="#{NavigationBean.nodeProperties.name}" /></div>
                              <div class="mainSubText"><h:outputText value="#{msg.view_description}" /></div>
                              <div class="mainSubText"><h:outputText value="#{NavigationBean.nodeProperties.description}" /></div>
                              <div class="mainSubText">There are currently 2 members of this space</div>
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=100 style="padding-left:2px">
                              <%-- Current object actions --%>
                              <h:outputText style="padding-left:20px" styleClass="mainSubTitle" value="#{msg.actions}"/><br>
                              <awc:actionLink value="#{msg.new_space}" image="/images/icons/space_small.gif" padding="4" action="createSpace" />
                              <awc:actionLink value="#{msg.delete_space}" image="/images/icons/delete.gif" padding="4" action="deleteSpace" actionListener="#{BrowseBean.spaceActionSetup}">
                                 <f:param name="id" value="#{NavigationBean.currentNodeId}" />
                              </awc:actionLink>
                              <awc:actionLink value="#{msg.add_content}" image="/images/icons/file.gif" padding="4" action="addContent" />
                              <awc:actionLink value="#{msg.invite}" image="/images/icons/invite.gif" padding="4" />
                              <%-- TODO: add real actions --%>
                              <awc:menu id="spaceMenu" itemSpacing="4" label="More..." image="/images/arrow_expanded.gif" tooltip="More Actions for this Space" menuStyleClass="moreActionsMenu" style="padding-left:20px">
                                 <awc:actionLink value="Change Details" image="/images/icons/Change_details.gif" />
                                 <awc:actionLink value="Cut" image="/images/icons/cut.gif" />
                                 <awc:actionLink value="Copy" image="/images/icons/copy.gif" />
                                 <awc:actionLink value="Paste" image="/images/icons/paste.gif" />
                              </awc:menu>
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=100>
                              <%-- Details View settings --%>
                              <h:outputText style="padding-left:26px" styleClass="mainSubTitle" value="#{msg.view}"/><br>
                              <awc:modeList itemSpacing="3" iconColumnWidth="20" selectedStyleClass="statusListHighlight"
                                    value="#{BrowseBean.browseViewMode}" actionListener="#{BrowseBean.viewModeChanged}">
                                 <awc:listItem value="details" label="List All Items" image="/images/icons/Details.gif" />
                                 <awc:listItem value="icons" label="Dashboard" />
                                 <awc:listItem value="list" label="Browse Items" />
                              </awc:modeList>
                           </td>
                        </tr>
                     </table>
                     
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_6.gif)" width=4></td>
               </tr>
               
               <%-- separator row with gradient shadow --%>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/statuspanel_7.gif" width=4 height=9></td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_8.gif)"></td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/statuspanel_9.gif" width=4 height=9></td>
               </tr>
               
               <%-- Toolbar --%>
               <tr style="padding-top:4px">
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td>
                     <table cellspacing=0 cellpadding=4>
                        <tr>
                           <td>
                              <%-- Toolbar actions --%>
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "bluetoolbar", "#E9F0F4"); %>
                                 <table cellspacing=0 cellpadding=0><tr>
                                    <td><awc:actionLink value="#{msg.cut}" image="/images/icons/cut.gif" showLink="false" /></td><td>&nbsp;|&nbsp;</td>
                                    <td><awc:actionLink value="#{msg.copy}" image="/images/icons/copy.gif" showLink="false" /></td><td>&nbsp;|&nbsp;</td>
                                    <td><awc:actionLink value="#{msg.paste}" image="/images/icons/paste.gif" showLink="false" /></td><td>&nbsp;|&nbsp;</td>
                                    <td><awc:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" showLink="false" /></td>
                                 </tr></table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "bluetoolbar"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <%-- Details - Spaces --%>
               <tr valign=top>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td>
                     <div style="padding:4px">
                     
                     <awc:panel id="spaces-panel" border="white" styleClass="mainSubTitle" label="#{msg.browse_spaces}">
                     
                     <%-- Browse - details/icons mode --%>
                     <awc:richList id="detailsList" rendered="#{BrowseBean.browseViewMode == 'details' || BrowseBean.browseViewMode == 'icons'}" viewMode="#{BrowseBean.browseViewMode}" pageSize="10" style="padding:2px" rowStyleClass="recordSetRow" altRowStyleClass="recordSetRowAlt"
                           width="100%" value="#{BrowseBean.nodes}" var="r" initialSortColumn="name" initialSortDescending="true">
                        
                        <awc:column primary="true" width="200" style="padding:2px;text-align:left">
                           <f:facet name="header">
                              <awc:sortLink label="Name" value="name" mode="case-insensitive" styleClass="header"/>
                           </f:facet>
                           <f:facet name="large-icon">
                              <awc:actionLink value="#{r.name}" image="/images/icons/folder_large.png" actionListener="#{BrowseBean.clickSpace}" showLink="false">
                                 <f:param name="id" value="#{r.id}" />
                              </awc:actionLink>
                           </f:facet>
                           <f:facet name="small-icon">
                              <awc:actionLink value="#{r.name}" image="/images/icons/folder.gif" actionListener="#{BrowseBean.clickSpace}" showLink="false">
                                 <f:param name="id" value="#{r.id}" />
                              </awc:actionLink>
                           </f:facet>
                           <awc:actionLink value="#{r.name}" actionListener="#{BrowseBean.clickSpace}">
                              <f:param name="id" value="#{r.id}" />
                           </awc:actionLink>
                        </awc:column>
                        
                        <awc:column style="text-align:left">
                           <f:facet name="header">
                              <awc:sortLink label="Description" value="description" styleClass="header"/>
                           </f:facet>
                           <h:outputText value="#{r.description}" />
                        </awc:column>
                        
                        <awc:column style="text-align:left">
                           <f:facet name="header">
                              <awc:sortLink label="Created Date" value="createddate" styleClass="header"/>
                           </f:facet>
                           <h:outputText value="#{r.createddate}">
                              <f:convertDateTime dateStyle="long" />
                           </h:outputText>
                        </awc:column>
                        
                        <awc:column style="text-align:left">
                           <f:facet name="header">
                              <awc:sortLink label="Modified Date" value="modifieddate" styleClass="header"/>
                           </f:facet>
                           <h:outputText value="#{r.modifieddate}">
                              <f:convertDateTime dateStyle="long" />
                           </h:outputText>
                        </awc:column>
                        
                        <awc:column actions="true" style="text-align:left">
                           <f:facet name="header">
                              <h:outputText value="#{msg.actions}"/>
                           </f:facet>
                           <awc:actionLink value="#{msg.edit}" image="/images/icons/edit_icon.gif" showLink="false" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.cut}" image="/images/icons/cut.gif" showLink="false" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.copy}" image="/images/icons/copy.gif" showLink="false" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" showLink="false" styleClass="inlineAction" action="deleteSpace" actionListener="#{BrowseBean.spaceActionSetup}">
                              <f:param name="id" value="#{r.id}" />
                           </awc:actionLink>
                           <%-- TODO: add real actions --%>
                           <awc:menu id="actionsMenu1" itemSpacing="4" image="/images/arrow_expanded.gif" tooltip="More Actions" menuStyleClass="moreActionsMenu">
                              <awc:actionLink value="Action 001" image="/images/icons/Change_details.gif" />
                              <awc:actionLink value="Action 001" image="/images/icons/Change_details.gif" />
                              <awc:actionLink value="Action 001" image="/images/icons/Change_details.gif" />
                           </awc:menu>
                        </awc:column>
                        
                        <awc:dataPager/>
                     </awc:richList>
                     
                     <%-- Browse - list mode --%>
                     <awc:richList id="browseList" viewMode="list" rendered="#{BrowseBean.browseViewMode == 'list'}" pageSize="5" style="padding:2px" rowStyleClass="recordSetRowAlt"
                           width="100%" value="#{BrowseBean.nodes}" var="r" initialSortColumn="name" initialSortDescending="true">
                        
                        <awc:column primary="true" style="padding:2px;text-align:left">
                           <f:facet name="small-icon">
                              <awc:actionLink value="#{r.name}" image="/images/icons/folder.gif" actionListener="#{BrowseBean.clickSpace}" showLink="false">
                                 <f:param name="id" value="#{r.id}" />
                              </awc:actionLink>
                           </f:facet>
                           <awc:actionLink value="#{r.name}" actionListener="#{BrowseBean.clickSpace}" styleClass="title">
                              <f:param name="id" value="#{r.id}" />
                           </awc:actionLink>
                        </awc:column>
                        
                        <awc:column style="text-align:left">
                           <h:outputText value="#{r.description}" />
                        </awc:column>
                        
                        <%--<awc:column style="text-align:left;">
                           <h:outputText value="Modified Date:" />
                           <h:outputText value="#{r.modifieddate}">
                              <f:convertDateTime dateStyle="long" />
                           </h:outputText>
                        </awc:column>--%>
                        
                        <awc:column style="text-align:left">
                           <awc:nodeDescendants value="#{r.nodeRef}" styleClass="header" actionListener="#{BrowseBean.clickDescendantSpace}" />
                        </awc:column>
                        
                        <awc:column actions="true" style="text-align:right">
                           <awc:actionLink value="#{msg.edit}" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.cut}" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.copy}" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.delete}" styleClass="inlineAction" action="deleteSpace" actionListener="#{BrowseBean.spaceActionSetup}">
                              <f:param name="id" value="#{r.id}" />
                           </awc:actionLink>
                           <%-- TODO: add real actions --%>
                           <awc:menu id="actionsMenu2" itemSpacing="4" label="More..." image="/images/arrow_expanded.gif" tooltip="More Actions" menuStyleClass="moreActionsMenu">
                              <awc:actionLink value="Action 001" image="/images/icons/Change_details.gif" />
                              <awc:actionLink value="Action 001" image="/images/icons/Change_details.gif" />
                              <awc:actionLink value="Action 001" image="/images/icons/Change_details.gif" />
                           </awc:menu>
                        </awc:column>
                        
                        <awc:dataPager/>
                     </awc:richList>
                     
                     </awc:panel>
                     
                     <div>
                     
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <%-- Details - Content --%>
               <tr valign=top>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td>
                     <div style="padding:4px">
                     
                     <awc:panel id="content-panel" border="white" styleClass="mainSubTitle" label="#{msg.browse_content}">
                        <p>
                     </awc:panel>
                     
                     </div>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <%-- Error Messages --%>
               <tr valign=top>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td>
                     <%-- messages tag to show messages not handled by other specific message tags --%>
                     <h:messages globalOnly="true" styleClass="errorMessage" />
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <%-- separator row with bottom panel graphics --%>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_7.gif" width=4 height=4></td>
                  <td width=100% align=center style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_8.gif)"></td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_9.gif" width=4 height=4></td>
               </tr>
               
            </table>
          </td>
       </tr>
    </table>
    
    <%-- TEMP! --%>
    <p>
    <h:commandButton id="show-zoo-page" value="Show Zoo" action="showZoo" />
    
    </h:form>
    
</f:view>
