package org.alfresco.benchmark.dataprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.benchmark.dataprovider.PropertyProfile.PropertyRestriction;
import org.alfresco.benchmark.dataprovider.PropertyProfile.PropertyType;
import org.alfresco.util.BaseAlfrescoSpringTest;
import org.alfresco.util.GUID;

public class DataProviderComponentImplTest extends BaseAlfrescoSpringTest
{
    private DataProviderComponent dataProviderComponent;
    
    public void setDataProviderComponent(DataProviderComponent dataProviderComponent)
    {
        this.dataProviderComponent = dataProviderComponent;
    }
    
    public void testGetTextPropertyData()
    {
        List<PropertyProfile> propertyProfiles = new ArrayList<PropertyProfile>(5);
        
        for (int i = 0; i < 10; i++)
        {
            PropertyProfile profile = new PropertyProfile();
            profile.setPropertyName(GUID.generate());
            profile.setPropertyType(PropertyType.TEXT);
            profile.setRestriction(PropertyRestriction.MIN_LENGTH, 35);
            profile.setRestriction(PropertyRestriction.MAX_LENGTH, 150);
            propertyProfiles.add(profile);          
        }
        
        Map<String, Object> properties = this.dataProviderComponent.getPropertyData(null, propertyProfiles);
        
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    
    public void testGetContentPropertyData()
    {
        List<PropertyProfile> propertyProfiles = new ArrayList<PropertyProfile>(5);
    
        for (int i = 0; i < 10; i++)
        {
            PropertyProfile profile = new PropertyProfile();
            profile.setPropertyName(GUID.generate());
            profile.setPropertyType(PropertyType.CONTENT);
            propertyProfiles.add(profile);          
        }
        
        Map<String, Object> properties = this.dataProviderComponent.getPropertyData(null, propertyProfiles);
        
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            ContentData contentData = (ContentData)entry.getValue();
            System.out.println(entry.getKey() + ": " + "location=" + contentData.getFile().getPath() + "; mimetype=" + contentData.getMimetype() + "; enconding=" + contentData.getEncoding() + "; size=" + contentData.getSize() + "; name=" + contentData.getName());
        }
    }
}
