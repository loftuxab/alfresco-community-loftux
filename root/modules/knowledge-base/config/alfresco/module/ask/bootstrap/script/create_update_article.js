// get the args
  var result = "";
  var updateStatus = "";
  var log = "";
  var currentSpace = document;
  var debug = 1;
  //var knowledgeBaseFolder = companyhome.childByNamePath("/Company Home/Data Dictionary/Knowledge Base");
  
//************************************************************************************************


function updateContentArticle(theArticleContent) {
  
  if (logger.isLoggingEnabled()) {
		logger.log("Excuting updateArticle...");
	}

  //apply the Ask Article aspect   
   if (!theArticleContent.hasAspect("ask:article")) {   	
    theArticleContent.addAspect("ask:article");
  }
      
  //apply the cm:generalclassifiable aspect   
  if (!theArticleContent.hasAspect("cm:generalclassifiable")) {   	
    theArticleContent.addAspect("cm:generalclassifiable");
  }


  //apply the cm:titled aspect   
  if (!theArticleContent.hasAspect("cm:titled")) {   	
    theArticleContent.addAspect("cm:titled");
  }  
  
  var counter = actions.create("counter");
  counter.execute(theArticleContent);


  // start the article content
  theArticleContent.content = '';
  theArticleContent.content += '<html><body>';
  
  
  //dump the params to the http stream for debug
  for (var i=0; i<(args.length); i++)
  {
     result += "param value = " + args[i] + "<br/>";
  }
  
  //dump some more debug markup
  result += "<br/><br/>";
  result += "<table width='200px'>";
  result += "<tr><td><b>Name</b></td><td><b>Value</b></td></tr>";

  for (var k in args) {

	   //dump the args name/value pairs for debug
     result += "<tr><td>" + k + "</td>";
     result += "<td>" + args[k] + "</td></tr>";
     
     //add the arg and its value to the article content
    if (k == "editor") {
    	//Wrap the editor content in a cdata tag
    	theArticleContent.content += args[k];
    }
   
   
    // now set the article properties
   if (k == "article_type") {
   	theArticleContent.properties["ask:article_type"] = args[k];
   }
   
    else if (k == "category_list") {      
		 //Split the category array by |
	 var catarray = args[k].split("|");
     theArticleContent.properties["cm:categories"] = catarray;
    }
    else if (k == "status") {      
    	theArticleContent.properties["ask:status"] = args[k];
    }
    else if (k == "article_title") {      
        result += "SETTINT THE TITLE TO" + args[k];
    	theArticleContent.properties.title = args[k];
    	result += "THE RESULT " + theArticleContent.properties.title;
    }
    else if  (k == "version_list") {
    	//Split the version_list array by |
	 		var versionarray = args[k].split("|");
      theArticleContent.properties["cm:alfresco_version"] = versionarray;
    }
    else if  (k == "article_tags") {
      var tagsArray = args[k].split(",");
      theArticleContent.properties["ask:tags"] = tagsArray;
      result += args[k];
    }
    
    else if  (k == "visibility") {
      theArticleContent.properties["ask:visibility"] = args[k];
    }    
    
  	}
    
    theArticleContent.save();
     result += "AFTER THE SAVE RESULT " + theArticleContent.properties.title;  
  
	  //make sure all the property updates have been saved
	// theArticleContent.save();
	   	
	  //more debug markup
	  result += "</table>";
	  
	  //finish the article content 
	  theArticleContent.content += "</body></html>";
	
	
	  //apply the templatable aspect   
	  //set the template for the aspect, it should not have templateable unless a rule has added it
	   var myTemplateRef = "workspace://SpacesStore/fb84f96b-7b4f-11db-a5b7-e1be205c8dfc";
	   if (!theArticleContent.hasAspect("cm:templatable")) {   	
	   	var props = new Array(1);
	    props["cm:template"] = myTemplateRef;
	    theArticleContent.addAspect("cm:templatable", props);
	    theArticleContent.save();
	  }
	  
	  
  } //updateContentArticle for generating html content



//**************************************************************************************************

