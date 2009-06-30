<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet">
   <div class="title">${msg("header.network")}</div>
   <div class="body scrollablePanel" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div class="detail-list-item">
         <h1 class="theme-color-2">Get more out of Alfresco.</h1>
         <p>With a subscription to the Alfresco Enterprise Network, you get access to Alfresco Enterprise Edition.  Enterprise Edition is put through 2,800 tests by a team of dedicated QA Engineers on open source and proprietary stacks for bugs, stability, scalability and security. The subscription is fully supported with Service Level Agreements by a team of Alfresco Support Engineers.</p>
         <p><a href="http://www.alfresco.com/services/subscription/"><img src="${url.context}/components/images/network-dashlet-button.png" alt="Learn More" width="97" height="28" /></a></p>
      </div>
   </div>
</div>