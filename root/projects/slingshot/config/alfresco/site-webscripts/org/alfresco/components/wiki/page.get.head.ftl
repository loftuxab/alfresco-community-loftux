<#include "../component.head.inc">
<!-- Wiki -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/components/wiki/wiki.css" />
<link rel="alternate" type="application/wiki" href="${page.url.servletContext}/site/${page.url.templateArgs.site}/wiki-page?title=${(page.url.args.title!"")?url}&amp;action=edit" />
<@script type="text/javascript" src="${page.url.context}/components/wiki/parser.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/wiki/page.js"></@script>
<!-- Wiki Editor -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/simple-editor.css" />
<@script type="text/javascript" src="${page.url.context}/modules/simple-editor.js"></@script>
<!-- Tag -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/taglibrary/taglibrary.css" />
<@script type="text/javascript" src="${page.url.context}/modules/taglibrary/taglibrary.js"></@script>
<!-- Wiki Versioning -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/wiki/revert-wiki-version.css" />
<@script type="text/javascript" src="${page.url.context}/modules/wiki/revert-wiki-version.js"></@script>