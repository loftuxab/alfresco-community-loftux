<script type="text/javascript">//<![CDATA[
   new Alfresco.FormUI("${args.htmlid}").setOptions(
   {
      currentUser: "${user.id}" <#-- ,
      formFields:
      [
         <#list fields as field>"${field}"<#if field_has_next>,</#if></#list>
      ]  -->
   };
//]]></script>

<div class="form">
   <i>The following markup has been written in order to test the behaviour of the forms code. It is
   not intended to be attractive or even usable.</i>
   <form>
      <div>${msg("form.required")}</div>
      <label for="${args.htmlid}-name">Name</label>
      <input type="text" name="name" id="${args.htmlid}-name" />

      <#list fields as blaaa>
         <label for="${blaaa}-id">${blaaa} : </label>
         <input type="text" name="${blaaa}" id="${blaaa}-id" value="${blaaa}"/>
      </#list>

      <input type="submit" value="${submitLabel}" />

      <#-- args.htmlid = ${args.htmlid} -->
      currentUser = ${user.id}
   </form>
  <b>Constraints</b>
  <#list constraints as c>
     ${c}
  </#list>
  <br/>
  <b>Default Controls</b>
  <#list defaultcontrols as dc>
     ${dc}
  </#list>
  <br/>
  <hr/>
   
   ${rubbish}
</div>
