<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	
<#else>

	<#if isVideo>

		<#if useQuicktime>

			<object classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" codebase="http://www.apple.com/qtactivex/qtplugin.cab" width="320" height="256">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />
				<param name="pluginspage" value="http://www.apple.com/quicktime/download/" />

				<!--[if !IE]> <-->

				<object type="${mimetype}" data="${src}" width="320" height="256">
					<param name="pluginurl" value="http://www.apple.com/quicktime/download/" />
					<param name="controller" value="true" />
					<param name="autoplay" value="true" />
					<param name="autostart" value="1" />

					<embed src="${src}" type="${src}" width="320" height="256" autostart="true" controller="true" ></embed>

				</object>

				<!--> <![endif]-->

			</object>

		</#if>

		<#if useWindowsMedia>

			<object classid="6BF52A52-394A-11d3-B153-00C04F79FAA6" codebase="http://activex.microsoft.com/activex/controls/mplayer/en/ nsmp2inf.cab#Version=6,0,02,902" width="320" height="256">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />

				<!--[if !IE]> <-->

				<object type="${mimetype}" data="${src}" width="320" height="256">
					<param name="controller" value="true" />
					<param name="autoplay" value="true" />
					<param name="autostart" value="1" />

					<embed src="${src}" type="${src}" width="320" height="256" autostart="true" controller="true" ></embed>

				</object>

				<!--> <![endif]-->

			</object>

		</#if>

		<#if useReal>

			<object id=RVOCX classid="clsid:CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA" width="320" height="256">
				<param name="src" value="${src}" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />
				<param name="controls" value="ImageWindow">
				<param name="console" value="video">

				<!--[if !IE]> <-->

				<object type="${mimetype}" data="${src}" width="320" height="256">
					<param name="autoplay" value="true" />
					<param name="autostart" value="1" />
					<param name="controls" value="ImageWindow">
					<param name="console" value="video">

					<embed src="${src}" type="${src}" width="320" height="256" autostart="true" controller="true" ></embed>

				</object>

				<!--> <![endif]-->

			</object>

		</#if>

		<#if useShockwave>

			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,22,0" width="320" height="256">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />

				<!--[if !IE]> <-->

				<object type="${mimetype}" data="${src}" width="320" height="256">
					<param name="controller" value="true" />
					<param name="autoplay" value="true" />
					<param name="autostart" value="1" />

					<embed src="${src}" type="${src}" width="320" height="256" autostart="true" controller="true" ></embed>

				</object>

				<!--> <![endif]-->

			</object>

		</#if>

		<#if useJW>

			<div id="container">
				<a href="http://www.macromedia.com/go/getflashplayer">Get the Flash Player</a> to see this player.
			</div>
			<script type="text/javascript">
				var s1 = new SWFObject("${swfObjectPath}","ply","328","200","9","#FFFFFF");
				s1.addParam("allowfullscreen","true");
				s1.addParam("allowscriptaccess","always");
				s1.addParam("flashvars","file=${src}&image=${swfImagePreview}");
				s1.write("container");
			</script>

		</#if>

	<#else>

		${msg}

	</#if>
	
</#if>