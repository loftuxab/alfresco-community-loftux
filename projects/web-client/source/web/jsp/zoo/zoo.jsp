<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<%@ page isELIgnored="false" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>The Zoo</h2>
   
   <h:form id="zooForm">
      
      <h:commandButton id="show-components-zoo" value="Component Zoo" action="showRichListZoo" />
      <br/><br/>
      <h:commandButton id="show-property-zoo" value="Property Zoo" action="showPropertyZoo" />
      <br/><br/>
      <h:commandButton id="show-auto-property-zoo" value="Auto Property Zoo" action="showAutoPropertyZoo" />
      <br/><br/>
      <h:commandButton id="show-image-picker" value="Image Picker Zoo" action="showImagePickerZoo" />
      <br/><br/>
      <h:commandButton id="show-user-list" value="UserList Test Pages" action="showUserlist" />
      
      <p/><p/>
      <hr/><p/>
      <h:commandButton id="show-web-client" value="Back to the Web Client" action="showWebClient" />
      
   </h:form>
   
</f:view>
