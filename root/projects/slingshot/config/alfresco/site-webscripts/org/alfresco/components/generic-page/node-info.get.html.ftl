<script type="text/javascript">//<![CDATA[
    var ni = new Alfresco.NodeInfo("${args.htmlid}").setOptions(
	{
		nodeId: "${page.url.args.path!''}"
	});
	ni.setMessages(
      ${messages}
    );
//]]></script>

   <div id="${args.htmlid}-node-info-titleBar" class="node-info-titlebar" >
	   <span id="${args.htmlid}-listtitle" class="title"></span>
       <span class="backLink">
			<a href="${url.context}/page/generic-page">
				${msg("header.back")}
			</a>
		</span>
   </div>
   <br />
   <span  class="list-title">${msg("title.node-info")}</span>
   <table>
      <tr>
        <td><b>${msg("title.type")}</b>&nbsp;:&nbsp;&nbsp;</td><td id="${args.htmlid}-node-type"></td>
      </tr>
      <tr>
          <td><b>${msg("title.reference")}</b>&nbsp;:&nbsp;&nbsp;</td><td id="${args.htmlid}-node-reference"></td>
      </tr>
      <tr>
          <td><b>${msg("title.url")}</b>&nbsp;:&nbsp;&nbsp;</td><td id="${args.htmlid}-node-url"></td>
      </tr>
   </table>
   <br />
    <span  class="list-title">${msg("title.properties")}</span>
    <div id="${args.htmlid}-body" class="node-info-body" >
	    <div  id="${args.htmlid}-node-info-list"> </div>
    </div>
   <br />
    <span  class="list-title">${msg("title.aspects")}</span>
    <div id="${args.htmlid}-body" class="node-info-body" >
	    <div  id="${args.htmlid}-node-info-aspects"> </div>
    </div>
    
<p/>
<p/>
<p/>