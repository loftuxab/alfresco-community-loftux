<#-- Include for Alfresco: Google Analytics -->
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-1936916-12");
pageTracker._initData();
pageTracker._trackPageview();
</script>
<#-- End of Include for Alfresco: Google Analytics -->


<style type="text/css">
<!--
.header2, .header2 a {
	color: #333333;
	height: 14px;
	padding: 2px 2px 2px 2px;
	font-family: Arial, Sans;
	font-size: 18px;		
}

.join-network{
    padding: 13px 13px 13px 13px;
}
.join-network-button {
    border: 0px;
}
.join-network-headline {
    font-family: Arial, Sans;
    font-size: 16px;
    color: #008500;
}
.join-network-description{
    font-family: Arial, Sans;
    font-size: 13px;
    color: #333333;
}

.bg-gradient{
    background: url(../images/networknews/bg-gradient.jpg) repeat-x top left;
}

.news-table{
    border-top-color: #999999;
    border-top-style: solid;
    border-top-width: 1px;
    padding-bottom: 20px;
    padding-top: 20px;
    padding-left: 13px;
}
.plugin-table{
    border-top-color: #cccccc;
    border-top-style: dashed;
    border-top-width: 1px;
    padding-bottom: 20px;
    padding-top: 20px;
    padding-left: 13px;
}

.news-item-thumbnail {
	vertical-align: top;
}
.news-item-headline, .plugin-headline{
	padding: 1px 0 1px 0;
	margin: 0 0 0 0;
	vertical-align: top;
	font-family: Arial, Sans;
	font-size: 14px;
    color: #42B11A;
}
.news-item-headline a, .plugin-headline a {
	color:#42B11A;
	font-size: 14px;
	text-decoration: none;
}
.plugin-headline p{
    color: #999999;
    font-size: 13px;
	padding: 4px 0 4px 0;
	margin: 0 0 0 0;
}
.plugin-thumbnail {
	vertical-align: top;
}

body
{
	padding: 0px 0px 0px 0px;
	margin: 0px 0px 0px 0px;
	border: 0px 0px 0px 0px;
}

-->
</style>



<!-- join network section -->
<div class="join-network">
    <table width="100%">
        <tr>
            <td class="join-network-headline">Explore the Alfresco Community Network</td>
        </tr>
        <tr>
            <td class="join-network-description">Connect with other Alfresco community members to share ideas, experiences and resources.</td>
        </tr>
        <tr>
            <td><a target="_blank" href="http://network.alfresco.com?ref=30b1labs"><img class="join-network-button" src="${url.context}/images/networknews/join-btn.png" width="97" height="28" alt="Join Today!"/></a></td>
        </tr>
    </table>
</div>



<!-- news section header -->

<div class="bg-gradient">
    <table  width="100%" class="news-table">
        <tr>
            <td  colspan="2" class="header2">
                Latest News
            </td>
        </tr>

    <#assign newsCount = 0>
    <#list newsItems as newsItem>

        <#if newsCount < maxNewsCount>

            <tr>
            <td class="news-item-thumbnail">
                <img src="${url.context}/images/networknews/news-icon.png" width="15" height="15" />
            </td>
            <td class="news-item-headline" width="100%">
                <a target="_blank" href="http://network.alfresco.com/extranet/?f=default&o=${newsItem.nodeRef}">
                ${newsItem.headline}
                </a>
            </td>
            </tr>

        </#if>

        <#assign newsCount = newsCount + 1>

    </#list>
    </table>
</div>

<!-- plugins section header -->
<div class="bg-gradient">
    <table width="100%" class="plugin-table">
        <tr>
            <td class="header2" colspan="2">
                Latest Plugins / Uploads
            </td>
    </tr>


    <#list assets as asset>

        <#assign title = asset.headline>
        <#if !title?exists || title == "">
            <#assign title = asset.title>
            <#if !title?exists || title == "">
                <#assign title = assist.name>
            </#if>
        </#if>

        <tr>
        <td class="plugin-thumbnail">
            <img src="${url.context}/images/networknews/plugins-icon.png" width="16" height="16" />
        </td>
        <td class="plugin-headline" width="100%">
            <a target="_blank" href="http://network.alfresco.com/extranet/?f=default&o=${asset.nodeRef}">
                ${title}
            </a>
            <p>
                ${asset.description}
            </p>
        </td>

    </#list>
    </table>
</div>


<#-- Include for Alfresco: Eloqua -->
<SCRIPT TYPE='text/javascript' LANGUAGE='JavaScript' SRC='http://www.alfresco.com/elqNow/elqCfg.js'></SCRIPT>
<SCRIPT TYPE='text/javascript' LANGUAGE='JavaScript' SRC='http://www.alfresco.com/elqNow/elqImg.js'></SCRIPT>
<#-- End of Include for Alfresco: Eloqua -->