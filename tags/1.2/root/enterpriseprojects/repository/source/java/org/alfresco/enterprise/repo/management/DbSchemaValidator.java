/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import org.alfresco.repo.domain.schema.SchemaBootstrap;

/**
 * DbSchemaValidatorMBean implementation. Allows users to validate the
 * database schema on demand.
 * 
 * @author Matt Ward
 */
public class DbSchemaValidator implements DbSchemaValidatorMBean
{
    private SchemaBootstrap schemaBootstrap;
    
    
    /**
     * @param schemaBootstrap
     */
    public DbSchemaValidator(SchemaBootstrap schemaBootstrap)
    {
        this.schemaBootstrap = schemaBootstrap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateSchema()
    {
        String outputFileNameTemplate = "Alfresco-{0}-Validation-{1}-";
        schemaBootstrap.validateSchema(outputFileNameTemplate, null);
    }
}
