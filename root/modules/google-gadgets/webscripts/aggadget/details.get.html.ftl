<html>
   <head>
      <title>Details</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css">
      <style type="text/css">
div.details
{
   padding: 2 4 6 4;
}

tr
{
   vertical-align: top;
}
      </style>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Details for '${node.name}'</div>
      <div class="dialog">
         <div class="details">
            <table cellspacing="0" cellpadding="0" border="0" width="100%">
               <tr><td style="vertical-align:middle" rowspan="99" width="44" align="left"><#if node.isDocument><a href="${url.context}${node.url}" target="alfnew"></#if><img src="${url.context}${node.icon32}" width="32" height="32" border="0" alt="${node.name?html}" title="${node.name?html}"><#if node.isDocument></a></#if></td>
                   <td><span class="metaTitle">Name:</span></td><td rowspan="99" width="8"></td><td><span class="metaData">${node.name?html}</span></td></tr>
               <tr><td><span class="metaTitle">Title:</span></td><td><span class="metaData"><#if node.properties.title?exists>${node.properties.title?html}<#else>&nbsp;</#if></span></td></tr>
               <tr><td><span class="metaTitle">Description:</span></td><td><span class="metaData"><#if node.properties.description?exists>${node.properties.description?html}<#else>&nbsp;</#if></span></td></tr>
               <tr><td><span class="metaTitle">Modified:</span></td><td><span class="metaData">${node.properties.modified?datetime}</span></td></tr>
               <tr><td><span class="metaTitle">Modified By:</span></td><td><span class="metaData">${node.properties.modifier?html}</span></td></tr>
               <tr><td><span class="metaTitle">Created:</span></td><td><span class="metaData">${node.properties.created?datetime}</span></td></tr>
               <tr><td><span class="metaTitle">Created By:</span></td><td><span class="metaData">${node.properties.creator?html}</span></td></tr>
               <#if node.isDocument>
               <tr><td><span class="metaTitle">Content Type:</span></td><td><span class="metaData">${node.displayMimetype!"<i>Unknown</i>"}</span></td></tr>
               <tr><td><span class="metaTitle">Encoding:</span></td><td><span class="metaData">${node.encoding!"<i>Unknown</i>"}</span></td></tr>
               <tr><td><span class="metaTitle">Size:</span></td><td><span class="metaData">${(node.size/1000)?string("0.##")}&nbsp;KB</span></td></tr>
               </#if>
            </table>
         </div>
         <div style="display:table"><input type="button" onclick="javascript:history.back();" value="Continue"></div>
      </div>
   </div>
   
   </body>
</html>