<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="16kb" %>
<%@ page isELIgnored="false" %>
<%@ page import="org.alfresco.web.ui.common.PanelGenerator" %>

<r:page>

<f:view>
   <%-- load a bundle of properties I18N strings here --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <h:form id="loginForm" >
   
   <table width=100% height=98% align=center>
      <tr width=100% align=center>
         <td valign=middle align=center width=100%>
            
            <table cellspacing=0 cellpadding=0 border=0>
            <tr><td width=7><img src='<%=request.getContextPath()%>/images/parts/white_01.gif' width=7 height=7 alt=''></td>
            <td background='<%=request.getContextPath()%>/images/parts/white_02.gif'>
            <img src='<%=request.getContextPath()%>/images/parts/white_02.gif' width=7 height=7 alt=''></td>
            <td width=7><img src='<%=request.getContextPath()%>/images/parts/white_03.gif' width=7 height=7 alt=''></td>
            </tr>
            <tr><td background='<%=request.getContextPath()%>/images/parts/white_04.gif'>
            <img src='<%=request.getContextPath()%>/images/parts/white_04.gif' width=7 height=7 alt=''></td><td bgcolor='white'>
            
            <table border=0 cellspacing=4 cellpadding=2>
               <tr>
                  <td colspan=2>
                     <img src='<%=request.getContextPath()%>/images/logo/AlfrescoLogo200.png' width=200 height=58 alt="Alfresco" title="Alfresco">
                  </td>
               </tr>
               
               <tr>
                  <td colspan=2>
                     <h4>Enter Login details:</h4>
                  </td>
               </tr>
               
               <tr>
                  <td>
                     <h:outputText value="#{msg.username}"/>:
                  </td>
                  <td>
                     <%-- input text field, with an example of a nested validator tag --%>
                     <h:inputText id="user-name" value="#{LoginBean.username}" validator="#{LoginBean.validateUsername}" style="width:150px" />
                  </td>
               </tr>
               
               <tr>
                  <td>
                     <h:outputText value="#{msg.password}"/>:
                  </td>
                  <td>
                     <%-- password text field, with an example of a validation bean method --%>
                     <%-- the validation method adds a faces message to be displayed by a message tag --%>
                     <h:inputSecret id="user-password" value="#{LoginBean.password}" validator="#{LoginBean.validatePassword}" style="width:150px" />
                  </td>
               </tr>
               
               <tr>
                  <td colspan=2 align=right>
                     <h:commandButton id="submit" action="#{LoginBean.login}" value="Login" />
                  </td>
               </tr>
            </table>
            
            <p>
            
            <%-- messages tag to show messages not handled by other specific message tags --%>
            <h:messages style="color:red; font-size:10px" layout="table" />
            
            </td><td background='<%=request.getContextPath()%>/images/parts/white_06.gif'>
            <img src='<%=request.getContextPath()%>/images/parts/white_06.gif' width=7 height=7 alt=''></td></tr>
            <tr><td width=7><img src='<%=request.getContextPath()%>/images/parts/white_07.gif' width=7 height=7 alt=''></td>
            <td background='<%=request.getContextPath()%>/images/parts/white_08.gif'>
            <img src='<%=request.getContextPath()%>/images/parts/white_08.gif' width=7 height=7 alt=''></td>
            <td width=7><img src='<%=request.getContextPath()%>/images/parts/white_09.gif' width=7 height=7 alt=''></td></tr>
            </table>
            
         </td>
      </tr>
   </table>
      
   </h:form>
</f:view>

</r:page>