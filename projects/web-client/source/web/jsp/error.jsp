<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="32kb" %>
<%@ page isELIgnored="false" %>

<r:page>

<table cellspacing="0" cellpadding="2" width="100%">
   <tr>
      <%-- Top level toolbar and company logo area --%>
      <td width="100%">
         <table cellspacing="0" cellpadding="0" width="100%">
            <tr>
               <td><img src="<%=request.getContextPath()%>/images/parts/titlebar_begin.gif" width="28" height="30" /></td>
               <td width="100%" style="background-image: url(<%=request.getContextPath()%>/images/parts/titlebar_bg.gif)">
                  <span class="topToolbarTitle">System Error</span>
               </td>
               <td><img src="<%=request.getContextPath()%>/images/parts/titlebar_end.gif" width=4 height=30></td>
            </tr>
         </table>
      </td>
   </tr>
   <tr>
      <td>
         <r:systemError styleClass="errorMessage" detailsStyleClass="mainSubTextSmall" 
                        showDetails="false" />
      </td>
   </tr>
</table>

</r:page>
