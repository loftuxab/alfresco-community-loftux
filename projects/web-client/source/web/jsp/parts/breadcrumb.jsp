<%-- Breadcrumb area --%>
<tr>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_begin.gif" width=4 height=33></td>
   <td width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_bg.gif)">
      <%-- Breadcrumb component --%>
      <div style="padding-left:8px" class="headbarTitle">
         <h:outputText value="#{msg.location}"/>:&nbsp;
         <awc:breadcrumb value="#{NavigationBean.location}" styleClass="headbarLink" />
      </div>
   </td>
   <td><img src="<%=request.getContextPath()%>/images/parts/headbar_end.gif" width=4 height=33></td>
</tr>
