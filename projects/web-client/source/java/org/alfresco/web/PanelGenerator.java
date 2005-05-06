/*
 * Created on Jan 18, 2005
 */
package org.alfresco.web;

import java.io.IOException;
import java.io.Writer;

/**
 * Helper to generate the rounded panel HTML templates
 * 
 * @author kevinr
 */
public final class PanelGenerator
{
   public static void generatePanel(Writer out, String contextPath, String panel, String inner)
      throws IOException
   {
      generatePanel(out, contextPath, panel, inner, BGCOLOR_WHITE);
   }
   
   public static void generatePanel(Writer out, String contextPath, String panel, String inner, String bgColor)
      throws IOException
   {
      generatePanel(out, contextPath, panel, inner, bgColor, false);
   }
   
   public static void generatePanel(Writer out, String contextPath, String panel, String inner, String bgColor, boolean dialog)
      throws IOException
   {
      generatePanelStart(out, contextPath, panel, bgColor, dialog);
      out.write(inner);
      generatePanelEnd(out, contextPath, panel);
   }
   
   public static void generatePanelStart(Writer out, String contextPath, String panel, String bgColor)
      throws IOException
   {
      generatePanelStart(out, contextPath, panel, bgColor, false);
   }
   
   public static void generatePanelStart(Writer out, String contextPath, String panel, String bgColor, boolean dialog)
      throws IOException
   {
      out.write("<table cellspacing=0 cellpadding=0 border=0 width=100%><tr><td width=7><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_01.gif' width=7 height=7 alt=''></td>");
      
      out.write("<td background='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_02.gif'><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_02.gif' width=7 height=7 alt=''></td>");
      
      out.write("<td width=7><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_03");
      if (dialog)
      {
         out.write("_squared");
      }
      out.write(".gif' width=7 height=7 alt=''></td></tr>");
      
      out.write("<tr><td background='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_04.gif'><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_04.gif' width=7 height=7 alt=''></td><td bgcolor='");
      out.write(bgColor);
      out.write("'>");
   }
   
   public static void generatePanelEnd(Writer out, String contextPath, String panel)
      throws IOException
   {
      out.write("</td><td background='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_06.gif'><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_06.gif' width=7 height=7 alt=''></td></tr>");
      
      out.write("<tr><td width=7><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_07.gif' width=7 height=7 alt=''></td>");
      
      out.write("<td background='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_08.gif'><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_08.gif' width=7 height=7 alt=''></td>");
      
      out.write("<td width=7><img src='");
      out.write(contextPath);
      out.write("/images/");
      out.write(panel);
      out.write("_09.gif' width=7 height=7 alt=''></td></tr></table>");
   }
   
   public final static String BGCOLOR_WHITE = "#FFFFFF";
}
