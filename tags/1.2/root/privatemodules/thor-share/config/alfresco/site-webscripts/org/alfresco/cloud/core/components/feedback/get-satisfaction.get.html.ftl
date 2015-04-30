<#-- Ideally we wouldn't place CSS in a style tag here, but it's the only way
      that we're able to override the default styles -->
<style type="text/css">
    a.fdbk_tab_bottom
    {
       right: 0 !important;
    }
</style>

<script type="text/javascript" charset="utf-8">
  var is_ssl = ("https:" == document.location.protocol);
  var asset_host = is_ssl ? "https://s3.amazonaws.com/getsatisfaction.com/" : "http://s3.amazonaws.com/getsatisfaction.com/";
  document.write(unescape("%3Cscript src='" + asset_host + "javascripts/feedback-v2.js' type='text/javascript'%3E%3C/script%3E"));
</script>

<script type="text/javascript" charset="utf-8">
  var feedback_widget_options = {};

  feedback_widget_options.display = "overlay";
  feedback_widget_options.company = "alfresco";
  feedback_widget_options.placement = "bottom";
  feedback_widget_options.color = "#222";
  feedback_widget_options.style = "idea";

  if (GSFN)
  {
     var feedback_widget = new GSFN.feedback_widget(feedback_widget_options);
  }
</script> 