<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * System Summary GET method
 */
function main()
{
   //System Information
   model.alfrescoAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=RepositoryDescriptor,Type=Server",
         ["Edition", "VersionNumber"]
      );
   model.sysPropsAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=SystemProperties",
         ["java.home", "java.version", "java.vm.vendor", "os.name", "os.version", "os.arch", "alfresco.home"]
      );

   //File Systems
   model.fileSystemsAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=fileServers,id1=default",
         ["cifs.enabled", "ftp.enabled", "nfs.enabled"]
      );

   //Memory
   model.memoryAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=Runtime",
         ["FreeMemory", "MaxMemory", "TotalMemory", "AvailableProcessors"]
      );
   //convert bytes into GB with 2 decimal places
   model.memoryAttributes["FreeMemory"].value = Math.round(model.memoryAttributes["FreeMemory"].value / 1024 / 1024 / 1024 * 100) /100;
   model.memoryAttributes["MaxMemory"].value = Math.round(model.memoryAttributes["MaxMemory"].value / 1024 / 1024 / 1024 * 100) /100;
   model.memoryAttributes["TotalMemory"].value = Math.round(model.memoryAttributes["TotalMemory"].value / 1024 / 1024 / 1024 * 100) /100;

   //Transfromation Services
   model.ooDirectAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=OOoDirect,id1=default",
         ["ooo.enabled"]
      );
   model.jodConvAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default",
         ["jodconverter.enabled"]
      );
   model.swfToolsAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=ContentTransformer,Type=pdf2swf",
         ["Available"]
      );
   model.imageMagicAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=ContentTransformer,Type=ImageMagick",
         ["Available"]
      );
   model.fFMpegAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=ContentTransformer,Type=Ffmpeg",
         ["Available"]
      );

   //Indexing Subsystem
   model.indexingAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=Search,id1=manager",
         ["sourceBeanName"]
      );

   //Repository Clustering
   model.clusteringAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=Cluster,Tool=Admin",
         ["ClusteringEnabled","ClusterName","NumClusterMembers"]
      );
   
   // WebDav
   model.webdavAttributes = Admin.getMBeanAttributes(
		 "Alfresco:Type=Configuration,Category=WebDav,id1=default",
	     ["Enabled"]
	  );
   
   // Sharepoint, vti
   var vtiBeanName = "Alfresco:Type=Configuration,Category=Sharepoint Protocol,id1=default";
   vtiBean = Admin.getMBean(vtiBeanName)
   if (model.vtiAvailable = (vtiBean !== null))
   {
      model.vtiAttributes = Admin.getMBeanAttributes(vtiBeanName,
         ["Enabled"]
      );
   }
   
   //Activities Feed
   model.activitesAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=ActivitiesFeed,id1=default",
         ["activities.feed.notifier.enabled"]
      );
   
   //Authentication
   authenticationAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=Authentication,id1=manager",
         ["chain"]
      );
   authDirs = authenticationAttributes["chain"].value.split(',');
   model.authenticationDirectories = [];
   for (var i=0;i<authDirs.length; i++)
   {
      dirProps = authDirs[i].split(':');
      model.authenticationDirectories[i] = {
            "name" : dirProps[0],
            "type" : dirProps[1]
      };
   }

   //Synchronized Authentication
   syncDirs = jmx.queryMBeans("Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=*");
   model.synchronizedDirectories = [];
   
   //Loop through all authentication directories
   for (var i=0; i<syncDirs.length; i++)
   {
      var beanName = syncDirs[i].getName();
      var tempDirectory = Admin.getMBeanAttributes(
            beanName,
            ["$type","ldap.synchronization.active"]
         );
      type = tempDirectory["$type"].value;
      
      //If the type is "ldap" or "ldap-ad" check if it is synchronized
      if(type == "ldap" || type == "ldap-ad")
      {
         dirName = beanName.substring(beanName.lastIndexOf("=")+1);
         synced = tempDirectory["ldap.synchronization.active"].value;
         
         //If it is synshronized add it to the list.
         if(synced == "true")
         {
            model.synchronizedDirectories[i] = {
                  "name" : dirName,
                  "type" : type
            };
         }
      }
   }

   //Email
   model.inEmailAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=email,id1=inbound",
         ["email.inbound.enabled","email.server.enabled"]
      );
   model.outEmailAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=email,id1=outbound",
         ["mail.from.enabled"]
         );
   model.imapEmailAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=imap,id1=default",
         ["imap.server.enabled"]
         );

   //Auditing Services
   model.auditingAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=Audit,id1=default",
         ["audit.enabled", "audit.cmischangelog.enabled", "audit.alfresco-access.enabled", "audit.tagging.enabled", "audit.sync.enabled"]
         );

   //Content Store
   //Get all the ContentStore MBeans.
   contentBeans = jmx.queryMBeans("Alfresco:Name=ContentStore,*"); 
   model.contentStoreAttributes = [];
   //Loop through the beans. Each one represents a content store.
   for (var i=0; i<contentBeans.length; i++)
   {
      contName = contentBeans[i].getName();
      storePath = contName.match(/Root=(.*?)(?=,|$)/)[1].replace("|", ":"); //Extract the store path from the bean name
      model.contentStoreAttributes[i] = Admin.getMBeanAttributes(
            contName,
            ["SpaceFree","SpaceTotal"]
            );
      
      //Convert the values from bytes to MB.
      model.contentStoreAttributes[i]["SpaceTotal"].value = Math.round(model.contentStoreAttributes[i]["SpaceTotal"].value / 1024 / 1024);
      model.contentStoreAttributes[i]["SpaceFree"].value = Math.round(model.contentStoreAttributes[i]["SpaceFree"].value / 1024 / 1024);
      
      //Add a read-only attribute SpaceUsed
      spaceUsed = (model.contentStoreAttributes[i]["SpaceTotal"].value - model.contentStoreAttributes[i]["SpaceFree"].value);
      model.contentStoreAttributes[i]["SpaceUsed"] = {
            "name" : "SpaceUsed",
            "readonly" : true,
            "type" : "java.lang.Long",
            "value" : spaceUsed
      };
      
      //Add a read-only attribute StorePath
      model.contentStoreAttributes[i]["StorePath"] = {
            "name" : "StorePath",
            "readonly" : true,
            "type" : "java.lang.String",
            "value" : storePath
      };
   }

   //AMPs
   model.installedAMPs = Admin.getCompositeDataAttributes(
         "Alfresco:Name=ModuleService",
         "AllModules",
         ["module.id","module.version"]
         );
   model.previousAMPs = Admin.getCompositeDataAttributes(
         "Alfresco:Name=ModuleService",
         "MissingModules",
         ["module.id","module.version"]
         );

   //Users and Groups
   model.authorityAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=Authority",
         ["NumberOfUsers", "NumberOfGroups"]
         );

   model.tools = Admin.getConsoleTools("admin-systemsummary");
   model.metadata = Admin.getServerMetaData();
}

main();