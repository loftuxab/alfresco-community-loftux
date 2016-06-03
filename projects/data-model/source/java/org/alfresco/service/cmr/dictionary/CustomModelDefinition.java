
package org.alfresco.service.cmr.dictionary;

import java.util.Collection;

/**
 * @author Jamal Kaabi-Mofrad
 */
public interface CustomModelDefinition extends ModelDefinition
{

    /**
     * Whether the model is active or not
     *
     * @return true if the model is active, false otherwise
     */
    public boolean isActive();

    /**
     * Returns the model description
     *
     * @return the model description
     */
    public String getDescription();

    /**
     * Returns a {@link Collection} of the model {@link TypeDefinition}s
     *
     * @return an unmodifiable collection of the model types definitions, or an empty collection
     */
    public Collection<TypeDefinition> getTypeDefinitions();

    /**
     * Returns a {@link Collection} of the model {@link AspectDefinition}s
     *
     * @return an unmodifiable collection of the model aspects definitions, or an empty collection
     */
    public Collection<AspectDefinition> getAspectDefinitions();

    /**
     * Returns a {@link Collection} of the model defined {@link ConstraintDefinition}s
     *
     * @return an unmodifiable collection of the model constraint definitions, or an empty collection
     */
    public Collection<ConstraintDefinition> getModelDefinedConstraints();
}
