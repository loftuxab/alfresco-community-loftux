/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive to output a string, truncating it at a certain number of characters.
 * The truncation is done between words.
 * Usage: <@truncate value=xxx chars=nnn/> where xxx is a variable which references string and
 * nnn is the number of characters.
 * @author Chris Lack
 */
public class TruncateDirective implements TemplateDirectiveModel
{
	@SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		if (params.size() != 2) throw new TemplateModelException("truncate directive expects two parameters");
					
		SimpleScalar valueParam = (SimpleScalar)params.get("value");
		SimpleNumber charsParam = (SimpleNumber)params.get("chars");

		if (valueParam == null || charsParam == null) 
		{
			throw new TemplateModelException("truncate directive expects value and chars parameters");
		}
		
		// Get the text and chars values
		String text = valueParam.getAsString();
		int chars = charsParam.getAsNumber().intValue();

		// Truncate the string if needed
		if (text.length() > chars) 
		{
			BreakIterator bi = BreakIterator.getWordInstance();
			bi.setText(text);
			int firstBefore = bi.preceding(chars);
			text = text.substring(0, firstBefore)+"...";
		}
		
		env.getOut().write(text);
    }
}

	