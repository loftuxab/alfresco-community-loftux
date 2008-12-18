<%@ page import="org.alfresco.module.vti.*,org.alfresco.module.vti.httpconnector.*,org.alfresco.module.vti.metadata.dialog.*,org.alfresco.module.vti.metadata.dic.*, java.util.*" %>

<%
	String location = ((VtiRequest)request).getParameter("location", "");
    VtiSortField sortField = ((VtiRequest)request).getParameter("SortField", VtiSortField.TYPE);
    VtiSort sort = ((VtiRequest)request).getParameter("SortDir", VtiSort.ASC);
    String context = ((VtiRequest)request).getContextPath(); 
    String host = ((VtiRequest)request).getHeader("Host");
    List<DialogMetaInfo> items = (List<DialogMetaInfo>)request.getAttribute("VTIDialogsMetaInfoList");
    String alfContext = (String)request.getAttribute(VtiServletContainer.VTI_ALFRESCO_CONTEXT);
%>


<!-- _lcid="1033" _version="11.0.5510" _dal="1" -->
<!-- _LocalBinding -->
<html dir="ltr">
<HEAD>
    <META Name="GENERATOR" Content="Microsoft SharePoint">
    <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8">
    <META HTTP-EQUIV="Expires" content="0">
    <link rel="stylesheet" href="<%=alfContext%>/resources/css/main.css" type="text/css">
	<link rel="stylesheet" href="<%=alfContext%>/resources/css/picker.css" type="text/css">    
    <Title ID=onetidTitle>File Properties</Title>
    <script language="JavaScript">
      function checkScroll()
       {
        if (document.body.scrollHeight > document.body.offsetHeight || document.body.scrollWidth > document.body.offsetWidth)
            document.body.scroll="yes";
       }
    </script>
    <style type="text/css">
	   .deselected {
			background-color: white;				
		}
		.selected {
			background-color: #BBDDFF;
		}
	</style>
	<script type="text/javascript">
		var oldSelect = null;
		function selectrow(rowId) {
			var selectedRow = document.getElementById(rowId);
			if (oldSelect != null && selectedRow != oldSelect) {
				oldSelect.className = "deselected";
			}
			selectedRow.className = "selected";
			oldSelect = selectedRow;	
		}
	</script>
	<script type="text/javascript">
		function changeStyle(id)
		{
			document.getElementById(id).style.cursor = "pointer";
			document.getElementById(id).style.textDecoration = "underline";
		}		
		
		function revertStyle(id)		
		{
			document.getElementById(id).style.cursor = "default";
			document.getElementById(id).style.textDecoration = "none";		
		}
	</script>
</HEAD>

