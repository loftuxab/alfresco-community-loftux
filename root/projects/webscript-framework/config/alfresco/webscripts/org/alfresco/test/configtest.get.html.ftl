<h3>Testing global config...</h3>

foo element name attribute = ${config.global.foo.attributes["name"]}<br/>
foo element title attribute = ${config.global.foo.attributes["title"]}<br/>

foo element has ${config.global.foo.children?size} children:
<ul>
<#list config.global.foo.children as child>
<li>element with name of '${child.name}'</li>
</#list>
</ul>
id of first child = ${config.global.foo.children[0].attributes["id"]}<br/>
value of bar3Id = ${config.global.foo.children[2].value}<br/>
value of baz = ${config.global.foo.children[3].value}

<#assign childMap=config.global.foo.childrenMap>
There are ${childMap["bar"]?size} bar elements<br/>
value of single baz element is ${childMap["baz"][0].value}

<h3>Testing scoped config...</h3>

<#assign scopedCfg=config.scoped["ServerConfigElementTest"]>
scheme = ${scopedCfg.server.scheme}<br/>
hostname = ${scopedCfg.server.hostName}<br/>
port = ${scopedCfg.server.port}<br/>

<h3>Testing overridden config...</h3>

param in global config (should be 'hello') = ${config.global["param"].value}<br/>
param in scoped config (should be 'goodbye') = ${config.scoped["OverrideTest"].param.value}<br/>

<h3>Testing script config...</h3>
id of test element = ${config.script.test.@id}<br/>
value of first bar element = ${config.script.test.foo.bar[0]}<br/>
values of all bar elements:<br/><ul>
<#list config.script.test.foo.bar as b>
<li>${b}</li>
</#list>
</ul>