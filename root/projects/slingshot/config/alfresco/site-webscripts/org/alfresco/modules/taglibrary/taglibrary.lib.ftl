<#--
   Renders the tag input fields for the form

    @param htmlid (String) the id to use
    @param tags (array) an array of tags
    @param tagInputName the name to use for the input field
-->
<#macro renderTagInputs htmlid tags tagInputName>
   <#list tags as tag>
      <input type="hidden" name="${tagInputName}[]" value="${tag}" />
   </#list>
</#macro>


<#--
   Outputs the passed JavaScript array *of strings* as a JSON array
   
   @param arr the javascript array to render
-->
<#macro toJSONArray arr>
[
   <#list arr as x>"${x?j_string}"<#if x_has_next>, </#if></#list>
]
</#macro>


<#-- 
   Renders the tag library component
   
   @param htmlid the html id to use for the component
   @param tags the current tags to display
-->
<#macro renderTagLibrary htmlid tags>
<script type="text/javascript">//<![CDATA[
   new Alfresco.TagLibrary("${htmlid}").setOptions(
   {
      //siteId: "{page.url.templateArgs.site}",
      //tags: [
      //topicId: "{item.name}",
      //topicRef: "{item.nodeRef}"
   }).setMessages(
      ${messages}
   ).setCurrentTags(
     <@toJSONArray tags />
   );
//]]></script>

<div class="taglibcontainer">
   <label>Tags:</label>
   <div class="tags">
      <div class="top_taglist tags_box">
         <ul id="${htmlid}-current-tags">
         <#list tags as tag>
            <li id="${htmlid}-onRemoveTag-${tag}">
               <a href="#" class="taglibrary-action">${tag}
                  <span class="close">&nbsp;
                     <!-- <img src="/modules/taglibrary/images/icon_close.gif" alt="x" /> -->
                  </span>
               </a>
            </li>
         </#list>
         </ul>
      </div>
      <br class="clear" />
      <div class="title">Type Tag(s):</div>
      <input type="text" size="30" class="rel_left" id="${htmlid}-tag-input-field" />
      <input type="button" id="${htmlid}-add-tag-button" value="Add" />
      <br class="clear" />
      <div class="bottom_taglist tags_box">
         <a href="#" id="${htmlid}-load-popular-tags-link">Choose from popular tags in this site</a>
         
         <#-- Following list contains the popular tags, loaded by AJAX on users request -->
         <ul id="${htmlid}-popular-tags">
         </ul>
      </div>
      <br class="clear" />
   </div>
</div>
</#macro>
