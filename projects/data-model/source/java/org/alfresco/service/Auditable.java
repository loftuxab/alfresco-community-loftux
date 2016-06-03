package org.alfresco.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Annotation to defined key and parameter names for the auditing API.
 * 
 * If this annotation is present on a public service interface it will be considered for auditing. If it is not present the method will never be audited.
 * 
 * Note that the service name and method name can be found from the bean definition and the method invocation.
 * 
 * @author Andy Hind
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AlfrescoPublicApi
public @interface Auditable
{
    /**
     * The names of the parameters
     * 
     * @return a String[] of parameter names, the default is an empty array.
     */
    String[] parameters() default {};
    
    /**
     * All method parameters are recorded by default.
     * This can be used to stop a parameter being written to the audit log.
     */
    boolean[] recordable() default {};
    
    /**
     * Return object are recorded by default.
     * Setting this means they can never be recorded in the audit.
     */
    boolean recordReturnedObject() default true;
}
