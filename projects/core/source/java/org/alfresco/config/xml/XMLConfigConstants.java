package org.alfresco.config.xml;

/**
 * Constants for the XML configuration service
 * 
 * @author gavinc
 */
public interface XMLConfigConstants
{
    // XML attribute names
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_CLASS = "class";
    public static final String ATTR_ELEMENT_NAME = "element-name";
    public static final String ATTR_EVALUATOR = "evaluator";
    public static final String ATTR_CONDITION = "condition";

    // XML element names
    public static final String ELEMENT_PLUG_INS = "plug-ins";
    public static final String ELEMENT_CONFIG = "config";
    public static final String ELEMENT_EVALUATORS = "evaluators";
    public static final String ELEMENT_EVALUATOR = "evaluator";
    public static final String ELEMENT_ELEMENT_READERS = "element-readers";
    public static final String ELEMENT_ELEMENT_READER = "element-reader";
}
