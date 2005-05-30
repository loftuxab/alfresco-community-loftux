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
      <tr width=185>
         <td style="background-image: url(<%=request.getContextPath()%>/images/parts/whitepanel_4.gif)" width=4></td>
         <td valign=top>
            
            <%-- Shelf component --%>
            <r:shelf id="shelf" groupPanel="ballongrey" groupBgcolor="#eeeeee" selectedGroupPanel="bluetoolbar" selectedGroupBgcolor="#e9f0f4"
                  innerGroupPanel="white" innerGroupBgcolor="#ffffff">
               <r:shelfGroup label="My Clipboard" id="shelf-group-1">
                  <r:shelfItem id="shelf-item-1-1">
                     
                  </r:shelfItem>
               </r:shelfGroup>
               
               <r:shelfGroup label="Shortcuts" id="shelf-group-2" expanded="true">
                  <r:shelfItem id="shelf-item-2-1">
                     
                  </r:shelfItem>
               </r:shelfGroup>
               
               <r:shelfGroup label="Drop Zone" id="shelf-group-3">
                  <r:shelfItem id="shelf-item-3-1">
                     
                  </r:shelfItem>
               </r:shelfGroup>
               
               <r:shelfGroup label="Actions in Progress" id="shelf-group-4">
                  <r:shelfItem id="shelf-item-4-1">
                     
                  </r:shelfItem>
               </r:shelfGroup>
            </r:shelf>
            
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
