package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VtiInfService extends HttpServlet
{

    private static final long serialVersionUID = 8040337962413270621L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        w.print("<!-- FrontPage Configuration Information\n");
        w.print("FPVersion=\"14.0.0.000\"\n");
        w.print("FPShtmlScriptUrl=\"_vti_bin/shtml.dll/_vti_rpc\"\n");
        w.print("FPAuthorScriptUrl=\"_vti_bin/_vti_aut/author.dll\"\n");
        w.print("FPAdminScriptUrl=\"_vti_bin/_vti_adm/admin.dll\"\n");
        w.print("TPScriptUrl=\"_vti_bin/owssvr.dll\"\n");
        w.print("-->\n");
    }

}
