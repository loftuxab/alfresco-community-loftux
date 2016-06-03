/**
 * 
 */
package org.alfresco.webdrone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Annotation can be used one off data setup before test suites run.
 *
 * @author Shan Nagarajan
 * @since  1.7
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface DataSetup
{
    
    /**
     * Whether methods on this class/method are enabled.
     */
    public boolean enabled() default true;
    
    /**
     * The list of groups this method belongs to.
     */
    public DataGroup[] groups() default {};
    
    /**
     * The Test Link Id for the {@link DataSetup} Method.
     * 
     * @return The Test Link Id.
     */
    public String testLinkId() default "";
    
}