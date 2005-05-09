<%-- Breadcrumb area --%>
<%-- Designed to support a variable height breadcrumb --%>
<tr>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_1.gif" width=4 height=7></td>
   <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_2.gif)"></td>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_3.gif" width=4 height=7></td>
</tr>

<tr>
   <td style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_4.gif)"></td>
   <td bgcolor="#D2CABB">
      <%-- Breadcrumb component --%>
      <div style="padding-left:8px" class="headbarTitle">
         <h:outputText value="#{msg.location}"/>:&nbsp;
         <a:breadcrumb value="#{NavigationBean.location}" styleClass="headbarLink" />
      </div>
   </td>
   <td style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_6.gif)"></td>
</tr>

<tr>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_7.gif" width=4 height=10></td>
   <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_8.gif)"></td>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_9.gif" width=4 height=10></td>
</tr>
