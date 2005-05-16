<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<r:page>

<f:view>
   <%-- load a bundle of properties I18N strings here --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <h:form id="loginForm" >
   
      <h3>Enter Login details:</h3>
      
      <table border=0 cellspacing=2 cellpadding=2>
         <tr>
            <td>
               <%-- use an I18N message --%>
               <h:outputText value="#{msg.username}"/>:
            </td>
            <td>
               <%-- input text field, with an example of a nested validator tag --%>
               <h:inputText id="user-name" value="#{LoginBean.name}" required="true">
                  <f:validateLength minimum="5" maximum="12" />
               </h:inputText>
         		<%-- message tag to show errors for the 'user-name' field --%>
               <h:message id="errors2" for="user-name" style="color:red; font-size:10px" />
            </td>
         </tr>
         
         <tr>
            <td>
               <h:outputText value="#{msg.password}"/>:
            </td>
            <td>
               <%-- password text field, with an example of a validation bean method --%>
               <%-- the validation method adds a faces message to be displayed by a message tag --%>
               <h:inputSecret id="user-password" value="#{LoginBean.password}" validator="#{LoginBean.validatePassword}" required="true" />
               <%-- message tag to show errors for the 'user-password' field --%>
               <h:message id="errors1" for="user-password" style="color:red; font-size:10px" />
            </td>
         </tr>
      </table>
      
      <p>
      
      <%-- command button - fires an action to a bean method for processing --%>
      <h:commandButton id="submit" action="#{LoginBean.login}" value="Login" />
      
      <p>
      
      <%-- messages tag to show messages not handled by other specific message tags --%>
      <%-- messages added with different FacesMessage.SEVERITY_XXXX level can be shown
           using different classes/styles depending on the level --%>
      <h:messages globalOnly="true" style="color:red; font-family:'New Century Schoolbook'; font-style:oblique;" />
      
   </h:form>
</f:view>

</r:page>