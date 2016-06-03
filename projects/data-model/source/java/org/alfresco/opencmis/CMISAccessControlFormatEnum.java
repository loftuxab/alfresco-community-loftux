package org.alfresco.opencmis;

/**
 * @author andyh
 *
 */
public enum CMISAccessControlFormatEnum implements EnumLabel
{
    /**
     * Report only CMIS basic permissions
     */
    CMIS_BASIC_PERMISSIONS("onlyBasicPermissions"),
    
    /**
     * May report CMIS basic permission, repository specific permissions or a mixture of both. 
     */
    REPOSITORY_SPECIFIC_PERMISSIONS("repositorySpecificPermissions");
    
    private String label;

    /**
     * Construct
     * 
     * @param label String
     */
    CMISAccessControlFormatEnum(String label)
    {
        this.label = label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.cmis.EnumLabel#label()
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Factory for CMISAclPropagationEnum
     */
    public static EnumFactory<CMISAccessControlFormatEnum> FACTORY = new EnumFactory<CMISAccessControlFormatEnum>(CMISAccessControlFormatEnum.class, CMIS_BASIC_PERMISSIONS, true);

}
