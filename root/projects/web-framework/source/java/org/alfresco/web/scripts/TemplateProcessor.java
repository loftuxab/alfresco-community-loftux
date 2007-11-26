package org.alfresco.web.scripts;

import java.io.Writer;

public interface TemplateProcessor
{
    /**
     * Determines if a template exists
     * 
     * @param template
     * @return  true => exists
     */
    public boolean hasTemplate(String template);
    
    /**
     * Process a template against the supplied data model and write to the out.
     * 
     * @param template       Template name/path
     * @param model          Object model to process template against
     * @param out            Writer object to send output too
     */
    public void process(String template, Object model, Writer out);
    
    /**
     * Process a string template against the supplied data model and write to the out.
     * 
     * @param template       Template string
     * @param model          Object model to process template against
     * @param out            Writer object to send output too
     */
    public void processString(String template, Object model, Writer out);

    
    public String getDefaultEncoding();
    
    public void reset();

}
