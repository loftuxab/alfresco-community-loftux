<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   
   <h2>Image Picker</h2>
   
   <h:form id="imagePicker">
   
      <awc:imagePickerRadio columns="4" spacing="5" value="#{DummyBean.properties.one}">
         <awc:modeListItem value="1" label="Checkin" tooltip="Checkin"
                           image="/images/icons/CheckIn.gif" />
         <awc:modeListItem value="2" label="Checkout" tooltip="Checkout"
                           image="/images/icons/CheckOut.gif" />
         <awc:modeListItem value="3" label="New File" tooltip="New File"
                           image="/images/icons/large_newFile.gif" />
      </awc:imagePickerRadio>
      <br/>
      <h:commandButton id="submit" value="Submit" action="#{DummyBean.submit}" />
      
      <p/>
      <awc:imagePickerRadio columns="1" spacing="6" value="#{DummyBean.properties.two}" style="border: 1px solid black">
         <awc:modeListItem value="1" tooltip="Checkin" image="/images/icons/CheckIn.gif" />
         <awc:modeListItem value="2" tooltip="Checkout" image="/images/icons/CheckOut.gif" />
         <awc:modeListItem value="3" tooltip="New File" image="/images/icons/large_newFile.gif" />
      </awc:imagePickerRadio>
      <br/>
      <h:commandButton id="submit" value="Submit" action="#{DummyBean.submit}" />
      
      
      <p/>
      <h:commandButton id="show-zoo-page" value="Show Zoo" action="showZoo" />

   </h:form>
   
   
      
</f:view>
