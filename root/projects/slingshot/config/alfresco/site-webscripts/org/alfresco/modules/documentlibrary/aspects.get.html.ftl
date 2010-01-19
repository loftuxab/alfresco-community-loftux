<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${args.htmlid}").setOptions(
   {
      visible: [<#list aspects.visible as a>"${a}"<#if a_has_next>,</#if></#list>],
      addable: [<#list aspects.addable as a>"${a}"<#if a_has_next>,</#if></#list>],
      removeable: [<#list aspects.removeable as a>"${a}"<#if a_has_next>,</#if></#list>]
   }).setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-dialog" class="aspects">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <input type="hidden" name="added" id="${args.htmlid}-added" value="" />
         <input type="hidden" name="removed" id="${args.htmlid}-removed" value="" />
         <div class="yui-g">
            <h2>${msg("header.aspects")}</h2>
         </div>
         <div class="yui-g">
            <div class="yui-u first">
               <div class="title-left">${msg("title.addable")}</div>
               <div id="${args.htmlid}-left" class="list-left"></div>
            </div>
            <div class="yui-u">
               <div class="title-right">${msg("title.current")}</div>
               <div id="${args.htmlid}-right" class="list-right"></div>
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.apply")}" tabindex="6" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="7" />
         </div>
      </form>
   </div>
</div>