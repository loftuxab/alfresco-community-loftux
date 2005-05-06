package org.alfresco.repo.dictionary.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.ref.QName;

/**
 * Utilities for managing Data Dictionary References
 * 
 * @author David Caruana
 */
public class M2References
{
    /**
     * Construct an immutable list of Class Definitions
     * 
     * @param m2Classes  list of Class to construct definitions from
     * @return  list of class definitions
     */
    public static List<ClassDefinition> createClassDefList(Collection<? extends M2Class> m2Classes)
    {
        List<ClassDefinition> defs = new ArrayList<ClassDefinition>(m2Classes.size());
        for (M2Class m2Class : m2Classes)
        {
            defs.add(m2Class.getClassDefinition());
        }
        return Collections.unmodifiableList(defs);
    }


    /**
     * Construct a list of Aspect Definitions
     * 
     * @param m2Aspects  list of Aspects to construct definitions from
     * @return  list of aspect definitions
     */
    public static List<AspectDefinition> createAspectDefList(Collection<? extends M2Aspect> m2Aspects)
    {
        List<AspectDefinition> defs = new ArrayList<AspectDefinition>(m2Aspects.size());
        for (M2Class m2Aspect : m2Aspects)
        {
            defs.add((AspectDefinition) m2Aspect.getClassDefinition());
        }
        return Collections.unmodifiableList(defs);
    }


    /**
     * Construct an immutable list of Property definitions
     * 
     * @param m2Properties  list of properties to construct definitions from
     * @return  list of property defintions
     */
    public static List<PropertyDefinition> createPropertyDefList(Collection<M2Property> m2Properties)
    {
        List<PropertyDefinition> defs = new ArrayList<PropertyDefinition>(m2Properties.size());
        for (M2Property m2Property : m2Properties)
        {
            defs.add(m2Property.getPropertyDefinition());
        }
        return Collections.unmodifiableList(defs);
    }
    

    /**
     * Construct an immutable list of Class References
     * 
     * @param qnames  list of QNames to construct references from
     * @return  list of class references
     */
    public static Collection<ClassRef> createQNameClassRefCollection(Collection<QName> qnames)
    {
        List<ClassRef> ddrefs = new ArrayList<ClassRef>(qnames.size());
        for (QName qname : qnames)
        {
            ClassRef classRef = new ClassRef(qname);
            ddrefs.add(classRef);
        }
        return Collections.unmodifiableList(ddrefs);
    }
}
