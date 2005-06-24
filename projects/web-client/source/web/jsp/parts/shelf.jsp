<%--
  Copyright (C) 2005 Alfresco, Inc.

  Licensed under the GNU Lesser General Public License as
  published by the Free Software Foundation; either version
  2.1 of the License, or (at your option) any later version.
  You may obtain a copy of the License at

    http://www.gnu.org/licenses/lgpl.txt

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  either express or implied. See the License for the specific
  language governing permissions and limitations under the
  License.
--%>
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
         <td valign=top width=100%>
            
            <%-- Shelf component --%>
            <%-- IMPORTANT NOTE: All inner components must be given an explicit ID! --%>
            <%--                 This is because they are wrapped in a Panel component --%>
            <r:shelf id="shelf" groupPanel="ballongrey" groupBgcolor="#eeeeee" selectedGroupPanel="bluetoolbar" selectedGroupBgcolor="#e9f0f4"
                  innerGroupPanel="white" innerGroupBgcolor="#ffffff">
               <r:shelfGroup label="Clipboard" id="shelf-group-1" expanded="true">
                  <r:clipboardShelfItem id="clipboard-shelf-item" collections="#{ClipboardBean.items}" pasteActionListener="#{ClipboardBean.pasteItem}" />
               </r:shelfGroup>
               
               <r:shelfGroup label="Shortcuts" id="shelf-group-2">
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
