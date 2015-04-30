<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Search Service POST method
 */
function main()
{
   var returnParams = "m=admin-console.success";
   try
   {
      var searchService = args["Alfresco:Type=Configuration,Category=Search,id1=manager|sourceBeanName"];
      
      // for the Solr base url attribute - there are composite values that use the base url stem to further
      // generate the base url to each core for various query operations - so we build those urls here also
      var persistBeans = Admin.getBeansForJMXFormData(function(attrname, attrvalue, mbean) {
         if (attrname == "solr.baseUrl" && mbean.name.match(searchService+"$") == searchService)
         {
            // found the solr "baseUrl" property used to build composite values for the cores
            var alfStoreBean = "Alfresco:Type=Configuration,Category=Search,id1=managed,id2="+searchService+",id3="+searchService+".store.mappings,id4=solrMappingAlfresco";
            var beans = jmx.queryMBeans(alfStoreBean);
            if (beans.length === 1)
            {
               beans[0].attributes["baseUrl"].value = attrvalue + "/alfresco";
               jmx.save(beans[0]);
            }
            var alfArchiveBean = "Alfresco:Type=Configuration,Category=Search,id1=managed,id2="+searchService+",id3="+searchService+".store.mappings,id4=solrMappingArchive";
            beans = jmx.queryMBeans(alfArchiveBean);
            if (beans.length === 1)
            {
               beans[0].attributes["baseUrl"].value = attrvalue + "/archive";
               jmx.save(beans[0]);
            }
         }
      });
      
      // save the remaining modified bean list in the exact order we want below                            
      // this is required as the search manager bean must be last in the save order - see ALF-19330
      var beanList = [
            "Alfresco:Type=Configuration,Category=Search,id1=managed,id2="+searchService,
            "Alfresco:Type=Configuration,Category=Search,id1=manager"
         ];
      for (var i=0; i<beanList.length; i++)
      {
         for each (var bean in persistBeans)
         {
            if (bean == beanList[i])
            {
               jmx.save(bean);
               break;
            }
         }
      }
   }
   catch (e)
   {
      returnParams = "e=" + e.message;
   }
   // generate the return URL
   status.code = 301;
   status.location = url.service + "?" + returnParams;
   status.redirect = true;
}

main();