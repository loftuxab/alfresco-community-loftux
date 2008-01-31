<h3>Testing global config...</h3>

foo element name attribute = ${config.global.foo.attributes["name"]}<br/>
foo element title attribute = ${config.global.foo.attributes["title"]}<br/>

foo element has ${config.global.foo.children?size} children:
<ul>
<#list config.global.foo.children as child>
<li>bar element with id of '${child.attributes["id"]}'</li>
</#list>
</ul>
value of bar3Id = ${config.global.foo.children[2].value}

<h3>Testing scoped config...</h3>

<#assign scopedCfg=config.scoped["NotModelAwareTest"]>
scheme = ${scopedCfg.server.scheme}<br/>
hostname = ${scopedCfg.server.hostName}<br/>
port = ${scopedCfg.server.port}<br/>

<h3>Testing overridden config...</h3>

param in global config (should be 'hello') = ${config.global["param"].value}<br/>
param in scoped config (should be 'goodbye') = ${config.scoped["OverrideTest"].param.value}<br/>

