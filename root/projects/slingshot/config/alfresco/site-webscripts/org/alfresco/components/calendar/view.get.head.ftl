<#include "../component.head.inc">
<!-- Calendar View -->
<@script type="text/javascript" src="${page.url.context}/modules/simple-dialog.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/calendar/calendar-event.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/calendar/calendar-view.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/calendar/calendar-view-${page.url.args.view?js_string!'month'}.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/calendar/eventinfo.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/calendar/microformat-parser.js"></@script>
<@script type="text/javascript" src="${page.url.context}/components/calendar/microformats/hcalendar.js"></@script>
<!-- Tag library -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/taglibrary/taglibrary.css" />
<@script type="text/javascript" src="${page.url.context}/modules/taglibrary/taglibrary.js"></@script>