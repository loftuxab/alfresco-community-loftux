 <p><h1>Alfresco Knowledge Base Search</h1></p>
    <input id="searchText" type="text" onKeyPress="{if (event.keyCode==13) textsearch();}">     
    <input type="Submit" onClick="textsearch()"/>
    <input onclick="resetSearchArticles()" type="button" value="Reset"/>
<div><a id="toggleadvanced" href="javascript:categorydisplay();">Advanced&gt;&gt;</a></div>
<div id="categorydisplay"  style="display:none">
<!--KB categories are loaded dynamically through webscript -->
<table border="0">
<tr>
	<td valign="top">
		<label for="alfresco_version">Alfresco Version(s)</label>
		<select name="alfresco_version" id="alfresco_version"  onchange="javascript:setAlfrescoversion('');">
		<option value="Any" selected=selected>Any</option>
		</select>
	</td>
	<td valign="top">&nbsp;| Type&nbsp;</td><td valign="top">
		<select name="article_type" id="article_type" onchange="javascript:setArticletype('');">
		</select>
	</td>
</tr>
<tr></td></tr>
</table>
</div>
<input type="hidden" id="refreshed" value="no">