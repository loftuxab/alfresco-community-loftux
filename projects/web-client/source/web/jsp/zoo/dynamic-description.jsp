<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>

<%@ page buffer="32kb" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>

   <h2>Dynamic Description</h2>
   
   <h:form id="dyna-desc">

      <h:selectOneMenu onchange="javascript:listItemSelected(this);">
         <f:selectItem itemLabel="Choice One" itemValue="one" />
         <f:selectItem itemLabel="Choice Two" itemValue="two" />
         <f:selectItem itemLabel="Choice Three" itemValue="three" />
         <f:selectItem itemLabel="Choice Four" itemValue="four" />
      </h:selectOneMenu>
      
      <br/><br/>
      <a:dynamicDescription selected="one" functionName="listItemSelected">
         <a:descriptions value="#{DummyBean.properties}" />
      </a:dynamicDescription>
      
      <br/><br/>
      <a:imagePickerRadio columns="4" spacing="4" value="container"
                            onclick="javascript:itemSelected(this);">
         <a:listItem value="container" label="Container" tooltip="Container"
                           image="/images/icons/space.gif" />
         <a:listItem value="wiki" label="Wiki" tooltip="Wiki"
                           image="/images/icons/wiki.gif" />
         <a:listItem value="discussion" label="Discussion" tooltip="Discussion"
                           image="/images/icons/discussion.gif" />
      </a:imagePickerRadio>
      
      <br/><br/>
      <a:dynamicDescription selected="container">
         <a:description controlValue="container" text="Container" />
         <a:description controlValue="wiki" text="Wiki" />
         <a:description controlValue="discussion" text="Discussion" />
      </a:dynamicDescription>
      
      <br/><br/>
      <h:commandButton id="show-zoo-page" value="Show Zoo" action="showZoo" />

   </h:form>

</f:view>
