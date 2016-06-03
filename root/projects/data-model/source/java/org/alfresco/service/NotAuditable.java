package org.alfresco.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Specifically indicate that a method is not to be audited.
 * This is a marker annotation.
 * 
 * @author Andy Hind
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AlfrescoPublicApi
public @interface NotAuditable
{

}
