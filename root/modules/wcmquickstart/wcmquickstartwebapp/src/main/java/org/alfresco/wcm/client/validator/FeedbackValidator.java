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
package org.alfresco.wcm.client.validator;

import java.util.regex.Pattern;

import org.alfresco.wcm.client.VisitorFeedback;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validate feedback input
 * @author Chris Lack
 */
public class FeedbackValidator implements Validator 
{
	private final static Pattern EMAIL_PATTERN = Pattern.compile(".+@.+\\.[a-z]+");
	
	public boolean supports(Class<?> clazz) 
	{
        return VisitorFeedback.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) 
    {
    	VisitorFeedback feedback = (VisitorFeedback) obj;

        ValidationUtils.rejectIfEmpty(errors, "visitorName", "comments.write.null");
        ValidationUtils.rejectIfEmpty(errors, "visitorEmail", "comments.write.null");
        if (feedback.getVisitorEmail() != null && feedback.getVisitorEmail().length() > 0 && 
        	 ! EMAIL_PATTERN.matcher(feedback.getVisitorEmail()).matches())
        {
        	errors.rejectValue("visitorEmail", "comments.write.invalid");
        }
        ValidationUtils.rejectIfEmpty(errors, "comment", "comments.write.null");        
   }
}