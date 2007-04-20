<?php
    $stores = $_REPOSITORY->createSession()->stores;
?>
<html>

<head>
</head>

<body>
	<h1>List of stores</h1>
	<br/>
<?php	
	foreach ($stores as $store)
    {
    	echo $store->scheme."://".$store->address."<br>\n";
    	
    	$rootNode = $store->rootNode;
    	echo("summert: ".$rootNode->cm_title."\n");
    	echo($rootNode->getClass()->toString()."\n");
    	var_dump($rootNode);
    }
?>    
</body>

</html>