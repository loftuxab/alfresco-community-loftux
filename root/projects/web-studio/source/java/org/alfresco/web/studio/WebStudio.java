package org.alfresco.web.studio;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.web.config.WebStudioConfigElement;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.studio.client.WebStudioStateProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class WebStudio 
{
	private static Log logger = LogFactory.getLog(WebStudio.class);
	
	public static ConfigService getConfigService()
	{
		return FrameworkHelper.getConfigService();
	}
			
    public static WebStudioConfigElement getConfig()
    {    	
    	Config config = getConfigService().getConfig("WebFramework");
    	return (WebStudioConfigElement) config.getConfigElement("web-studio");
    }

	public static WebStudioStateProvider getWebStudioStateProvider()
	{
		return (WebStudioStateProvider) FrameworkHelper.getApplicationContext().getBean("webstudio.clientStateProvider");
	}
    
	/*
    public static String[] getAllJavascriptIncludes()
    {
    	// build up a map of includes
		Map<String, String> includes = new HashMap<String, String>(64, 1.0f);		
		
		// application dependencies
		String[] applicationIds = WebStudio.getConfig().getApplicationIds();
		for(int i = 0; i < applicationIds.length; i++)
		{
			ApplicationDescriptor appDescriptor = WebStudio.getConfig().getApplication(applicationIds[i]);
			if(appDescriptor != null)
			{
				// add the application bootstrap file
				
				// add the application file
				String appFile = appDescriptor.getJavascript();
				if(appFile != null)
				{
					includes.put(appFile, appFile);
				}
				
				// walk the applets
				List appletIncludes = appDescriptor.getAppletIncludes();
				if(appletIncludes != null)
				{
					for(int z = 0; z < appletIncludes.size(); z++)
					{
						String appletId = (String) appletIncludes.get(z);
						if(appletId != null)
						{
							AppletDescriptor appletDescriptor = WebStudio.getConfig().getApplet(appletId);
							if(appletDescriptor != null)
							{
								// inject the dependencies for this applet
								DependencyDescriptor[] deps = appletDescriptor.getDependencies();
								for(int v = 0; v < deps.length; v++)
								{
									String file = deps[v].getJavascript();
									if(file != null)
									{
										includes.put(file, file);
									}
								}	
								
								String appletFile = appletDescriptor.getJavascript();
								if(appletFile != null)
								{
									includes.put(appletFile, appletFile);
								}
							}
						}
					}
				}
			}
		}
		
		return includes.values().toArray(new String[includes.size()]);    	
    }

    public static String[] getAllCSSIncludes()
    {
    	// build up a map of includes
		Map<String, String> includes = new HashMap<String, String>(64, 1.0f);		
		
		// core dependencies
		String[] coreIds = WebStudio.getConfig().getCoreDependencyIds();
		for(int i = 0; i < coreIds.length; i++)
		{
			// inject the dependencies for the core
			DependencyDescriptor descriptor = WebStudio.getConfig().getCoreDependency(coreIds[i]);
			if(descriptor != null)
			{
				String file = descriptor.getCSS();
				if(file != null)
				{
					includes.put(file, file);
				}
			}
		}
		
		// application dependencies
		String[] applicationIds = WebStudio.getConfig().getApplicationIds();
		for(int i = 0; i < applicationIds.length; i++)
		{
			ApplicationDescriptor appDescriptor = WebStudio.getConfig().getApplication(applicationIds[i]);
			if(appDescriptor != null)
			{
				// inject the dependencies for this app
				DependencyDescriptor[] dependencies = appDescriptor.getDependencies();
				for(int z = 0; z < dependencies.length; z++)
				{
					String file = dependencies[z].getCSS();
					if(file != null)
					{
						includes.put(file, file);
					}
				}
				
				// add the application file
				String appFile = appDescriptor.getCSS();
				if(appFile != null)
				{
					includes.put(appFile, appFile);
				}
				
				// walk the applets
				List appletIncludes = appDescriptor.getAppletIncludes();
				if(appletIncludes != null)
				{
					for(int z = 0; z < appletIncludes.size(); z++)
					{
						String appletId = (String) appletIncludes.get(z);
						if(appletId != null)
						{
							AppletDescriptor appletDescriptor = WebStudio.getConfig().getApplet(appletId);
							if(appletDescriptor != null)
							{
								// inject the dependencies for this applet
								DependencyDescriptor[] deps = appletDescriptor.getDependencies();
								for(int v = 0; v < deps.length; v++)
								{
									String file = deps[v].getCSS();
									if(file != null)
									{
										includes.put(file, file);
									}
								}	
								
								String appletFile = appletDescriptor.getCSS();
								if(appletFile != null)
								{
									includes.put(appletFile, appletFile);
								}
							}
						}
					}
				}
			}
		}
		
		return includes.values().toArray(new String[includes.size()]);    	
    }
    */
    
    public static String compressJavascript(StringBuilder buffer)
    	throws IOException
    {
    	String result = null;
    	
        try 
        {
        	Reader in = new java.io.StringReader(buffer.toString());
        	
        	ErrorReporter reporter = new ErrorReporter() {

                public void warning(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
                    if (line < 0) {
                        System.err.println("\n[WARNING] " + message);
                    } else {
                        System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
                    }
                }

                public void error(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
                    if (line < 0) {
                        System.err.println("\n[ERROR] " + message);
                    } else {
                        System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
                    }
                }

                public EvaluatorException runtimeError(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
                    error(message, sourceName, line, lineSource, lineOffset);
                    return new EvaluatorException(message);
                }
        	};

        	
            JavaScriptCompressor compressor = new JavaScriptCompressor(in, reporter);

            // close the input input stream?
            in.close();
            
            StringWriter out = new StringWriter();

            // set to true if we want to obfuscate
            boolean munge = false;
            boolean preserveAllSemiColons = true;
            boolean disableOptimizations = false;
            int linebreakpos = 0;
            boolean verbose = false;
            
            compressor.compress(out, linebreakpos, munge, verbose,
                    preserveAllSemiColons, disableOptimizations);
            
            result = out.toString();

        } catch (EvaluatorException e) {

            e.printStackTrace();
        }
        
        return result;
    }

    public static String compressCSS(StringBuilder buffer)
    	throws IOException
    {
    	String result = null;
    	
    	Reader in = new java.io.StringReader(buffer.toString());
    	
        CssCompressor compressor = new CssCompressor(in);

        // close the input stream?
        in.close();
        
        StringWriter out = new StringWriter();

        int linebreakpos = 0;
        compressor.compress(out, linebreakpos);
        
        result = out.toString();
        
        return result;

    }
    
}
