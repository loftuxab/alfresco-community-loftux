<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*,java.io.*,org.alfresco.repo.avm.*,org.alfresco.repo.avm.util.*,org.alfresco.service.cmr.avm.*,org.alfresco.service.cmr.repository.*,org.alfresco.repo.security.authentication.*,org.alfresco.service.cmr.avmsync.*,org.alfresco.service.cmr.security.*" %>
<html>
<head>
<%!
    static AVMService fgService;
    static BulkLoader fgLoader;
    static AVMInterpreter fgInterpreter;

    static
    {
        fgService = (AVMService)RawServices.Instance().getContext().getBean("AVMService");
        fgLoader = new BulkLoader();
        fgLoader.setAvmService(fgService);
        fgInterpreter = new AVMInterpreter();
        fgInterpreter.setAvmService(fgService);
        fgInterpreter.setAvmSyncService(
            (AVMSyncService)RawServices.Instance().getContext().getBean("AVMSyncService"));
        fgInterpreter.setBulkLoader(fgLoader);
    }
        
    static String EscapeForHTML(String data)
    {
        StringBuilder builder = new StringBuilder();
        int count = data.length();
        for (int i = 0; i < count; i++)
        {
            char c = data.charAt(i);
            switch (c)
            {
                case '<' :
                    builder.append("&lt;");
                    break;
                case '>' :
                    builder.append("&gt;");
                    break;
                case '"' :
                    builder.append("&quot;");
                    break;
                case '&' :
                    builder.append("&amp;");
                    break;
                case '\'' :
                    builder.append("&apos;");
                    break;
                case '\\' :
                    builder.append("&#092;");
                    break;
                default :
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }
%>
<title>AVM Interpreter</title>
</head>
<body>
<font face="Arial, Helvetica">
<h3>AVM Interpreter</h3>
<hr>
<pre>
<%
    String command = request.getParameter("command");
    if (command != null)
    {
       long start = System.currentTimeMillis();
       String data = request.getParameter("data");
       data = data + "\n\n";
       ((AuthenticationService)RawServices.Instance().getContext().getBean("AuthenticationService")).authenticate("admin", "admin".toCharArray());
       BufferedReader in = new BufferedReader(new StringReader(data));
       String result = fgInterpreter.interpretCommand(command, in);
       out.println(EscapeForHTML(command));
       out.println();
       out.println(EscapeForHTML(result));
       out.println((System.currentTimeMillis() - start) + "ms");
    }
    else
    {
       command = "";
    }
%>
</pre>
<form action="avm.jsp" method="POST">
<p>Command: <input style="font-family:monospace" type="text" name="command" id="command" size="70" value="<%=command%>"></p>
Optional Data:<br>
<textarea name="data" cols="80" rows="10"></textarea>
<p><input type="submit" name="submit" value="Execute"></p>
<a href="help.txt">Help!</a>
</form>
</font>
</body>
</html>
<script>
   document.getElementById("command").focus();
</script>
