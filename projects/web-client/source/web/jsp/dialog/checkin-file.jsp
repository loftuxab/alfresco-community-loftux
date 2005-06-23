<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="32kb" %>
<%@ page isELIgnored="false" %>
<%@ page import="org.alfresco.web.ui.common.PanelGenerator" %>
<%@ page import="org.alfresco.web.app.portlet.AlfrescoFacesPortlet" %>
<%@ page import="org.alfresco.web.bean.CheckinCheckoutBean" %>

<r:page>

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
                  <td bgcolor="#EEEEEE">
                  
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
                              <div class="mainSubText"><h:outputText value="#{msg.checkinfile_description}" /></div>
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
                              <table cellpadding="2" cellspacing="2" border="0" width="100%">
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
                                       <h:inputTextarea value="#{CheckinCheckoutBean.versionNotes}" rows="2" cols="50" />
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>
                                       <h:selectBooleanCheckbox value="#{CheckinCheckoutBean.keepCheckedOut}" />
                                       <span style="vertical-align:20%">Check in changes and keep file checked out</span>
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="wizardSectionHeading">Working copy location</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       Which copy do you want to check-in?
                                    </td>
                                 </tr>
                                 
                                 <tr>
                                    <td>
                                       <h:selectOneRadio value="#{CheckinCheckoutBean.copyLocation}" layout="pageDirection">
                                          <f:selectItem itemValue="current" itemDisabled="#{CheckinCheckoutBean.fileName != null}" itemLabel="Use copy in current space" />
                                          <f:selectItem itemValue="other" itemLabel="Use copy uploaded from my computer" />
                                       </h:selectOneRadio>
                                    </td>
                                 </tr>
                              </table>
                              
                           </td>
                           
                           <td width="100%" valign="top">
                              <% PanelGenerator.generatePanelStart(out, request.getContextPath(), "blue", "#D3E6FE"); %>
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
                        
                        </h:form>

                        <tr>
                           <td style="padding-left: 32px;">
                              <r:uploadForm>
                              <table>
                                 <tr>
                                    <td>1. Locate document to upload</td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td>
                                       Location:<input style="margin-left:12px;" type="file" size="50" name="alfFileInput"/>
                                    </td>
                                 </tr>
                                 <tr><td class="paddingRow"></td></tr>
                                 <tr>
                                    <td class="mainSubText">2. Click upload</td>
                                 </tr>
                                 <tr>
                                    <td>
                                       <input style="margin-left:12px;" type="submit" value="Upload" />
                                    </td>
                                 </tr>
                                 <%
                                 CheckinCheckoutBean bean = (CheckinCheckoutBean)session.getAttribute(AlfrescoFacesPortlet.MANAGED_BEAN_PREFIX + "CheckinCheckoutBean");
                                 if (bean == null)
                                 {
                                    bean = (CheckinCheckoutBean)session.getAttribute("CheckinCheckoutBean");
                                 }
                                 if (bean != null && bean.getFileName() != null) {
                                 %>
                                    <tr><td class="paddingRow"></td></tr>
                                    <tr>
                                       <td>
                                          <img alt="Information icon" align="absmiddle" src="<%=request.getContextPath()%>/images/icons/info_icon.gif" />
                                          The file "<%=bean.getFileName()%>" was uploaded successfully.
                                       </td>
                                    </tr>
                                 <% } %>
                              </table>
                              </r:uploadForm>
                           </td>
                        </tr>
                     </table>
                  </td>
                  <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width="4"></td>
               </tr>
               
               <h:form id="checkin-file2">
                            
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
               
               </h:form>
               
            </table>
          </td>
       </tr>
    </table>
    
</f:view>

</r:page>