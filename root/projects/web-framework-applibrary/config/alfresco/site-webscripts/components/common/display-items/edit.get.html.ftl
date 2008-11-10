<table class="AlfrescoComponentEditor">
  <tr>
    <td class="label">Title</td>
    <td>
    	<input type="text" size="40" name="${title.id}" value="${title.value}">
    </td>
  </tr>
  <tr>
    <td class="label">Description</td>
    <td>
    	<textarea rows="3" size="40" name="${description.id}">${description.value}</textarea>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <hr/>
    </td>
  </tr>
  <tr>
    <td class="label">View</td>
    <td>
    	<select name="${view.id}">
    	    <option value="views/list" <#if view.value == "views/list">selected</#if> >List View</option>
    	    <option value="views/4grid" <#if view.value == "views/4grid">selected</#if> >Four-by-four View</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td class="label">Icon Size</td>
    <td>
    	<select name="${iconSize.id}">
    	    <option value="16" <#if view.value == "16">selected</#if> >16 pixels</option>
    	    <option value="24" <#if view.value == "24">selected</#if> >24 pixels</option>
    	    <option value="32" <#if view.value == "32">selected</#if> >32 pixels</option>
    	    <option value="48" <#if view.value == "48">selected</#if> >48 pixels</option>
    	    <option value="64" <#if view.value == "64">selected</#if> >64 pixels</option>
    	    <option value="72" <#if view.value == "72">selected</#if> >72 pixels</option>
    	    <option value="96" <#if view.value == "96">selected</#if> >96 pixels</option>
    	    <option value="128" <#if view.value == "128">selected</#if> >128 pixels</option>
    	</select>
    </td>
  </tr>
</table>