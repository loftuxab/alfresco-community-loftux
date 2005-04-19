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
   
   <%-- Main outer table --%>
   <table cellspacing=0 cellpadding=2>
      
      <%-- Title bar area --%>
      <tr>
         <td colspan=2>
            <table cellspacing=0 cellpadding=2 width=100%>
               <tr>
                  <%-- Top level toolbar and company logo area --%>
                  <td width=100%>
                     <table cellspacing=0 cellpadding=0 border=0 width=100%>
                        <tr>
                           <td><img src="<%=request.getContextPath()%>/images/parts/titlebar_begin.gif" width=28 height=30></td>
                           <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/titlebar_bg.gif)">
                              ...toolbar modes etc. go here...
                           </td>
                           <td><img src="<%=request.getContextPath()%>/images/parts/titlebar_end.gif" width=4 height=30></td>
                        </tr>
                     </table>
                  </td>
                  
                  <%-- Help area --%>
                  <td>
                     <table cellspacing=2 cellpadding=0 border=0 width=100%>
                        <tr>
                           <td><img src="<%=request.getContextPath()%>/images/icons/shelf.gif" width=16 height=16></td>
                           <td width=8>&nbsp;</td>
                           <td><img src="<%=request.getContextPath()%>/images/icons/Help_icon.gif" width=15 height=15></td>
                           <td><h:outputText value="#{msg.help}"/></td>
                        </tr>
                     </table>
                  </td>
                  
                  <%-- Search area --%>
                  <td>
                     <table cellspacing=0 cellpadding=0 border=0 width=100%>
                        <tr>
                           <td><img src="<%=request.getContextPath()%>/images/parts/searchbar_begin.gif" width=4 height=30></td>
                           <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/searchbar_bg.gif)">
                              <span style="white-space:nowrap">
                                 <img src="<%=request.getContextPath()%>/images/icons/search_controls.gif" width=27 height=15 style="padding-top:4px">
                                 &nbsp;
                                 <h:inputText styleClass="userInputForm" id="search" maxlength="255" style="width:90px" />
                              </span>
                           </td>
                           <td><img src="<%=request.getContextPath()%>/images/parts/searchbar_end.gif" width=4 height=30></td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>
         </td>
      </tr>
      
      <%-- Main area --%>
      <tr>
         <%-- Shelf area --%>
         <td valign=top>
            <table cellspacing=0 cellpadding=0 border=0 width=100% bgcolor='#ffffff'>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/headbar_begin.gif" width=4 height=33></td>
                  <td align=center width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_bg.gif)">
                     <div class="headbarTitle"><h:outputText value="#{msg.shelf}"/></div>
                  </td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/headbar_end.gif" width=4 height=33></td>
               </tr>
               <tr>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td height=425 valign=top>
                     
                     <%-- TODO: Shelf components --%>
                     <img src="<%=request.getContextPath()%>/images/test/shelf.png">
                     
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_7.gif" width=4 height=4></td>
                  <td width=100% align=center style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_8.gif)"></td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_9.gif" width=4 height=4></td>
               </tr>
            </table>
         </td>
         
         <%-- Work area --%>
         <td width=100% valign=top>
            <table cellspacing=0 cellpadding=0 width=100%>
               <%-- Breadcrumb --%>
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/headbar_begin.gif" width=4 height=33></td>
                  <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_bg.gif)">
                     <%-- TODO: Breadcrumb text --%>
                     <div style="padding-left:8px" class="headbarTitle"><h:outputText value="#{msg.location}"/>:&nbsp;<h:outputText styleClass="headbar" value="My Home"/></div>
                  </td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/headbar_end.gif" width=4 height=33></td>
               </tr>
               
               <%-- Status and Actions --%>
               <tr>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_4.gif)" width=4></td>
                  <td bgcolor="#ECE9E1">
                     <table cellspacing=0 cellpadding=4>
                        <tr>
                           <td valign=top width=26>
                              <img src="<%=request.getContextPath()%>/images/ball_big.gif" width=26 height=27>
                           </td>
                           <td>
                              <p class="status">
                                 Status and actions.
                              </p>
                              <p class="status">
                                 Several text lines will probably appear here, some will be quite long.
                              </p>
                              <p class="status">
                                 So let's have some more text.
                              </p>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/statuspanel_6.gif)" width=4></td>
               </tr>
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
                              <%-- TODO: Toolbar actions --%>
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "bluetoolbar", "#E9F0F4"); %>
                                 [Toolbar Buttons]
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "bluetoolbar"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <%-- Details --%>
               <tr>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
                  <td valign=top height=350>
                     ...Details...
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
               </tr>
               
               <tr>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_7.gif" width=4 height=4></td>
                  <td width=100% align=center style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_8.gif)"></td>
                  <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_9.gif" width=4 height=4></td>
               </tr>
               
            </table>
          </td>
       </tr>
    </table>
    
</f:view>
