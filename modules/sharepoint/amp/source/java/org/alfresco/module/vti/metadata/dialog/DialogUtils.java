package org.alfresco.module.vti.metadata.dialog;

import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;

/**
 * <p>Utils class for dialogview method (FileOpen/Save).</p>
 *  
 * @author PavelYur
 *
 */
public class DialogUtils
{
    public static final String IMAGE_PREFIX = "images/filetypes/";
    public static final String IMAGE_POSTFIX = ".gif";
    public static final String DEFAULT_IMAGE = "images/filetypes/_default.gif";
    
    /**
     * <p>Recognize image for the given file extension, if extension
     * is empty string then default image is returned.</p>  
     * 
     * @param fileName name of the file is being displayed 
     * @return server path to the correct image
     */
    public static String getFileTypeImage(String fileName)
    {
        String image = DEFAULT_IMAGE;
        int extIndex = fileName.lastIndexOf('.');
        if (extIndex != -1 && fileName.length() > extIndex + 1)
        {
            String ext = fileName.substring(extIndex + 1).toLowerCase();

            image = IMAGE_PREFIX + ext + IMAGE_POSTFIX;                
        }

        return image;
    }
    
    /**
     * <p>According the current sorting field and sorting order return new sorting order.</p> 
     * 
     * @param sortFieldValue value of the sorting field
     * @param currentSortField current sorting field value
     * @param sort current sorting order
     * @return new sorting order
     */
    public String getSortDir(String sortFieldValue, VtiSortField currentSortField, VtiSort sort)
    {
        VtiSortField sortField = VtiSortField.value(sortFieldValue);

        if (sortField.equals(currentSortField))
        {
            if (sort.equals(VtiSort.ASC))
            {
                return VtiSort.DESC.toString();
            }
            if (sort.equals(VtiSort.DESC))
            {
                return VtiSort.ASC.toString();
            }
        }
        return VtiSort.ASC.toString();
    }

    /**
     * <p>Cast String to VtiSortField</p>
     * 
     * @param sortFieldValue String
     * @return VtiSortField
     */
    public VtiSortField getSortFieldValue(String sortFieldValue)
    {
        return VtiSortField.value(sortFieldValue);
    }

    /**
     * <p>Cast String to VtiSort</p>
     * 
     * @param sortValue String
     * @return VtiSort
     */
    public VtiSort getSortValue(String sortValue)
    {
        return VtiSort.value(sortValue);
    }
}