<BODY topmargin=5 leftmargin=5 scroll=no serverType=OWS onload="checkScroll()" onresize="checkScroll()">
   
   <table width="100%">
   	<tr> <td width="100%" align="rigth"> <img src='<%=alfContext%>/resources/images/logo/AlfrescoLogo200.png' width=200 height=58 alt="Alfresco" title="Alfresco"> </td> </tr>
   </table>
   
   <table ID="FileDialogViewTable" width="100%" class="recordSet" style="cursor: default;" cellspacing=0>
	<tr>
		<th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortDocIcon" TITLE="Sort by Type" SORTINGFIELDS="RootFolder=<%=location%>&SortField=DocIcon&SortDir=<%=getSortDir(VtiSortField.TYPE, sortField, sort)%>&View=FileDialog" onmouseover="changeStyle('diidSortDocIcon');" onmouseout="revertStyle('diidSortDocIcon');">
		 Type <% if (sortField.equals(VtiSortField.TYPE)) {%><img src='<%=alfContext%>/resources/images/icons/<%= sort.equals(VtiSort.ASC) ? "sort_up.gif" : "sort_down.gif" %>' width='10' height='6' alt='' style='border-width:0px;'/> <% } %></a></th>
        <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortBaseName" TITLE="Sort by Name" SORTINGFIELDS="RootFolder=<%=location%>&SortField=BaseName&SortDir=<%=getSortDir(VtiSortField.NAME, sortField, sort)%>&View=FileDialog" onmouseover="changeStyle('diidSortBaseName');" onmouseout="revertStyle('diidSortBaseName');">
         Name <% if (sortField.equals(VtiSortField.NAME)) {%><img src='<%=alfContext%>/resources/images/icons/<%= sort.equals(VtiSort.ASC) ? "sort_up.gif" : "sort_down.gif" %>' width='10' height='6' alt='' style='border-width:0px;'/> <% } %></a></th>
        <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortEditor" TITLE="Sort by Modified By" SORTINGFIELDS="RootFolder=<%=location%>&SortField=Editor&SortDir=<%=getSortDir(VtiSortField.MODIFIEDBY, sortField, sort)%>&View=FileDialog" onmouseover="changeStyle('diidSortEditor');" onmouseout="revertStyle('diidSortEditor');">
         Modified By <% if (sortField.equals(VtiSortField.MODIFIEDBY)) {%><img src='<%=alfContext%>/resources/images/icons/<%= sort.equals(VtiSort.ASC) ? "sort_up.gif" : "sort_down.gif" %>' width='10' height='6' alt='' style='border-width:0px;'/> <% } %></a></th>
		<th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortLast_x0020_Modified" TITLE="Sort by Modified" SORTINGFIELDS="RootFolder=<%=location%>&SortField=Last_x0020_Modified&SortDir=<%=getSortDir(VtiSortField.MODIFIED, sortField, sort)%>&View=FileDialog" onmouseover="changeStyle('diidSortLast_x0020_Modified');" onmouseout="revertStyle('diidSortLast_x0020_Modified');">
		 Modified <% if (sortField.equals(VtiSortField.MODIFIED)) {%><img src='<%=alfContext%>/resources/images/icons/<%= sort.equals(VtiSort.ASC) ? "sort_up.gif" : "sort_down.gif" %>' width='10' height='6' alt='' style='border-width:0px;'/> <% } %></a></th>
        <th style="padding: 2px; text-align: left" class="recordSetHeader"><a class="header" ID="diidSortCheckedOutTitle" TITLE="Sort by Checked Out To" SORTINGFIELDS="RootFolder=<%=location%>&SortField=CheckedOutTitle&SortDir=<%=getSortDir(VtiSortField.CHECKEDOUTTO, sortField, sort)%>&View=FileDialog" onmouseover="changeStyle('diidSortCheckedOutTitle');" onmouseout="revertStyle('diidSortCheckedOutTitle');">
         Checked Out To <% if (sortField.equals(VtiSortField.CHECKEDOUTTO)) {%><img src='<%=alfContext%>/resources/images/icons/<%= sort.equals(VtiSort.ASC) ? "sort_up.gif" : "sort_down.gif" %>' width='10' height='6' alt='' style='border-width:0px;'/> <% } %></a></th>
	</tr>
	
	<tr height="5">
		<td colspan="5"> </td>
	</tr>
    
 <% for(DialogMetaInfo item : items)
    {
        if (item.isFolder())
        {%>
          <tr class="recordSetRow"  fileattribute=folder ID="http://<%=host%><%=context%>/<%=item.getPath()%>" onmousedown="selectrow('http://<%=host%><%=context%>/<%=item.getPath()%>')">
              <td style="padding: 2px; text-align: left"><IMG BORDER=0 ALT="Icon" SRC="<%=request.getContextPath()%>/resources/images/icons/space-icon-default-16.gif"></td>
              <td style="text-align: left"><%=item.getName()%></td>
              <td style="text-align: left"><%=item.getModifiedBy()%></td>
              <td style="text-align: left"><%=item.getModifiedTime()%></td>
              <td style="text-align: left">&nbsp;</td>
          </tr>                
     <% } else { %>
          <tr class="recordSetRow" fileattribute=file ID="http://<%=host%><%=context%>/<%=item.getPath()%>" onmousedown="selectrow('http://<%=host%><%=context%>/<%=item.getPath()%>')">
              <td style="padding: 2px; text-align: left"><IMG BORDER=0 ALT="Icon" SRC="<%=request.getContextPath()%>/resources/<%=DialogUtils.getFileTypeImage(request.getSession().getServletContext(), item.getName())%>"></td>
              <td style="text-align: left"><%=item.getName()%></td>
              <td style="text-align: left"><%=item.getModifiedBy()%></td>
              <td style="text-align: left"><%=item.getModifiedTime()%></td>
              <td style="text-align: left"><%=(item.getCheckedOutTo().equals("") ? "&nbsp;" : item.getCheckedOutTo())%></td>
          </tr>                
   <% }} %>
 
    </table>
</BODY>
</HTML>


<%!

	private String getSortDir(VtiSortField sortField, VtiSortField currentSortField, VtiSort sort)
    {
        if (sortField.equals(currentSortField))
        {
            if (sort.equals(VtiSort.ASC))
            {                
                return VtiSort.DESC.toString();
            }
            if (sort.equals(VtiSort.DESC))
            {                
                return VtiSort.ASC.toString();
            }
        }
        return VtiSort.ASC.toString();        
    }

%>