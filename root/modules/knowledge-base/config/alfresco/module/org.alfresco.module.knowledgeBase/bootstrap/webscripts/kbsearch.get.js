// check that search term has been provided
if (args.q == undefined || args.q.length == 0)
{
   status.code = 400;
   status.message = "Search term has not been provided.";
   status.redirect = true;
}
else
{
   // perform search
//    var nodes = search.luceneSearch("TEXT:" + args.q);
   

 var articleHome = companyhome.childByNamePath("Knowledge Base");
 var query="+PATH:\"/app:company_home/cm:Knowledge_x0020_Base//.\" +ASPECT:\"{ask.new.model}article\"";
 query=query+ " +@ask\\:status:\"draft\"";
 var str = (args.q).split(" ");
 query=query + '+(';
           for(var i=0;i<str.length;i++)
            query=query + ' TEXT:"' + str[i] + '"';
           
           query=query + ')';
 query = query + " +@cm\\:content.mimetype:\"text/html\"";

 var nodes =search.luceneSearch(query); 
 model.resultset = nodes;
/*
var doc1 = userhome.createFile("transform_me7.txt");
doc1.mimetype = "text/plain";
doc1.content = "This is plain text";
doc1.editInline="yes";
var trans1 = doc1.transformDocument("application/x-shockwave-flash");
*/
/*
var doc1 = userhome.createFile("transform_me1.txt");
doc1.mimetype = "text/plain";
doc1.content = "This is plain text";
var trans1 = doc1.transformDocument("application/x-shockwave-flash");

// create an HTML doc and convert to plain text
var doc2 = userhome.createFile("transform_me2.html");
doc2.mimetype = "text/html";
doc2.content = "This is an <b>HTML</b> <font color=blue><i>document</i>!</font>";
var trans2 = doc2.transformDocument("text/plain", userhome);
*/


}
