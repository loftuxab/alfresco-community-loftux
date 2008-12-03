<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />

<#else>

	<#if container == "iframe">
		<IFRAME src="${src}"></IFRAME>
	<#else>
		<#if container == "div">

			<div id="include${instance.id}">
			</div>
			<script language="Javascript">

				var divId = 'include${instance.id}';
				var div = $(divId);

				// ajax load
				var ajax = new Ajax("${src}", {
					method: 'get',
					onComplete: (function(data) {

						data = data.replace('${r"${app.context}"}', '${app.context}');
						this.innerHTML = data;

					}).bind(div),
					onFailure: (function(data) {

						this.innerHTML = "Unable to load component: ${src}";

					}).bind(div)
				}).request();

			</script>

		</#if>
	</#if>

</#if>