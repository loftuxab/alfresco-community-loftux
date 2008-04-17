//var json = remote.call("/person/someuser")
var json = '{"name":"Paul Holmes Higgin", "title":"VP Engineering", "department":"Engineering department", "company":"Alfresco", "location":"Maidenhead", "status":"ONLINE", "loggedIn":"12:37 pm", "email":"paulhh@alfresco.com", "mobile":"+44 0208 2548001", "skype":"paulhh", "linkedIn":"paul_holmes_higgin"}';
var profile = eval('(' + json + ')');
model.profile = profile;
