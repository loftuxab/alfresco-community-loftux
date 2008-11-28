<script type="text/javascript">//<![CDATA[
   new Alfresco.FormUI("${args.htmlid}");
//]]></script>

<div class="form">
   <form>
      <div>${msg("form.required")}</div>
      <label for="name">Name</label>
      <input type="text" name="name" id="${args.htmlid}-name" />
      <input type="submit" value="${submitLabel}" />
   </form>
</div>