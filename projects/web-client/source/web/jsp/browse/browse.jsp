<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<%@ page isELIgnored="false" %>

<%@ page import="com.activiti.web.PanelGenerator" %>

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
                           <td width=26>
                              <img src="<%=request.getContextPath()%>/images/ball_big.gif" width=26 height=27>
                           </td>
                           <td>
                              <%-- Summary --%>
                              <p class="status">
                                 Status and actions.
                              </p>
                              <p class="status">
                                 Several text lines will probably appear here, some will be quite long. And maybe really long, it's difficult to say really at the start of things.
                              </p>
                              <p class="status">
                                 Let's have some more text. And more.
                              </p>
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=100 style="padding-left:2px">
                              <%-- Current object actions --%>
                              <h:outputText style="padding-left:20px" styleClass="mainSubTitle" value="#{msg.actions}"/><br>
                              <awc:actionLink value="#{msg.new_space}" image="/images/icons/space_small.gif" padding="4" action="createSpace" />
                              <awc:actionLink value="#{msg.new_space}" image="/images/icons/space_small.gif" padding="4" action="createSpace" />
                              <awc:actionLink value="#{msg.new_space}" image="/images/icons/space_small.gif" padding="4" action="createSpace" />
                           </td>
                           <td bgcolor="#495F69" width=1></td>
                           <td width=100>
                              <%-- Details View settings --%>
                              <h:outputText style="padding-left:26px" styleClass="mainSubTitle" value="#{msg.view}"/><br>
                              <awc:modeList itemSpacing="3" iconColumnWidth="20" selectedStyleClass="statusListHighlight"
                                    value="details" actionListener="#{BrowseBean.viewModeChanged}">
                                 <awc:modeListItem value="details" label="List All Items" image="/images/icons/Details.gif" />
                                 <awc:modeListItem value="list" label="Dashboard" />
                                 <awc:modeListItem value="icons" label="Browse Items" />
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
                                    <td><awc:actionLink value="#{msg.cut}" image="/images/icons/cut.gif" showLink="false"/></td><td>&nbsp;|&nbsp;</td>
                                    <td><awc:actionLink value="#{msg.copy}" image="/images/icons/copy.gif" showLink="false"/></td><td>&nbsp;|&nbsp;</td>
                                    <td><awc:actionLink value="#{msg.paste}" image="/images/icons/paste.gif" showLink="false"/></td><td>&nbsp;|&nbsp;</td>
                                    <td><awc:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" showLink="false"/></td>
                                 </tr></table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "bluetoolbar"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <%-- Details --%>
               <tr valign=top>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td>
                     
                     <%-- Details inner components --%>
                     <awc:richList id="browseList" viewMode="#{BrowseBean.browseViewMode}" pageSize="10" style="padding:2px" rowStyleClass="recordSetRow" altRowStyleClass="recordSetRowAlt"
                           width="100%" value="#{BrowseBean.nodes}" var="r" initialSortColumn="name" initialSortDescending="true">
                        
                        <awc:column primary="true" width="175" style="padding:2px;text-align:left;">
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
                        
                        <awc:column style="text-align:left;">
                           <f:facet name="header">
                              <awc:sortLink label="Description" value="description" styleClass="header"/>
                           </f:facet>
                           <h:outputText value="#{r.description}" />
                        </awc:column>
                        
                        <awc:column style="text-align:left;">
                           <f:facet name="header">
                              <awc:sortLink label="Created Date" value="createddate" styleClass="header"/>
                           </f:facet>
                           <h:outputText value="#{r.createddate}">
                              <f:convertDateTime dateStyle="long" />
                           </h:outputText>
                        </awc:column>
                        
                        <awc:column style="text-align:left;">
                           <f:facet name="header">
                              <awc:sortLink label="Modified Date" value="modifieddate" styleClass="header"/>
                           </f:facet>
                           <h:outputText value="#{r.modifieddate}">
                              <f:convertDateTime dateStyle="long" />
                           </h:outputText>
                        </awc:column>
                        
                        <awc:column actions="true" style="text-align:left;">
                           <f:facet name="header">
                              <h:outputText value="#{msg.actions}"/>
                           </f:facet>
                           <awc:actionLink value="#{msg.edit}" image="/images/icons/edit_icon.gif" showLink="false" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.cut}" image="/images/icons/cut.gif" showLink="false" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.copy}" image="/images/icons/copy.gif" showLink="false" styleClass="inlineAction" />
                           <awc:actionLink value="#{msg.delete}" image="/images/icons/delete.gif" showLink="false" styleClass="inlineAction" />
                        </awc:column>
                        
                        <awc:dataPager/>
                     </awc:richList>
                     
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
    <h:commandButton id="show-jump-page" value="Back To Jump Page" action="jumppage" />
    
    </h:form>
    
</f:view>
