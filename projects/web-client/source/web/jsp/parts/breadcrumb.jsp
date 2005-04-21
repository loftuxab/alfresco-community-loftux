<%-- Breadcrumb area --%>
<tr>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_begin.gif" width=4 height=33></td>
   <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_bg.gif)">
      <%-- TODO: Breadcrumb text --%>
      <div style="padding-left:8px" class="headbarTitle"><h:outputText value="#{msg.location}"/>:&nbsp;<h:outputText styleClass="headbar" value="My Home"/></div>
   </td>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_end.gif" width=4 height=33></td>
</tr>
