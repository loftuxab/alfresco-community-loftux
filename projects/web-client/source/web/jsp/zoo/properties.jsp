<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>Property sheet with standard JSF components</h2>
   
   <h:form id="propertySheetForm">
   
      <a:propertySheetGrid value="/gav.doc">
         <h:outputText value='#{MockDDService.types[node.type].propertiesMap["name"].displayName}: ' />
         <h:inputText value="#{node.name}" />
         <h:outputText value='#{MockDDService.types[node.type].propertiesMap["description"].displayName}: ' />
         <h:inputText value="#{node.description}" />         
         <h:outputText value='#{MockDDService.types[node.type].propertiesMap["created"].displayName}: ' />
         <h:inputText value="#{node.created}" disabled='#{MockDDService.types[node.type].propertiesMap["created"].readOnly}'>
            <f:convertDateTime dateStyle="short" pattern="d/MM/yyyy" />
         </h:inputText>
         <h:outputText value='#{MockDDService.types[node.type].propertiesMap["modified"].displayName}: ' />
         <h:inputText value="#{node.modified}">
            <f:convertDateTime dateStyle="short" pattern="d/MM/yyyy" />
         </h:inputText>
         <!-- TODO: Put the keywords in here to test the custom converter tag -->
      </a:propertySheetGrid>
   
      <div style="color:red;"><h:messages/></div>
      <br/>
      <h:commandButton value="Update Properties" action="#{node.persist}"/>  
   
   </h:form>

   <br/><hr/>
   
   <h2>Property sheet with custom property components</h2>
   
   <h:form id="propertySheetForm2">
   
      <a:propertySheetGrid value="/kev.txt" var="node2">
         <a:property value="name" columns="1" />
         <a:property value="description" columns="1" />
         <a:property value="created" columns="1" />
         <a:property value="modified" columns="2" />
         <a:property value="non-existent" columns="1" />
      </a:propertySheetGrid>

      <div style="color:red;"><h:messages/></div>
      <br/>
      <h:commandButton value="Update Properties" action="#{node2.persist}"/>

      <p>
      
      <h:commandButton id="show-zoo-page" value="Show Zoo" action="showZoo" />

   </h:form>
      
</f:view>
