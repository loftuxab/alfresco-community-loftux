<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>Property sheet with standard JSF components</h2>
   
   <h:form id="propertySheetForm">
   
      <awc:propertySheetGrid value="/gav.doc">
         <h:outputText value='#{DDService.types[node.type].propertiesMap["name"].displayName}: ' />
         <h:inputText value="#{node.properties.name}" />
         <h:outputText value='#{DDService.types[node.type].propertiesMap["description"].displayName}: ' />
         <h:inputText value="#{node.properties.description}" />         
         <h:outputText value='#{DDService.types[node.type].propertiesMap["created"].displayName}: ' />
         <h:inputText value="#{node.properties.created}" disabled='#{DDService.types[node.type].propertiesMap["created"].readOnly}'>
            <f:convertDateTime dateStyle="short" pattern="d/MM/yyyy" />
         </h:inputText>
         <h:outputText value='#{DDService.types[node.type].propertiesMap["modified"].displayName}: ' />
         <h:inputText value="#{node.properties.modified}">
            <f:convertDateTime dateStyle="short" pattern="d/MM/yyyy" />
         </h:inputText>
         <!-- TODO: Put the keywords in here to test the custom converter tag -->
      </awc:propertySheetGrid>
   
      <div style="color:red;"><h:messages/></div>
      <br/>
      <h:commandButton value="Update Properties" action="#{node.persist}"/>  
   
   </h:form>

   <br/><hr/>
   
   <h2>Property sheet with custom property components</h2>
   
   <h:form id="propertySheetForm2">
   
      <awc:propertySheetGrid value="/kev.txt" var="node2">
         <awc:property value="name" columns="1" />
         <awc:property value="description" columns="1" />
         <awc:property value="created" columns="1" />
         <awc:property value="modified" columns="2" />
         <awc:property value="non-existent" columns="1" />
      </awc:propertySheetGrid>

      <div style="color:red;"><h:messages/></div>
      <br/>
      <h:commandButton value="Update Properties" action="#{node2.persist}"/>
      
      <br/><br/><hr/><br/>
      <h:commandButton value="Auto Configured Property Zoo" action="#{ZooService.showPropertyZoo2}" />

      <p>
      
      <h:commandButton id="show-jump-page" value="Back To Jump Page" action="jumppage" />

   </h:form>
      
</f:view>
