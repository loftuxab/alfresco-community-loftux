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
    <td>Fetch</td>
    <td>
       <select name="${fetch.id}">
          <option value="url" <#if fetch.value == 'url'>selected</#if> >URL</option>
          <option value="space" <#if fetch.value == 'space'>selected</#if> >Spaces Content</option>
          <option value="site" <#if fetch.value == 'site'>selected</#if> >Sites Content</option>
          <option value="webapp" <#if fetch.value == 'webapp'>selected</#if> >Webapp Content</option>
       </select>
    </td>
  </tr>
  <tr>
    <td>Source</td>
    <td><input type="text" name="${source.id}" value="${source.value}"/></td>
  </tr>
  <tr>
    <td colspan="2">
      <hr/>
    </td>
  </tr>
  <tr>
    <td>Image Text</td>
    <td><input type="text" name="${imageText.id}" value="${imageText.value}"/></td>
  </tr>
</table>

