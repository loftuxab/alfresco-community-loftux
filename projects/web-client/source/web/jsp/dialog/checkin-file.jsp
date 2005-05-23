<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page isELIgnored="false" %>

<%@ page import="org.alfresco.web.ui.common.PanelGenerator" %>
<%@ page import="org.alfresco.web.app.portlet.AlfrescoFacesPortlet" %>
<%@ page import="org.alfresco.web.bean.CheckinCheckoutBean" %>

<r:page>

<script language="JavaScript1.2" src="<%=request.getContextPath()%>/scripts/menu.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <%-- load a bundle of properties with I18N strings --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <%-- set the form name here --%>
   <h:form id="checkin-file1">
   
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
                              <h:graphicImage id="wizard-logo" url="/images/icons/CheckIn.gif" />
                           </td>
                           <td>
                              <div class="mainSubTitle"><h:outputText value="#{NavigationBean.nodeProperties.name}" /></div>
                              <div class="mainTitle">Check In '<h:outputText value="#{CheckinCheckoutBean.document.name}" />'</div>
                              <div class="mainSubText">Current version created by Linton Baddeley at 11:01pm on 12th May 2005</div>
                              <div class="mainSubText">Current status is 'draft'.</div>
                              <div class="mainSubText">Use this page to check in your working copy for other team members to work with.</div>
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
                              <table cellpadding="2" cellspacing="2" border="0" width="100%">
                                 <tr>
                                    <td class="wizardSectionHeading">Working copy location</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <h:selectOneRadio value="current" layout="pageDirection">
                                          <f:selectItem itemValue="current" itemLabel="Use copy in current space" />
                                          <f:selectItem itemValue="other" itemLabel="Locate copy on my computer" />
                                       </h:selectOneRadio>
                                    </td>
                                 </tr>
                                 
                                 </h:form>
                                 
                                 <r:uploadForm>
                                 <tr>
                                    <td>
                                       Location:<input style="margin-left:12px;" type="file" size="50" name="file"/>
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="mainSubText">Click upload</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <input style="margin-left:12px;" type="submit" value="Upload" />
                                    </td>
                                 </tr>
                                 </r:uploadForm>
                                 
                                 <h:form id="checkin-file2">
                                 <tr><td class="paddingRow"></td></tr>
                                 <%
                                 CheckinCheckoutBean bean = (CheckinCheckoutBean)session.getAttribute(AlfrescoFacesPortlet.MANAGED_BEAN_PREFIX + "CheckinCheckoutBean");
                                 if (bean == null)
                                 {
                                 	bean = (CheckinCheckoutBean)session.getAttribute("CheckinCheckoutBean");
                                 }
                                 if (bean != null && bean.getFileName() != null) {
                                 %>
                                    <tr>
                                       <td>
                                          <img alt="Information icon" align="absmiddle" src="<%=request.getContextPath()%>/images/icons/info_icon.gif" />
                                          The file "<%=bean.getFileName()%>" was uploaded successfully.
                                       </td>
                                    </tr>
                                 <% } %>
                                 
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="wizardSectionHeading">Check In options</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       Version Notes
                                    </td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <h:inputTextarea id="notes" rows="2" cols="50"/>
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>
                                       <h:selectBooleanCheckbox value="#{CheckinCheckoutBean.keepCheckedOut}" />
                                       <span style="vertical-align:20%">Check in changes and keep file checked out</span>
                                    </td>
                                 </tr>
                              </table>
                              <% PanelGenerator.generatePanelEnd(out, request.getContextPath(), "white"); %>
                           </td>
                           
                           <td width="100%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#cddbe8"); %>
                              <table cellpadding="1" cellspacing="1" border="0">
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Check In" action="#{CheckinCheckoutBean.checkinFileOK}" styleClass="dialogControls" />
                                    </td>
                                 </tr>
                                 <tr><td class="dialogButtonSpacing"></td></tr>
                                 <tr>
                                    <td align="center">
                                       <h:commandButton value="Cancel" action="#{CheckinCheckoutBean.cancel}" styleClass="dialogControls" />
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
                     <h:messages globalOnly="true" styleClass="errorMessage" />
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