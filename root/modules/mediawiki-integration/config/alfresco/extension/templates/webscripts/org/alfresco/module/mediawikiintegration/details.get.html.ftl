<html>

<head>
	<title>MediaWiki Details</title>
</head>

<body>
	<b>MediaWiki Details</b><br>	
	<br>
	<table cellpadding='2' cellspacing='2'>	
		<tr>
			<td>Name:&nbsp;&nbsp;</td>
			<td>${config.properties["mwcp:wgSitename"]?html}</td>
		</tr>
	</table>
	<br>
	<b>MediaWiki Links</b><br>
	<br>
	<table cellpadding='2' cellspacing='2'>
		<tr><td><a href="${absurl(url.context)}/php/wiki/index.php?mediaWikiSpace=${mediawiki.nodeRef?string}" target="new">MediaWiki Main Page</a></td></tr>
		<!-- TODO hide this link if the user doesn't have the correct rights to edit the properties -->
		<tr><td><a href="${absurl(url.context)}/command/ui/editcontentprops?noderef=${config.nodeRef?string}">Edit MediaWiki Configuration Values</a></td></tr>
	</table>	 	
</body>

</html>