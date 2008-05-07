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

		<div class="logo">

			<!-- Start Top Info Left -->
			<@region id="logo" scope="global"/>
			<!-- End Top Info Left -->
		
		</div>

	</div>

	<div class="bar">

		<@region id="navigation" scope="global" />

	</div>
	
	<!-- MIDDLE COLUMN -->
	<div align="center">
		<br/>
		<br/>
		You have been successfully logged out!
		<br/>
		<br/>
		<a href="${url.context}">Go back to main page</a>
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
