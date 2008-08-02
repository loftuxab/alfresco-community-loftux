 <p><h1>Alfresco Knowledge Base Search</h1></p>
    <input id="searchText" type="text" onKeyPress="{if (event.keyCode==13) textsearch();}">     
    <input type="Submit" onClick="textsearch()"/>
    <input onclick="resetSearchArticles()" type="button" value="Reset"/>
<div><a id="toggleadvanced" href="javascript:categorydisplay();">Advanced&gt;&gt;</a></div>
<div id="categorydisplay"  style="display:none">
<!-- TODO Hardcoded KB categories, which has to be updated-->
<table border="0">
<tr>
	<td valign="top">
		<label for="alfresco_version">Alfresco Version(s)</label>
		<select name="alfresco_version" id="alfresco_version"  onchange="javascript:setAlfrescoversion('');">
		<option value="Any" selected=selected>Any</option>
		<option value="workspace://SpacesStore/kb:version-1-2">1.2</option>
		<option value="workspace://SpacesStore/kb:version-1-4">1.4</option>
		<option value="workspace://SpacesStore/kb:version1.4.3">1.4.3</option>
		<option value="workspace://SpacesStore/kb:version2-0">2.0</option>
		<option value="workspace://SpacesStore/kb:version-2-0-1">2.2</option>
		</select>
	</td>
	<td valign="top">&nbsp;| Type&nbsp;</td><td valign="top">
		<select name="article_type" id="article_type" onchange="javascript:setArticletype('');">
		<option value="workspace://SpacesStore/kb:type-any">Any</option>
		<option value="workspace://SpacesStore/kb:type-article">Article</option>
		<option value="workspace://SpacesStore/kb:type-faq">FAQ</option>
		<option value="workspace://SpacesStore/kb:type-white-paper">White Paper</option>
		</select>
	</td>
</tr>
<tr></td></tr>
</table>
</div>