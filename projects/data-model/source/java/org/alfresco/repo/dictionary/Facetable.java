package org.alfresco.repo.dictionary;

/**
 * How faceting is to be supported
 * 
 * @author Andy
 *
 */
public enum Facetable
{
    /**
     * TRUE - faceting is required and enhanced support for this is provided if possible
     */
    TRUE,
    /**
     * UNSET - facet support is unset, standard support is assumed
     */
    UNSET,
    /**
     * FALSE - feceting is not required and will not be supported
     */
    FALSE;
    
    public static String serializer(Facetable facetable) {
        return facetable.toString();
    }

    public static Facetable deserializer(String value) {
        if (value == null) {
            return null;
        } else if (value.equalsIgnoreCase(TRUE.toString())) {
            return TRUE;
        } else if (value.equalsIgnoreCase(FALSE.toString())) {
            return FALSE;
        } else if (value.equalsIgnoreCase(UNSET.toString())) {
            return UNSET;
        } else {
            throw new IllegalArgumentException(
                    "Invalid facetable enum value: " + value);
        }
    }
}
