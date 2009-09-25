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
/*
 * Note, "From" is customreferences from this node and *not* from other documents to this node.
 * 
 */ 
function getDocReferences()
{

   var nodeRef = page.url.args.nodeRef.replace(":/", "");
   var result = remote.call("/api/node/"+nodeRef+"/customreferences");
   var processDocRefs = function(docrefs,type) {
      for (var i=0,len = docrefs.length;i<len;i++)
      {
         var ref = docrefs[i];
         ref.targetRefDocName = (ref.referenceType=='parentchild')? getDocName(ref.childRef) : getDocName(ref.targetRef);
         if (ref.referenceType=='parentchild')
         {
            ref.label = ref.target;
            ref.targetRef = ref.childRef;
            ref.sourceRef = ref.parentRef;
         }

         docrefs[i]=ref;
      }      
      return docrefs;
   };
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      var docrefs = {
         from: processDocRefs(data.data.customReferencesFrom,'from'),
         to: processDocRefs(data.data.customReferencesTo,'to')
      };
      
      return docrefs;
   }
   else {
      return [];
   }
}


model.references = getDocReferences();

