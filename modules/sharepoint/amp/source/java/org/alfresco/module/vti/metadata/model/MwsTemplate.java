
package org.alfresco.module.vti.metadata.model;

/**
 * @author PavelYur
 *
 */
public class MwsTemplate
{

    private String name;
    
    private String title;
    
    private int id;
    
    private String description;
    
    private String imageUrl;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
    
    public String getImageUrl()
    {
        return imageUrl;
    }
    
    public static MwsTemplate getDefault()    
    {
        MwsTemplate result = new MwsTemplate();
        
        result.setName("MPS#0");
        result.setTitle("Alfresco Meeting Workspace");
        result.setId(2);
        result.setDescription("Standard Alfresco Meeting Workspace. This Meeting Workspace contains the following:  Calendar, Members.");
        result.setImageUrl("");
        
        return result;
    }
}