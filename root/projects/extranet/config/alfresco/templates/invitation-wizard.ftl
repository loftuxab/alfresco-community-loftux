<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>${page.title}</title>
	${head}
</head>

<body>
	<div id="custom-doc" class="yui-t6">
	
		<!-- HEADER start -->
		<div id="hd2">
			<@region id="header" scope="page"/>
		</div>
		<!-- HEADER end -->
		
		<!-- BODY  -->
		<div id="bd">	
		
			<div id="yui-main">
		
				<@region id="body" scope="page"/>
			
			</div>
		</div>
		<!-- BODY end -->
				
	</div>

</body>
</html>
