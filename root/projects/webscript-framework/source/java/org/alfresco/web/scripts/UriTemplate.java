/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a Uri Template - with basic {token} format support.
 * See JAX-RS JSR-311.
 * 
 * @author davidc
 */
public class UriTemplate
{
    private static final Pattern VALID_URI = Pattern.compile("^/(([\\w\\-]+|\\{([a-zA-Z][\\w]*)\\})(;*)/?)+(\\.\\w+$)?|^/$");
    private static final Pattern VARIABLE = Pattern.compile("\\{([a-zA-Z]\\w*)\\}");
    private static final String VARIABLE_REGEX = "(.*?)";

    private String template;
    private Pattern regex;
    private String[] vars;
    private int charCnt;

    /**
     * Construct
     * 
     * @param template
     */
    public UriTemplate(String template)
    {
        // ensure template is provided
        if (template == null || template.length() == 0)
        {
            throw new WebScriptException("URI Template not provided");
        }

        // ensure template is syntactically correct
        Matcher validMatcher = VALID_URI.matcher(template);
        if (!validMatcher.matches())
        {
            throw new WebScriptException("URI Template malformed: " + template);
        }

        // convert uri template into equivalent regular expression
        // and extract variable names
        StringBuilder templateRegex = new StringBuilder();
        List<String> names = new ArrayList<String>();
        int charCnt = 0;
        int start = 0;
        int end = 0;
        Matcher matcher = VARIABLE.matcher(template);
        while(matcher.find())
        {
            end = matcher.start();
            charCnt += appendTemplate(template, start, end, templateRegex);
            templateRegex.append(VARIABLE_REGEX);
            String name = matcher.group(1);
            names.add(name);
            start = matcher.end();
        }
        charCnt += appendTemplate(template, start, template.length(), templateRegex);

        // initialise
        this.template = template;
        this.charCnt = charCnt;
        this.regex = Pattern.compile(templateRegex.toString());
        this.vars = new String[names.size()];
        names.toArray(this.vars);
    }

    /**
     * Helper for constructing regular expression (escaping regex chars where necessary)
     * 
     * @param template
     * @param start
     * @param end
     * @param regex
     * @return
     */
    private int appendTemplate(String template, int start, int end, StringBuilder regex)
    {
        for (int i = start; i < end; i++)
        {
            char c = template.charAt(i);
            if ("(.?)".indexOf(c) != -1)
            {
                regex.append("\\");
            }
            regex.append(c);
        }
        return end - start;
    }

    /**
     * Determine if uri is matched by this uri template
     * 
     * @param uri  uri to match
     * @return  map of variable values (or null, if no match, or empty if no vars)
     */
    public Map<String, String> match(String uri)
    {
        Map<String, String> values = null;

        if (uri != null && uri.length() > 0)
        {
            Matcher m = regex.matcher(uri);
            if (m.matches())
            {
                values = new HashMap<String, String>(m.groupCount());
                for (int i = 0; i < m.groupCount(); i++)
                {
                    String name = vars[i];
                    String value = m.group(i + 1);
                    String existingValue = values.get(name);
                    if (existingValue != null && !existingValue.equals(value))
                    {
                        return null;
                    }
                    values.put(vars[i], value);
                }
            }
        }

        return values;
    }

    /**
     * @return  get template
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * @return  get regular expression equivalent
     */
    public Pattern getRegex()
    {
        return regex;
    }

    /**
     * @return  get variable names contained in uri template
     */
    public String[] getVariableNames()
    {
        return vars;
    }

    /**
     * @return  get number of static characters in uri template
     */
    public int getStaticCharCount()
    {
        return charCnt;
    }

    @Override
    public final String toString()
    {
        String strVars = "";
        for (int i = 0; i < vars.length; i++)
        {
            strVars += vars[i];
            if (i < vars.length -1)
            {
                strVars += ",";
            }
        }
        return regex.toString() + " (vars=[" + strVars + "])"; 
    }

    @Override
    public final int hashCode()
    {
        return regex.hashCode();
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (!(obj instanceof UriTemplate))
        {
            return false;
        }
        return regex.equals(((UriTemplate)obj).regex);
    }
}
