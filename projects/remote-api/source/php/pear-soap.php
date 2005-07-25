<html>
   <head>
      <title>Web Services with PEAR::SOAP</title>

      <style>
         body {font-family:verdana;font-size:10pt;}
      </style>
   </head>

   <body>
      <h2>Web Services with PEAR::SOAP</h2>

      <?php 
      require_once('SOAP/Client.php');

      // get the node service object
      //$wsdl = "http://localhost:8090/web-client/remote-api/NodeService?wsdl";
      //$client = new SOAP_WSDL($wsdl);
      $client = new SOAP_Client("http://localhost:8080/web-client/remote-api/NodeService");
      //$client->__options = array('trace'=>1);
      
      // define the store ref array
		$storeRef = array('identifier' => 'workspace://SpacesStore', 'protocol' => 'workspace://SpacesStore');
		
		//$proxycode = $client->generateProxyCode();
		//print_r($proxycode);
		//print "<br>";
		
      // find out if the SpacesStore exists      
      //$exists = $nodeService->storeExists($storeRef);
      $storeExists =& new SOAP_Value('{http://node.webservice.repo.alfresco.org}storeExists',false,$v=array('storeRef' => $storeRef));
      $exists = $client->call('storeExists', $v = array('storeExists' => $storeExists),
         array('namespace' => 'http://node.webservice.repo.alfresco.org', 'soapaction' => '', 'style' => 'document', 'use' => 'literal'));

      if (PEAR::isError($exists))
      {
         print("<br><br><span style='font-weight:bold;color:red'>Error: " . $exists->getMessage() . "</span><br>\n");
      }
      else
      {
         print '<br><br>SpacesStores exists = ' . $exists;
      }

		// find out the id of the root node
      //$nodeRef = $nodeService->getRootNode($storeRef);
      $getRootNode =& new SOAP_Value('{http://node.webservice.repo.alfresco.org}getRootNode',false,$v=array('storeRef' => $storeRef));
      $nodeRef = $client->call('getRootNode', $v = array('getRootNode' => $getRootNode),
         array('namespace' => 'http://node.webservice.repo.alfresco.org', 'soapaction' => '', 'style' => 'document', 'use' => 'literal'));
      if (PEAR::isError($nodeRef))
      {
         print("<br><br><span style='font-weight:bold;color:red'>Error: " . $nodeRef->getMessage() . "</span><br>\n");
      }
      else
      {
		   $id = $nodeRef->id;
		   print '<br><br>The root id of the SpacesStore is ' . $id;
      }

		// get the children of the root node
      //$result = $nodeService->getChildren($storeRef, "0");
      $getChildren =& new SOAP_Value('{http://node.webservice.repo.alfresco.org}getChildren',false,$v=array('storeRef' => $storeRef, "id" => "0"));
      $result = $client->call('getChildren', $v = array('getChildren' => $getChildren),
         array('namespace' => 'http://node.webservice.repo.alfresco.org', 'soapaction' => '', 'style' => 'document', 'use' => 'literal'));
      
      if (PEAR::isError($result))
      {
         print("<br><br><span style='font-weight:bold;color:red'>Error: " . $result->getMessage() . "</span><br>\n");
      }
      else
      {
         $number = count($result->nodes);
         print "<br><br>There are " . $number . " child nodes, their ids are:<br>";
         foreach ($result->nodes as $node) 
         {
            print $node->id . '<br>';
         }
      }

      //print "<xmp>";
      //print $client->wire;
      //print "</xmp>";
      ?>
   </body>

</html>