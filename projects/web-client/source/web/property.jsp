<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<f:view>
   
   <h2>Property Sheet</h2>
   
   <h:form id="propertySheetForm">

   <!--
   <awc:propertySheet value="/gav.doc" />
   -->
   <awc:propertySheet value="/sop.txt" />

   <br/>
   <h:commandButton value="Update" action="#{RepositoryService.updateProperties}"/>
   
   <div style="color:red;"><h:messages/></div>
   
   </h:form>
      
</f:view>
