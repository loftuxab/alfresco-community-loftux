<table class="AlfrescoComponentEditor">
  <tr>
    <td>Title</td>
    <td><input type="text" size="40" name="${title.id}" value="${title.value}"></td>
  </tr>
  <tr>
    <td>Description</td>
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
    <td>Orientation</td>
    <td>
    	<select name="${renderer.id}">
    	    <option value="horizontal" <#if renderer.value == "horizontal">selected</#if> >Horizontal</option>
    	    <option value="vertical" <#if renderer.value == "horizontal">selected</#if> >Vertical</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td>Background Color</td>
    <td>
    	<input type="text" size="40" id="${backgroundColor.id}" name="${backgroundColor.id}" value="${backgroundColor.value}">
    </td>
  </tr>
  <tr>
    <td>Root Node</td>
    <td>
    	<select name="${rootNode.id}">
    	    <option value="siteroot" <#if renderer.value == "siteroot">selected</#if> >Site Root Page</option>
    	    <option value="currentpage" <#if renderer.value == "currentpage">selected</#if> >Current Page</option>
    	</select>
    </td>
  </tr>
</table>