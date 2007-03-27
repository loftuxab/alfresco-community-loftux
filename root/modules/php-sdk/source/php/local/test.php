<?php

$session = new Session();
$stores = $session->getStores();

foreach ($stores as $store)
{
   echo $store->scheme."://".$store->address."<br>";
   if ($store->address == "SpacesStore")
   {
      $node = $store->rootNode;
      echo "&nbsp;&nbsp;&nbsp;Root node - ".$node->id."<br>";
   }
}

?>