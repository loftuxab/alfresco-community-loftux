<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<%@ page import="com.activiti.web.PanelGenerator"%>

<%@ page isELIgnored="false" %>

<script language="JavaScript1.2" src="<%=request.getContextPath()%>/scripts/menu.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <%-- Main outer table --%>
   <table cellspacing=0 cellpadding=4 style="border:solid #EEEEEE;border-width:1px">
      
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
                     Toolbar
                  </td>
               </tr>
               <%-- Content --%>
               <tr>
                  <td>
                     <table cellspacing="5" cellpadding="5" border="1" width="100%">
                        <tr>
                           <td width="20%">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table>
                                 <tr><td>Steps</td></tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td width="60%">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table>
                                 <tr><td>Details</td></tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td width="40%">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "white", "white"); %>
                              <table>
                                 <tr><td>Buttons</td></tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>
          </td>
       </tr>
    </table>
    
    <br/><br/><br/>
    <h:form id="navForm">
       <h:commandButton id="show-jump-page" value="Back To Jump Page" action="jumppage" />
   </h:form>
    
</f:view>
