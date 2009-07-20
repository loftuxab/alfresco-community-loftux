<script type="text/javascript" charset="utf-8">
 new Alfresco.RM.References("${args.htmlid}").setMessages(${messages});
</script>
<div id="${args.htmlid}" class="manageReferences">
   <h2 class="title">${msg("label.title",'NAME HERE')}</h2>
   <button type="button" id="manageReferences-newReference" name="manageReferences-newReference" value="newRef" class="newRef">${msg('label.new-reference')}</button>
       <ol>
           <#list references as ref>
           <li><span>${ref.displayName?html}</span><button id="editReference-but-${ref.id?html}" class="editRef refAction" value="${ref.id?html}">${msg('label.edit')}</button><button id="deleteReference-but-${ref.id?html}" class="deleteRef refAction" value="delete-id">${msg('label.delete')}</button></li>
           </#list>
       </ol>
       <div class="componentFtr">
          <button id="manageReferences-doneRef" class="doneRef refAction" value="done">${msg('label.done')}</button>
       </div>
</div>    