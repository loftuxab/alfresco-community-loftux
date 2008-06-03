<#import "include/globals.ftl" as global />
<#import "include/homepage.ftl" as homepage />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<title><@pageTitle/></title>
	<@global.header/>
	${head}
</head>

<body>
	<div id="custom-doc" class="yui-t6">
	
		<!-- HEADER start -->
		<div id="hd">
			<@region id="header" scope="theme"/>
		</div>
		<!-- HEADER end -->
		
		<!-- NAVIGATION start -->
		<div id="nav">
			<@region id="navigation" scope="global"/>
		</div>
		<!-- NAVIGATION end -->

		<!-- BODY  -->
		<div id="bd">
		
			<@homepage.body/>

		</div>
		<!-- BODY end -->
		
		<!-- FOOTER start -->
		<div id="ft">
			<@region id="footer" scope="global"/>
		</div>
		<!-- FOOTER end -->
		
	</div>

</body>
</html>
