<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="content-type" content="text/html;charset=iso-8859-1">
  <link rel="stylesheet" href="./css/extranet.css" type="text/css">
  <title>Alfresco Extranet</title>
  
  ${head}
  
</head>

<body>

<div style="width: 829px;" class="content">

	<div class="header">
	
		<div class="top_info_right">

			<!-- Start Top Info Right -->
			<@region id="user-tools" scope="global"/>
			<!-- End Top Info Right -->

		</div>

		<div class="logo">

			<!-- Start Top Info Left -->
			<@region id="logo" scope="global"/>
			<!-- End Top Info Left -->
		
		</div>

	</div>

	<div class="bar">

		<@region id="navigation" scope="global" />

	</div>

	<div class="search_field">
	
		<@region id="search" scope="global" />

	</div>


	<div align="center">
		<br/>
		<br/>
		TODO:  The knowledge base integration!
		<br/>
		<br/>
		<br/>
	</div>

	<!-- FOOTER -->
	<div class="footer">
	
		<@region id="footer" scope="global"/>

	</div>
</div>

</body>
</html>
