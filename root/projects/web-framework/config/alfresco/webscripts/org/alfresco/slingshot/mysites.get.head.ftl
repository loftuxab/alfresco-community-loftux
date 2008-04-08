
<!-- Dependencies -->
<script type="text/javascript" src="/alfweb/js/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>

<!-- OPTIONAL: Animation (only required if enabling Animation) -->
<script type="text/javascript" src="/alfweb/js/yui/build/animation/animation-min.js"></script>

<!-- OPTIONAL: Drag & Drop (only required if enabling Drag & Drop) -->
<script type="text/javascript" src="/alfweb/js/yui/build/dragdrop/dragdrop-min.js"></script>

<!-- Source file -->
<script type="text/javascript" src="/alfweb/js/yui/build/container/container-min.js"></script>

<script language="JavaScript">

function showCreateSiteDialog(){
  var createSiteDialog = new YAHOO.widget.Panel("createSiteDialog",  
			{ width:"240px", 
			  fixedcenter:true, 
			  close:false, 
			  draggable:false, 
			  zindex:4,
			  modal:true,
			  visible:true
			} 
		);
  createSiteDialog.render(document.body);
  createSiteDialog.show();  

}

</script>
