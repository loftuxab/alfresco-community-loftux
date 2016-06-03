
package org.alfresco.repo.dictionary;

/**
 * A simple POJO to encapsulate the custom models' statistics information.
 *
 * @author Jamal Kaabi-Mofrad
 */
public class CustomModelsInfo
{
    private int numberOfActiveModels;
    private int numberOfActiveTypes;
    private int numberOfActiveAspects;

    public int getNumberOfActiveModels()
    {
        return this.numberOfActiveModels;
    }

    public void setNumberOfActiveModels(int numberOfActiveModels)
    {
        this.numberOfActiveModels = numberOfActiveModels;
    }

    public int getNumberOfActiveTypes()
    {
        return this.numberOfActiveTypes;
    }

    public void setNumberOfActiveTypes(int numberOfActiveTypes)
    {
        this.numberOfActiveTypes = numberOfActiveTypes;
    }

    public int getNumberOfActiveAspects()
    {
        return this.numberOfActiveAspects;
    }

    public void setNumberOfActiveAspects(int numberOfActiveAspects)
    {
        this.numberOfActiveAspects = numberOfActiveAspects;
    }
}
