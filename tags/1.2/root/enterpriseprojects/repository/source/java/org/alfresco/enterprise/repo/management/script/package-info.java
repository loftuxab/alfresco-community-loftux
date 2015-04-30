/**
 * This package contains the types which define and implement the JMX via JavaScript feature. See ALF-17113.
 * <p/>
 * 
 * The major Java types used to achieve this are as follows:
 * <ul>
 *   <li>{@link JmxScript} defines the operations which are available on the JavaScript root object "jmx".</li>
 *   <li>{@link JmxScriptProcessorExtension} is the object which is injected into the {@link ScriptService}.</li>
 *   <li>{@link JmxScriptImpl} sits behind {@link JmxScriptProcessorExtension} with a {@link MethodSecurityInterceptor} in front of it
 *       in order to apply role-based authorisation.</li>
 *   <li>{@link JmxScriptImpl} returns {@link ScriptMBean script-friendly MBeans} from which {@link ScriptMBeanAttribute script-friendly attributes}
 *       can be obtained.</li>
 * </ul>
 */
package org.alfresco.enterprise.repo.management.script;