function updateArticle(theArticle) {
  
  if (logger.isLoggingEnabled()) {
		logger.log("Excuting updateArticle...");
	}

  //apply the Ask Article aspect   
   if (!theArticle.hasAspect("ask:article")) {   	
    theArticle.addAspect("ask:article");
  }
      
  //apply the cm:generalclassifiable aspect   
  if (!theArticle.hasAspect("cm:generalclassifiable")) {   	
    theArticle.addAspect("cm:generalclassifiable");
  }


  //apply the cm:titled aspect   
  if (!theArticle.hasAspect("cm:titled")) {   	
    theArticle.addAspect("cm:titled");
  }  
  
  //var counter = actions.create("counter");
  //counter.execute(theArticle);


  // start the article content
  theArticle.content = '';
  theArticle.content += '<?xml version="1.0" standalone="yes"?>';
  theArticle.content += "<article>" + "\r\n";
  
  //dump the params to the http stream for debug
  for (var i=0; i<(args.length); i++)
  {
     result += "param value = " + args[i] + "<br/>";
  }
  
  //dump some more debug markup
  result += "<br/><br/>";
  result += "<table width='200px'>";
  result += "<tr><td><b>Name</b></td><td><b>Value</b></td></tr>";

  for (var k in args) {

	   //dump the args name/value pairs for debug
     result += "<tr><td>" + k + "</td>";
     result += "<td>" + args[k] + "</td></tr>";
     
     //add the arg and its value to the article content
    if (k == "editor") {
    	//Wrap the editor content in a cdata tag
    	theArticle.content += "<" + k + ">" + "<![CDATA[" + args[k] + "]]>" + "</" + k + ">\r\n";
    }
    else {
     theArticle.content += "<" + k + ">" + args[k] + "</" + k + ">\r\n";
    } 

    // now set the article properties
   if (k == "article_type") {
   	theArticle.properties["ask:article_type"] = args[k];
   }
   
    else if (k == "category_list") {      
		 //Split the category array by |
	 var catarray = args[k].split("|");
     theArticle.properties["cm:categories"] = catarray;
    }
    else if (k == "status") {      
    	theArticle.properties["ask:status"] = args[k];
    }
    else if (k == "article_title") {      
        result += "SETTINT THE TITLE TO" + args[k];
    	theArticle.properties.title = args[k];
    	result += "THE RESULT " + theArticle.properties.title;
    }
    else if  (k == "version_list") {
    	//Split the version_list array by |
	 		var versionarray = args[k].split("|");
      theArticle.properties["cm:alfresco_version"] = versionarray;
    }
    else if  (k == "article_tags") {
      var tagsArray = args[k].split(",");
      theArticle.properties["ask:tags"] = tagsArray;
      result += args[k];
    }
    
    else if  (k == "visibility") {
      theArticle.properties["ask:visibility"] = args[k];
    }    
    
  	}
    
    theArticle.save();
     result += "AFTER THE SAVE RESULT " + theArticle.properties.title;  
  
	  //make sure all the property updates have been saved
	// theArticle.save();
	   	
	  //more debug markup
	  result += "</table>";
	  
	  //finish the article content 
	  theArticle.content += "</article>" + "\r\n";
	
	
	  //apply the templatable aspect   
	  //set the template for the aspect, it should not have templateable unless a rule has added it
	   var myTemplateRef = "workspace://SpacesStore/fb84f96b-7b4f-11db-a5b7-e1be205c8dfc";
	   if (!theArticle.hasAspect("cm:templatable")) {   	
	   	var props = new Array(1);
	    props["cm:template"] = myTemplateRef;
	    theArticle.addAspect("cm:templatable", props);
	    theArticle.save();
	  }
	  
	  
  } //updateArticle
  
//**************************************************************************************************  

  if (document.isDocument) {
	var currentSpace = document.parent;
        //var len = document.name.length;
        var doc = document.name.substring(0,5);
        var theArticle=document;
        var url = document.displayPath+"/"+doc+".html";
         
           var theArticleContent = companyhome.childByNamePath(url.substring(13,url.length));
        
	   	if (logger.isLoggingEnabled()) {logger.log("Article Exists Updating");}  
	   	updateArticle(theArticle);
                updateContentArticle(theArticleContent);
	   	updateStatus =  "<p>Article ASKID: " + theArticle.properties["ask:askid"] + " updated. <input type='button' value='Close' onclick='javascript:window.close();'></p>";
  }
	else
  {
  	  if (logger.isLoggingEnabled()) {logger.log("Creating new article....");}
	  var theArticle = null;
          var theArticleContent= null;
  }  


  //if (args.exists && args.docid)
  
  //delete the article if we have one
  //var theOldArticle = currentSpace.childByNamePath(args.article_name + ".xml");
  //if (theOldArticle != null){
  //  theOldArticle.remove();
  //}
  
  // TODO: Check whether or not we have permission to create/update the content
  // TODO: Throw an error if we are trying to create an article with the same name as an existing article
  
  if (theArticle == null){
  	//create the article
  	   //Apply the countable aspect  to the script 
   //this is used to genetate the article ID
   
	  var counterNode = companyhome.childByNamePath("Knowledge Base/counter/askIDCounter");
	  
	  if (counterNode) {
	      if (logger.isLoggingEnabled()) {
		   		logger.log("Found counternode");
		   	}
		  if (!counterNode.hasAspect("cm:countable")) {   	
		    counterNode.addAspect("cm:countable");
		    counterNode.save();
		  }
		  
		  // Get the current record count
		    var countAction = actions.create("counter");
		    countAction.execute(counterNode);
		    var recordCounter = counterNode.properties["cm:counter"];
		       
		    // Pad to get the db id
		    var askid = utils.pad(String(recordCounter), 5);
	
		   	if (logger.isLoggingEnabled()) {
		   		logger.log("Created article ID: " + askid);
		   	}
		   	  
			var theArticle = currentSpace.createFile(askid + ".xml");
			theArticle.properties["ask:askid"] = askid;
		    theArticle.save();
                       updateArticle(theArticle);

                    var theArticleContent = currentSpace.createFile(askid + ".html");
			theArticleContent.properties["ask:askid"] = askid;
		    theArticleContent.save();
		    
		    
                    updateContentArticle(theArticleContent);
		    updateStatus =  "<p>Article ASKID: " + askid + " created. <input type='button' value='Close' onclick='javascript:window.close();'></p>";
	  }
	  else
	  {
	   	if (logger.isLoggingEnabled()) {logger.log("Failed to generate askid, exiting...");}
	   	updateStatus = "<p>Article not created, unable to generate ASK ID <input type='button' value='Close' onclick='javascript:window.close();'></p>";
	  }
  }
  
 

   // display the categories of a node as an HTML list 
   
//   if (document.hasAspect("cm:generalclassifiable"))
//   {
//   	  result += "<br/><br/>";
//      var categories = document.properties["cm:categories"];
//      for (var i=0; i<categories.length; i++)
//      {
//         result += "Category " + i + ": " + categories[i].name + "<br>";
//      }
//   }
   
   //send the result to the http stream for debug
   //result;
 
updateStatus;
 
    
