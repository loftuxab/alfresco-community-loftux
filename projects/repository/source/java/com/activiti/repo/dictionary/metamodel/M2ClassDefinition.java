package com.activiti.repo.dictionary.metamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.ref.QName;




public class M2ClassDefinition implements ClassDefinition
{
    protected M2Class m2Class;
    
    
    public static M2ClassDefinition create(M2Class m2Class)
    {
        if (m2Class instanceof M2Type)
        {
            return new M2TypeDefinition((M2Type)m2Class);
        }
        else if (m2Class instanceof M2Aspect)
        {
            return new M2AspectDefinition((M2Aspect)m2Class);
        }
        else
        {
            return new M2ClassDefinition(m2Class);
        }
    }
    
    
    /*package*/ M2ClassDefinition(M2Class m2Class)
    {
        this.m2Class = m2Class;

        // Force load-on-demand of related entities
        this.m2Class.getSuperClass();
        this.m2Class.getInheritedProperties();
        this.m2Class.getInheritedAssociations();
    }

    
    /*package*/ M2Class getM2Class()
    {
        return m2Class;
    }

    
    public ClassRef getReference()
    {
        return m2Class.getReference();
    }

    
    public QName getName()
    {
        return m2Class.getName();
    }


    public boolean isAspect()
    {
        return (m2Class instanceof M2Aspect);
    }

    
    public ClassRef getSuperClass()
    {
        return m2Class.getSuperClass().getReference();
    }
    
    
    public Map getProperties()
    {
        List aggregatedProperties = aggregateProperties();
        Map propertyDefs = new HashMap(aggregatedProperties.size());
        for (Iterator iter = aggregatedProperties.iterator(); iter.hasNext(); /**/)
        {
            M2Property m2Property = (M2Property)iter.next();
            propertyDefs.put(m2Property.getPropertyDefinition().getReference(), m2Property.getPropertyDefinition());
        }
        return Collections.unmodifiableMap(propertyDefs);
    }

    
    protected List aggregateProperties()
    {
        return m2Class.getInheritedProperties();
    }
    
    
    public Map getAssociations()
    {
        List aggregatedAssociations = aggregateAssociations();
        Map assocDefs = new HashMap(aggregatedAssociations.size());
        for (Iterator iter = aggregatedAssociations.iterator(); iter.hasNext(); /**/)
        {
            M2Association m2Assoc = (M2Association)iter.next();
            assocDefs.put(m2Assoc.getAssociationDefintion().getReference(), m2Assoc.getAssociationDefintion());
        }
        return Collections.unmodifiableMap(assocDefs);
    }


    protected List aggregateAssociations()
    {
        return m2Class.getInheritedAssociations();
    }
        

}
