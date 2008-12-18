<script type="text/javascript">//<![CDATA[
   new Alfresco.FormUI("");
//]]></script>

<!--dollar{args.htmlid} -->

<div id="-form" class="form">
   
   <form method="POST" action="${form.submissionUrl}">
      <#list form.items as item>
         <!-- TODO: See if we can use absolute template paths/urls. -->
         <#include "${item.control.template}" />
      </#list>
      <hr/>
      <input type="submit" value="Submit" />&nbsp;&nbsp;
      <input type="button" value="Cancel" /> 
   </form>
   
</div>
