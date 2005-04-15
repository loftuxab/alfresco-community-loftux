<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<%@ page isELIgnored="false" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>Jalapeno Jump Page</h2>
   
   <h:form id="jumpForm">
      
      <h:commandButton id="show-components-zoo" value="Components Zoo" action="showComponentsZoo" />
      <br>
      <h:commandButton id="show-property-zoo" value="Property Zoo" action="showPropertyZoo" />
      <br>
      <h:commandButton id="show-user-list" value="UserList Test Pages" action="showUserlist" />
      <br>
      <h:commandButton id="show-browse" value="Browse Test Page" action="showBrowse" />
      
   </h:form>
   
</f:view>
