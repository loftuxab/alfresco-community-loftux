<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	
<#else>

	<div id="container"><a href="http://www.macromedia.com/go/getflashplayer">Get the Flash Player</a> to see this player.</div>
	<script type="text/javascript" src="${url.context}/components/common/jw-player/swfobject.js"></script>

	<script type="text/javascript">
		var s1 = new SWFObject("${url.context}/components/common/jw-player/player.swf","ply","328","200","9","#FFFFFF");
		s1.addParam("allowfullscreen","true");
		s1.addParam("allowscriptaccess","always");
		s1.addParam("flashvars","file=${src}&image=${previewImageUrl}");
		s1.addParam("wmode", "transparent");
		s1.write("container");
	</script>
	
</#if>

