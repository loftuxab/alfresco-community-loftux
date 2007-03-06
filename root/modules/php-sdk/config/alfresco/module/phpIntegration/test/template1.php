<?php
    $result = "";
    $session = createSession();
    
    $stores = $session->stores;
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
    	echo $store->scheme."://".$store->address."<br>";
    }
?>    
</body>

</html>