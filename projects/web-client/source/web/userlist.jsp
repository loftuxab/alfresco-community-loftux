<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="awc" %>

<%@ page isELIgnored="false" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css" TYPE="text/css">

<f:view>
   <%-- load a bundle of properties I18N strings here --%>
   <f:loadBundle basename="messages" var="msg"/>
   
   <h:form id="userListForm">
   
      <h2>Test Components</h2>
      
      <%-- use JSTL as simple example way to list the users --%>
      <%-- TODO: find out how to get this working - currently it can't find the JSF bean
                 in the session scope. Using useBean tag creates a new copy --%>
      <%--<jsp:useBean id="UserListBean" scope="session" class="jsftest.UserListBean" />--%>
      <%--
      <ol>
         <c:forEach items="${sessionScope.UserListBean.users}" var="u">
            <li>Username: ${u.username}, Name: ${u.name}, Roles: ${u.roles}</li>
         </c:forEach>
      </ol>
            
      <p>
      --%>
      
      <%-- Breadcrumb tests --%>
      <b>Breadcrumbs:</b><br>
      Default style with modified separator and actionListener handler:<br>
      <awc:breadcrumb id="path1" value="/horse/biscuit/flibble/1234" action="success" actionListener="#{TestList.clickBreadcrumb}" separator="~" />
      <br>
      Path style with default separator:<br>
      <awc:breadcrumb id="path2" styleClass="path"  value="/this/is/a/small/breadcrumb" />
      <br>
      Root should be missing from this breadcrumb:<br>
      <awc:panel id="panel0" progressive="true">
         <awc:breadcrumb id="path3" styleClass="path" value="/001/002/003/004/005" showRoot="false" />
      </awc:panel>
      
      <p>
      
      <%-- ActionLink tests --%>
      <b>ActionLinks:</b><br>
      <awc:actionLink id="action1" value="Action Link" action="success" actionListener="#{TestList.clickActionLink}" />
      <br>
      <awc:actionLink id="action2" value="Action Link With CSS" action="success" actionListener="#{TestList.clickActionLink}" styleClass="header" />
      <br>
      Action link with image and text, using vertical alignment property to centre text.<br>
      <awc:actionLink id="action3" value="Image Action Link" image="/images/icons/BlueArrow.gif" verticalAlign="40%" />
      <br>
      Action link with no text, just an image with title/alt text instead:<br>
      <awc:actionLink id="action4" value="Image Only Action Link" image="/images/icons/link_small.gif" showLink="false"/>
      
      <p>
      
      <%-- Progressive panel test --%>
      <awc:panel id="panel1" border="mainwhite" bgcolor="white" progressive="true" label="Progressive Panel Test!" styleClass="mainTitle">
         <p>
         nothing exciting here
         <b>not at all</b>
         testing123
         <p>
         <h:outputText value="Test Text From Outputtest Tag _WITHOUT_ explicit ID (WILL BREAK!)"/>
         <br>
         <h:commandButton id="panel1-but1" value="Test Button with explicit ID" action="success" />
      </awc:panel>
      
      <p>
      
      <%-- DataList component test --%>
      DataList component test
      before datalist table
      <awc:dataList cellspacing="2" cellpadding="1" styleClass="recordSet">
         <tr><td>inside datalist table</td></tr>
      </awc:dataList>
      after datalist table
      
      <p>
      
      <%-- Progressive panel test --%>
      <awc:panel id="panelX" border="mainwhite" bgcolor="white" progressive="true" label="Panel Testing again!" styleClass="mainTitle">
         <p>
         BLAH BLAH BLAH
         <p>
         <h:outputText id="out1" value="Test Text From Outputtest Tag WITH explicit ID"/>
      </awc:panel>
      
      <p>
      
      <%-- RichList component test --%>
      <%-- The RichList component is different to the data-grid in that it is
           designed to render it's own child components. This means it is capable
           of rendering the columns in any order and in any kind of layout. Allowing
           the impl of details, icons and list views within a single component. --%>
      <%-- NOTE: Suggest config of each view independantly in the config XML e.g.
                 to allow the icon/details views to use different page sizes or styles.
                 Otherwise you have to pick values "compatible" with all view modes! --%>
      RichList component test shown in Details view mode, including sortable columns of various data types and paging.
      <awc:richList viewMode="details" pageSize="5" styleClass="" style="border:thin solid #eeeeff; padding:2px" rowStyleClass="recordSetRow" altRowStyleClass="recordSetRowAlt" width="100%"
            value="#{TestList.rows}" var="r" initialSortColumn="name" initialSortDescending="true">
         <awc:column primary="true" width="200" style="padding:2px" styleClass="">
            <f:facet name="header">
               <awc:sortLink label="Name" value="name" mode="case-insensitive" styleClass="header"/>
            </f:facet>
            <f:facet name="large-icon">
               <%-- this could be a clickable action image etc. --%>
               <h:graphicImage alt="#{r.name}" title="#{r.name}" width="38" height="38" url="/images/icons/folder_large.png" />
            </f:facet>
            <f:facet name="small-icon">
               <%-- example of a clickable action image as an icon --%>
               <awc:actionLink value="#{r.name}" image="/images/icons/folder.gif" actionListener="#{TestList.clickNameLink}" showLink="false">
                  <f:param name="name" value="#{r.name}" />
               </awc:actionLink>
            </f:facet>
            <awc:actionLink value="#{r.name}" actionListener="#{TestList.clickNameLink}">
               <f:param name="name" value="#{r.name}" />
            </awc:actionLink>
         </awc:column>
         
         <awc:column>
            <f:facet name="header">
               <awc:sortLink label="Count" value="count" styleClass="header"/>
            </f:facet>
            <h:outputText value="#{r.count}"/>
         </awc:column>
         
         <awc:column>
            <f:facet name="header">
               <awc:sortLink label="Valid" value="valid" styleClass="header"/>
            </f:facet>
            <h:outputText value="#{r.valid}"/>
         </awc:column>
         
         <awc:column>
            <f:facet name="header">
               <awc:sortLink label="Relevance" value="relevance" styleClass="header"/>
            </f:facet>
            <h:outputText value="#{r.relevance}"/>
         </awc:column>
         
         <awc:column>
            <f:facet name="header">
               <awc:sortLink label="Created Date" value="created" styleClass="header"/>
            </f:facet>
            <h:outputText value="#{r.created}">
               <%-- example of a DateTime converter --%>
               <%-- can be used to convert both input and output text --%>
               <f:convertDateTime dateStyle="short" />
            </h:outputText>
         </awc:column>
         
         <awc:column actions="true">
            <f:facet name="header">
               <h:outputText value="#{msg.actions}"/>
            </f:facet>
            <h:outputText value="Action | Action | Action"/>
         </awc:column>
         
         <%-- components other than columns added to a RichList will generally
              be rendered as part of the list footer --%>
         <awc:dataPager/>
      </awc:richList>
      
      <p>
      
      <awc:panel id="panel2" border="white" bgcolor="white" progressive="true" label="Progressive Panel Example 2" styleClass="mainSubTitle">
         NOTE: currently all JSF components within a Progressive Panel MUST be given an explicit ID!
         
         <br>
         
         Same list shown in a different view mode (Icons). This mode renderer displays the large icon and renderers in a multi-column format. It also chooses not to display the sort header links.
         <awc:richList id="list2" viewMode="icons" pageSize="6" styleClass="" style="padding:2px" rowStyleClass="recordSetRow" altRowStyleClass="recordSetRowAlt" width="100%"
               value="#{TestList.rows}" var="r" initialSortColumn="name" initialSortDescending="true">
            <awc:column id="list2-col1" primary="true" width="200" style="padding:2px" styleClass="">
               <f:facet name="header">
                  <awc:sortLink id="list2-sort1" label="Name" value="name" mode="case-insensitive" styleClass="header"/>
               </f:facet>
               <f:facet name="large-icon">
                  <%-- example of a clickable action image as an icon --%>
                  <awc:actionLink id="list2-img1" value="#{r.name}" image="/images/icons/folder_large.png" actionListener="#{TestList.clickNameLink}" showLink="false">
                     <f:param id="list2-param1-id" name="name" value="#{r.name}" />
                  </awc:actionLink>
               </f:facet>
               <f:facet name="small-icon">
                  <h:graphicImage id="list2-img2" alt="#{r.name}" title="#{r.name}" width="15" height="13" url="/images/icons/folder.gif" />
               </f:facet>
               <%-- example of a clickable action link item --%>
               <awc:actionLink id="list2-link1" value="#{r.name}" actionListener="#{TestList.clickNameLink}">
                  <f:param id="list2-param2-id" name="name" value="#{r.name}" />
               </awc:actionLink>
            </awc:column>
            
            <%-- TODO: need some way to allow columns for specific views
            <awc:column forViewMode="icon">
               <h:outputText value="This would be a longer textual description"/>
            </awc:column>
            --%>
            
            <awc:column id="list2-col2">
               <f:facet name="header">
                  <awc:sortLink id="list2-sort2" label="Count" value="count" styleClass="header"/>
               </f:facet>
               <h:outputText id="list2-out3" value="Count: #{r.count}"/>
            </awc:column>
            
            <awc:column id="list2-col3">
               <f:facet name="header">
                  <awc:sortLink id="list2-sort3" label="Created Date" value="created" styleClass="header"/>
               </f:facet>
               <h:outputText id="list2-out4" value="Created Date: "/>
               <h:outputText id="list2-out5" value="#{r.created}">
                  <%-- example of a DateTime converter --%>
                  <%-- can be used to convert both input and output text --%>
                  <f:convertDateTime dateStyle="short" />
               </h:outputText>
            </awc:column>
            
            <awc:column id="list2-col4" actions="true">
               <f:facet name="header">
                  <h:outputText id="list2-out6" value="#{msg.actions}"/>
               </f:facet>
               <h:outputText id="list2-out7" value="Action | Action | Action"/>
            </awc:column>
            
            <%-- components other than columns added to a RichList will generally
                 be rendered as part of the list footer --%>
            <awc:dataPager id="list2-pager"/>
         </awc:richList>
      
      </awc:panel>
      
      <p>
      
      <awc:panel id="panel3" border="innergrey" bgcolor="#e8e8e8" progressive="false" label="Non-progressive Panel" styleClass="mainSubTitle">
      
         <br>
         
         Same list shown in a different view mode (List). This mode displays the small icon and uses single column rendering.
         <awc:richList viewMode="list" pageSize="5" styleClass="" style="padding:4px" width="100%"
               value="#{TestList.rows}" var="r" initialSortColumn="name" initialSortDescending="true">
            <awc:column primary="true">
               <f:facet name="header">
                  <awc:sortLink label="Name" value="name" mode="case-insensitive" styleClass="header"/>
               </f:facet>
               <f:facet name="large-icon">
                  <%-- this could be a clickable action image etc. --%>
                  <h:graphicImage alt="#{r.name}" title="#{r.name}" width="38" height="38" url="/images/icons/folder_large.png" />
               </f:facet>
               <f:facet name="small-icon">
                  <%-- this could be a clickable action image etc. --%>
                  <h:graphicImage alt="#{r.name}" title="#{r.name}" width="15" height="13" url="/images/icons/folder.gif" />
               </f:facet>
               <awc:actionLink value="#{r.name}" actionListener="#{TestList.clickNameLink}">
                  <f:param id="list-param-id" name="name" value="#{r.name}" />
               </awc:actionLink>
            </awc:column>
            
            <awc:column>
               <f:facet name="header">
                  <awc:sortLink label="Count" value="count" styleClass="header"/>
               </f:facet>
               <h:outputText value="Count: #{r.count}"/>
            </awc:column>
            
            <awc:column>
               <f:facet name="header">
                  <awc:sortLink label="Created Date" value="created" styleClass="header"/>
               </f:facet>
               <h:outputText value="Created Date: "/>
               <h:outputText value="#{r.created}">
                  <%-- example of a DateTime converter --%>
                  <%-- can be used to convert both input and output text --%>
                  <f:convertDateTime dateStyle="short" />
               </h:outputText>
            </awc:column>
            
            <awc:column actions="true">
               <f:facet name="header">
                  <h:outputText value="#{msg.actions}"/>
               </f:facet>
               <h:outputText value="Action | Action | Action"/>
            </awc:column>
            
            <%-- components other than columns added to a RichList will generally
                 be rendered as part of the list footer --%>
            <awc:dataPager/>
         </awc:richList>
      
      </awc:panel>
      
      <p>
      
      <%-- example of using a JSF DataTable to list the users --%>
      <%-- iterates around the List of User objects in the UserListBean --%>
      JSF dataTable component test.
      <h:dataTable id="userlist" value="#{UserListBean.usersModel}" var="u">
        <h:column>
          <f:facet name="header">
            <%-- NOTE: You cannot insert plain HTML text here, due to the way that some JSF
                       components are architected, the plain HTML would get displayed before
                       the body of the datatable tag is output. This is also true of the
                       other container tags including 'panel'.
                       The datatable is considerably inferior to our portal data tags
                       or even the freely available 'displaytag' tag library --%>
            <%-- You can also use the nasty 'f:verbatim' tag to wrap any non JSF elements --%>
            <h:outputText value="#{msg.username}"/>
          </f:facet>
          <h:outputText value="#{u.username}"/>
        </h:column>
        <h:column>
          <f:facet name="header">
            <h:outputText value="#{msg.name}"/>
          </f:facet>
          <h:outputText value="#{u.name}"/>
        </h:column>
        <h:column>
          <f:facet name="header">
            <h:outputText value="#{msg.joindate}"/>
          </f:facet>
          <h:outputText value="#{u.dateJoined}">
            <%-- example of a DateTime converter --%>
            <%-- can be used to convert both input and output text --%>
            <f:convertDateTime dateStyle="short" />
          </h:outputText>
        </h:column>
        <h:column>
          <f:facet name="header">
            <h:outputText value="#{msg.roles}"/>
          </f:facet>
          <h:outputText value="#{u.roles}"/>
        </h:column>
        <h:column>
          <f:facet name="actions">
            <h:outputText value="#{msg.actions}"/>
          </f:facet>
          <%-- inline command link - has an action listener which will decode which
               item in the grid it was clicked using the param tag below
               Then the action listener will delegate to the action view --%>
          <h:commandLink id="edit" value="Edit" action="edituser" actionListener="#{UserListBean.editUser}">
            <f:param id="userId" name="id" value="#{u.username}" />
          </h:commandLink>
        </h:column>
      </h:dataTable>
      
      <p>
      
      <%-- component evaluators --%>
      <b>Test Evaluators</b><br>
      1a. Boolean Evaluator - you should see the next line of text:<br>
      <awc:booleanEvaluator value="#{TestList.rows != null}">
         <h:outputText value="Component inside an evaluator"/>
      </awc:booleanEvaluator>
      <br><br>
      1b. Boolean Evaluator - you should <i>not</i> see the next line of text:<br>
      <awc:booleanEvaluator value="#{TestList.rows == null}">
         <h:outputText value="Component inside an evaluator"/>
      </awc:booleanEvaluator>
      <br><br>
      2a. Value Set Evaluator - you should see the next line of text:<br>
      <awc:valueSetEvaluator value="#{TestList.rows}">
         <h:outputText value="Component inside an evaluator"/>
      </awc:valueSetEvaluator> 
      <br><br>
      2b. Value Set Evaluator - you should <i>not</i> see the next line of text:<br>
      <awc:valueSetEvaluator value="#{null}">
         <h:outputText value="Component inside an evaluator"/>
      </awc:valueSetEvaluator>
      <br><br>
      3a. String Equals Evaluator - you should see the next line of text:<br>
      <awc:stringEqualsEvaluator value="some string" condition="some string">
         <h:outputText value="Component inside an evaluator"/>
      </awc:stringEqualsEvaluator>
      <br><br>
      3b. String Equals Evaluator - you should <i>not</i> see the next line of text:<br>
      <awc:stringEqualsEvaluator value="some string" condition="some string123">
         <h:outputText value="Component inside an evaluator"/>
      </awc:stringEqualsEvaluator>
      
      <p>
      
      <%-- Date Picker Component --%>
      <%-- Example of a tag utilising an Input Component with a custom renderer.
              The renderer handles encoding and decoding of date values to UI elements --%>
      <b>Date Picker Components</b><br>
      Basic date picker:<br>
      <awc:inputDatePicker id="date1" value="#{TestList.rows[0].created}" startYear="1996" yearCount="10"/>
      <br>
      Date Picker with CSS style applied:<br>
      <awc:inputDatePicker id="date2" startYear="2000" yearCount="5" styleClass="userInputForm" />
      
      <p>
      
      <h:commandButton id="add-user" value="Add" action="adduser" actionListener="#{UserListBean.addUser}"/>
      <br/><br/>
      <h:commandButton id="show-property-zoo" value="Property Zoo" action="#{ZooService.showPropertyZoo}" />
   </h:form>
</f:view>
