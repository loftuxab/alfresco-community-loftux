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
    <td>Container</td>
    <td>
    	<select name="${container.id}">
    		<option value="iframe" <#if container.value =="iframe">selected</#if> >IFRAME</option>
    		<option value="div" <#if container.value =="div">selected</#if>>DIV</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <hr/>
    </td>
  </tr>
  <tr>
    <td>Source Type</td>
    <td>
       <select name="${sourceType.id}">
          <option name="url" <#if sourceType.value == 'url'>selected</#if> >URL</option>
          <option name="space" <#if sourceType.value == 'space'>selected</#if> >Space Content</option>
          <option name="site" <#if sourceType.value == 'site'>selected</#if> >Site Content</option>
          <option name="webapp" <#if sourceType.value == 'webapp'>selected</#if> >Webapp Content</option>
       </select>
    </td>
  </tr>
  <tr>
    <td>Source Endpoint</td>
    <td><input type="text" name="${sourceEndpoint.id}" value="${sourceEndpoint.value}"/></td>
  </tr>
  <tr>
    <td>Source Value</td>
    <td><input type="text" name="${sourceValue.id}" value="${sourceValue.value}"/></td>
  </tr>
</table>

