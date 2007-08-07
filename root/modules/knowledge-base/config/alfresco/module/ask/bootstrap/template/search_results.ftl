<#assign articleHome = companyhome.childByNamePath["Knowledge Base"]>

<#assign query="+PATH:\"${ articleHome.qnamePath}//.\" +ASPECT:\"{ask.new.model}article\"">
<#assign query= query +" +@cm\\:content.mimetype:\"text/xml\"">
<#if !args.status?exists>
         <#assign status = "current"/>
            <#else>
         <#assign status = args.status/>
 <#if status!='Any'>
  <#assign query=query+ " +@ask\\:status:\"+${status}+\""/>
             </#if>
 </#if>
                  <#assign tmpquery="&status=${status}">

<#if args.article_modifier?exists>
         
          <#if args.article_modifier!="Any" & args.article_modifier!="">
            <#assign query=query + " +@cm\\:modifier:\"${ args.article_modifier}\"">
                  
          </#if>
        </#if>

<#if args.alfresco_version?exists>
         
          <#if args.alfresco_version!="Any" & args.alfresco_version!="">
            <#assign query=query + " +@cm\\:alfresco_version:\"${args.alfresco_version}\"">
                   
          </#if>
        </#if>

<#if args.visibility?exists>
         
          <#if args.visibility!="Any" & args.visibility!="">
            <#assign query=query + " +@ask\\:visibility:\"${ args.visibility}\"">
                   
          </#if>
        </#if>

<#if args.modified?exists>
            <#if args.modified != "">
               

                    <#-- Format the modified date argument so we can use it in a Lucene query -->
                  <#assign date1 = "${args.modified}"?date("dd/MM/yyyy")?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>
                  <#assign fromDate="${'${date1}'?replace('-','\\\\-')}">
                    <#assign currentdate=date?string("yyyy\\-MM\\-dd'T'HH:mm:ss'.00Z'")>
                    <#assign date_range="[${fromDate} TO ${currentdate}]">
                    <#assign query = query + " +@cm\\:modified:${date_range}">   
                                    
            </#if>
        </#if>

<#-- Add or define the max results to display -->
       
        <#if args.maxresults?exists>
                    <#assign maxresults=args.maxresults ?number>
        <#else>
                    <#assign maxresults=5>
        </#if>
       

<#if !args.askid?exists><#assign askid = ""/>
            <#else>
            <#assign askid = args.askid/>
         <#if askid!=''>
            <#assign query=query+ " +@ask\\:askid:\"+${askid}+\""/>
             </#if></#if>

<#if !args.article_type?exists><#assign article_type = "Article"/>
            <#else>
  
            <#assign article_type = args.article_type/>
           <#if article_type!='Any' & article_type!=''>
             <#assign query=query+ " +@ask\\:article_type:\"+${article_type}+\""/>
               
             </#if>
</#if>

<#if !args.searchText?exists><#assign searchString = ""/>
<#else><#assign searchString = args.searchText/>
<#if args.searchText?exists && args.searchText != "">
                    

              <#assign query=query + '+('>
            <#assign query=query + ' TEXT:"' + searchString + '"'>
            <#assign count=1>
            <#assign searchTermList = searchString?split(" ")>
            <#assign testquery="">
            <#if (searchTermList?size > 1)>
                <#list searchTermList as term>
                    <#assign query=query + ' TEXT:"' + term?lower_case + '*"'>
                                       
                </#list>
            </#if>
              <#assign query=query + ')'>
        </#if>
</#if>
<#assign pagination='false'>
<#assign p=1>

  <#assign index=0>


<#-- Run the article query -->
                
                  <#assign results = companyhome.childrenByLuceneSearch[query]>
                
        <#-- If we have no results return a no results message, else, return the list of matching articles -->
        <#if results?size = 0>
          <br/>
            <table border="0" cellpadding="0" cellspacing="0"  width="100%">
            <tr align="left"><td align="left" style="font-size:130%" class="recordSetHeader">Sorry, no articles matched your search criteria</td></tr>
            </table>
            <!--<table style="margin-left:5px;">-->
        <#else>
                        <#assign total=results?size>
                        <#assign pages=total/maxresults>
            <br/>
            <table border="0" cellpadding="0" cellspacing="0"  width="100%">
            <tr align="left"><td align="left" style="font-size:130%" class="recordSetHeader">Search Results - <#if (results?size > maxresults)>Displaying ${maxresults}  of <#else> Displaying ${results?size} of</#if> ${results?size} Article(s) Found</td></tr>
            </table>
            <!--<table style="margin-left:5px;">-->

<table width="100%" border="0">
   
    <tr><td><p><!--Your Query :    
${query}--></p><h3></h3></td></tr>
    <tr><td>
 </td></table>
<#assign flag='true' />
<table border="0" cellpadding="2" cellspacing="2" >
<tr><th>s.no</th><th>URL </th><th>Title</th><th>Status</th><th>Type </th><th>Created By</th><th>Modifier</th><th>date of creation</th><th>date of modification</th><th>visibility</th><th>Tags</th><th>Version</th><th>Approve status</th></tr>
<#list results as child>

