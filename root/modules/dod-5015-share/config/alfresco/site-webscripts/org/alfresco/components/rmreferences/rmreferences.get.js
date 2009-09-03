function getDocName(nodeRef)
{
   //must regexp this
   nodeRef = nodeRef.replace(':','').replace("\\",'').replace('//','/');
   var result = remote.call("/slingshot/doclib/dod5015/doclist/node/"+nodeRef);

   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      return data.items[0].displayName;
   }
   else return nodeRef;
}

function getDocReferences()
{

   var nodeRef = page.url.args.nodeRef.replace(":/", "");
   var result = remote.call("/api/node/"+nodeRef+"/customreferences");

   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      var docrefs = data.data.customReferences;
      
      for (var i=0,len = docrefs.length;i<len;i++)
      {
         var ref = docrefs[i];
         ref.targetRefDocName = (ref.referenceType=='parentchild')? getDocName(ref.childRef) : getDocName(ref.targetRef);
         if (ref.referenceType=='parentchild')
         {
            ref.label = ref.target;
            ref.targetRef = ref.childRef;
         }
         ref.domId = ref.targetRef.split('/').pop()
         docrefs[i]=ref;
      }      
      return docrefs;
   }
   else {
      return [];
   }
}


model.references = getDocReferences();

