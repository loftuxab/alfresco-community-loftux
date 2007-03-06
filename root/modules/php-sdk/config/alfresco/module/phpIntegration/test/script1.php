<?php
    $result = "";
    $session = createSession();
    
    $stores = $session->stores;
    foreach ($stores as $store)
    {
    	$result .= $store->scheme."://".$store->address."\n";
    }

	echo("some bonnin");
	    
    return "Result\n". $result;
?>

<html></html>