<#if args.p?exists && pagination='false'>
                 <#assign p=args.p?number>
                 <#assign pagination='true'>
                  <#assign index=(args.p?number * maxresults) - maxresults>
                  <#assign maxresults=(args.p?number*maxresults?number)>
  </#if>     


<#if (child_index>=index?number)>
<#if flag='true'>
    <tr style="background:#dfe6ed; color: black;" align="center">
     <#assign flag='false' />
   <#else>
   <#assign flag='true' />
   <tr style="background:##ffffff; color: black;" align="center">
   </#if>

<td>${child_index+1}</td>
<td><a href="/alfresco/navigate/showDocDetails/workspace/SpacesStore/${ child.id}" target="new"><img src="/alfresco/images/icons/View_details.gif" border=0 align=absmiddle alt="Article Details" title="Article Details" ></a>

        <a href="/alfresco${child.url}" target="new" alt="View Article" title="View Article">${child.properties.name }</a>
</td>
<td>${child.properties.title}</td>
<td><#if child.properties["ask:status"]?exists>${child.properties["ask:status"]}</#if></td>
<td><#if child.properties["ask:article_type"]?exists>${child.properties["ask:article_type"]}</#if></td>
<td>${child.properties.creator}</td>
<td>${child.properties.modifier }</td>
<td>${child.properties.created?date}</td>

<td>${child.properties.modified ?date}</td>
<td><#if child.properties["ask:visibility"]?exists>${child.properties ["ask:visibility"]}</#if></td>
       
          
         
           <td><#if child.properties["ask:tags"]?exists><#list child.properties["ask:tags"] as tag>${tag}<#if tag_has_next>,</#if></#list><#else>-nil-</#if></td>
           <td> <#if child.properties["cm:alfresco_version"]?exists><#list child.properties["cm:alfresco_version"] as versionValue>${versionValue}<#if versionValue_has_next>,</#if></#list><#else>-</#if></td>
<#-- Link to send for approval -->
            <td>
            <#if child.properties["ask:status"] == "Draft">
                <!-- <a href="javascript:openStartWorkflow('${ child.displayPath}','${child.name}');">Request Approval</a> -->
                <!--<input type="button" value="Send Approval" onclick="javascript:sndWFReq('${ child.id}');">-->
                    <a href="javascript:searchsndWFReq('${child.id}');">Request Approval</a>
                <#else>
                       nil
            </#if>
            </td>       
          <td>
			 <#if !(child.properties["ask:status"] == "Current") && !args.current?exists> 	
		   	<a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/edit_article.ftl&contextPath=${child.displayPath}/${child.name}" target="new">Edit Article</a>&nbsp;&nbsp;
		   </#if>
		   </td>
   
         </tr>
               <#if child_index =maxresults-1>
                        <#assign index=child_index>
                       <#break>
               </#if>
   
  </#if>
       
            </#list>
</table>
<#assign temp=pages?int><#if temp==pages>  <#else><#assign pages=temp+1> </#if>
<table cellspacing=0 cellpadding=0 class="recordSet" width="100%">
<tr><td colspan=99 align=center>
<span class=pager>
Page ${p} of ${pages}
<#if (pages>=2 && p!=1)>  
<a href="#" onclick="javascript:pagination('1');">      
<img src="/alfresco/images/icons/FirstPage.gif" width=16 height=16 border=0 alt="First Page" title="First Page">
</a>
<a href="#" onclick="javascript:pagination('${p-1}');"> 
<img src="/alfresco/images/icons/PreviousPage.gif" width=16 height=16 border=0 alt="Previous Page" title="Previous Page">
</a>
<#else>                                                                             
<img src="/alfresco/images/icons/FirstPage_unavailable.gif" width=16 height=16 border=0>    
<img src="/alfresco/images/icons/PreviousPage_unavailable.gif" width=16 height=16 border=0>  
</#if>
<#assign x=pages?number>
<#list 1..x as i>
 <#if i=p?number>
          <b> ${i}</b>
 <#else>
                <a href="#" onclick="javascript:pagination('${i}');">  ${i}</a>
</#if>
</#list>  
<#if (pages>=2 && p<pages)>
<a href="#" onclick="javascript:pagination('${p+1}');"> 
<img src="/alfresco/images/icons/NextPage.gif" width=16 height=16 border=0 alt="Next Page" title="Next Page">
</a>
<a href="#" onclick="javascript:pagination('${pages}');"> 
<img src="/alfresco/images/icons/LastPage.gif" width=16 height=16 border=0 alt="Last Page" title="Last Page">
</a>
<#else>
<img src="/alfresco/images/icons/NextPage_unavailable.gif" width=16 height=16 border=0>     
<img src="/alfresco/images/icons/LastPage_unavailable.gif" width=16 height=16 border=0>
</#if>

</span>
</td>
</table>

        </#if>