var articleFolder = space.childByNamePath("Articles");
 
var logFolder = space.childByNamePath("logs");
if (logFolder == null){
    logFolder = space.createFolder("logs");
}
var logger = logFolder.childByNamePath("log.txt");
if (logger == null){
    logger = logFolder.createFile("log.txt");
}
 
//------------------------------------------------
 
if(space.hasPermission("CreateChildren")){
    
    if (articleFolder == null){
       // create the folder for the first time
       articleFolder = space.createFolder("Articles");
   
    }
    var date=new Date();
    var year=date.getFullYear();
    var month=date.getMonth() + 1; 
    var day=date.getDate();
    
    logger.content += "date : " + date + "\r\n";
    logger.content += "year : " + year + "\r\n";
    logger.content += "month : " + month + "\r\n";
    logger.content += "day : " + day + "\r\n\r\n";
    
    var yearFolder=articleFolder.childByNamePath(year);
    if (yearFolder == null){
       yearFolder = articleFolder.createFolder(year);
    }
 
    var monthFolder=yearFolder.childByNamePath(month);
    if (monthFolder == null){
       monthFolder = yearFolder.createFolder(month);
    }
    
    var dayFolder=monthFolder.childByNamePath(day);
    if (dayFolder == null){
       dayFolder = monthFolder.createFolder(day);
    }
    
//move document into the Article folder
    
    var moveddoc = document.move(dayFolder);
    if (moveddoc != null){
        //logger.content += "Moving Content to  : " + dayFolder.properties.name;
        //copy.save();
        logger.content += "Moved Content : " + document.name + " : [" + year + "," + month + "," + day +"]\r\n";
        //document.remove();
    }
    
}
 
