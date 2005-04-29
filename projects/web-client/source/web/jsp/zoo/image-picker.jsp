<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>Image Picker</h2>
   
   <h:form id="imagePicker">
   
      
      <h:commandButton id="show-zoo-page" value="Show Zoo" action="showZoo" />

   </h:form>
      
</f:view>
