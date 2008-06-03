<#macro communityheader>

<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.5.1/build/reset-fonts-grids/reset-fonts-grids.css" />
<link rel="stylesheet" type="text/css" href="css/common.css" />
<link rel="stylesheet" type="text/css" href="css/community.css" />
<!--[if IE]><link rel="stylesheet" type="text/css" href="css/ie.css" media="screen"/><![endif]-->

</#macro>

<#macro enterpriseheader>

<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.5.1/build/reset-fonts-grids/reset-fonts-grids.css" />
<link rel="stylesheet" type="text/css" href="css/common.css" />
<link rel="stylesheet" type="text/css" href="css/enterprise.css" />
<!--[if IE]><link rel="stylesheet" type="text/css" href="css/ie.css" media="screen"/><![endif]-->

</#macro>

<#macro header>

<#if alfEnterprise == true>
	<@enterpriseheader/>
<#else>
	<@communityheader/>
</#if>

</#macro>