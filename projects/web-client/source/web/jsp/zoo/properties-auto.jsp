<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>Auto configured property sheet</h2>
   
   <h:form id="propertySheetForm3">
   
      <r:propertySheetGrid value="/sop.txt" var="node3">
      </r:propertySheetGrid>

      <div style="color:red;"><h:messages/></div>
      <br/>
      <h:commandButton value="Update Properties" action="#{node3.persist}"/>
   
   </h:form>
   
   <br/><hr/>
   
   <h2>Config driven property sheet (WEB-INF/web-client-config.xml)</h2>
   
   <h:form id="propertySheetForm4">
   
      <r:propertySheetGrid value="/sop.txt" var="node4" externalConfig="true">
      </r:propertySheetGrid>
   
      <div style="color:red;"><h:messages/></div>
      <br/>
      <h:commandButton value="Update Properties" action="#{node4.persist}"/>

      <p>
      
      <h:commandButton id="show-zoo-page" value="Show Zoo" action="showZoo" />
   </h:form>
      
   
</f:view>
