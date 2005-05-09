<%-- Shelf area --%>
<a:panel id="shelfPanel" expanded="#{NavigationBean.shelfExpanded}">
   <table cellspacing=0 cellpadding=0 width=100% bgcolor='#ffffff'>
      <tr>
         <td><img src="<%=request.getContextPath()%>/images/parts/headbar_begin.gif" width=4 height=33></td>
         <td align=center width=100% style="background-image: url(<%=request.getContextPath()%>/images/parts/headbar_bg.gif)">
            <div class="headbarTitle"><h:outputText id="shelfText" value="#{msg.shelf}"/></div>
         </td>
         <td><img src="<%=request.getContextPath()%>/images/parts/headbar_end.gif" width=4 height=33></td>
      </tr>
      <tr>
         <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
         <td height=425 valign=top>
            
            <%-- TODO: Shelf components --%>
            <img src="<%=request.getContextPath()%>/images/test/shelf.png" style="padding-top:4px">
            
         </td>
         <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_6.gif)" width=4></td>
      </tr>
      <tr>
         <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_7.gif" width=4 height=4></td>
         <td width=100% align=center style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_8.gif)"></td>
         <td><img src="<%=request.getContextPath()%>/images/parts/whitepanel_9.gif" width=4 height=4></td>
      </tr>
   </table>
</a:panel>
