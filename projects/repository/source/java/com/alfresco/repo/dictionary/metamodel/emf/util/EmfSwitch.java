/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.util;

import org.alfresco.repo.dictionary.metamodel.emf.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage
 * @generated
 */
public class EmfSwitch {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static EmfPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfSwitch()
    {
        if (modelPackage == null)
        {
            modelPackage = EmfPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch(EObject theEObject)
    {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject)
    {
        if (theEClass.eContainer() == modelPackage)
        {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        else
        {
            List eSuperTypes = theEClass.getESuperTypes();
            return
                eSuperTypes.isEmpty() ?
                    defaultCase(theEObject) :
                    doSwitch((EClass)eSuperTypes.get(0), theEObject);
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject)
    {
        switch (classifierID)
        {
            case EmfPackage.EMF_CLASS:
            {
                EMFClass emfClass = (EMFClass)theEObject;
                Object result = caseEMFClass(emfClass);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_PROPERTY:
            {
                EMFProperty emfProperty = (EMFProperty)theEObject;
                Object result = caseEMFProperty(emfProperty);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_ASPECT:
            {
                EMFAspect emfAspect = (EMFAspect)theEObject;
                Object result = caseEMFAspect(emfAspect);
                if (result == null) result = caseEMFClass(emfAspect);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_ASSOCIATION:
            {
                EMFAssociation emfAssociation = (EMFAssociation)theEObject;
                Object result = caseEMFAssociation(emfAssociation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_CHILD_ASSOCIATION:
            {
                EMFChildAssociation emfChildAssociation = (EMFChildAssociation)theEObject;
                Object result = caseEMFChildAssociation(emfChildAssociation);
                if (result == null) result = caseEMFAssociation(emfChildAssociation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_PROPERTY_TYPE:
            {
                EMFPropertyType emfPropertyType = (EMFPropertyType)theEObject;
                Object result = caseEMFPropertyType(emfPropertyType);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_TYPE:
            {
                EMFType emfType = (EMFType)theEObject;
                Object result = caseEMFType(emfType);
                if (result == null) result = caseEMFClass(emfType);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_NAMESPACE_PREFIX:
            {
                EMFNamespacePrefix emfNamespacePrefix = (EMFNamespacePrefix)theEObject;
                Object result = caseEMFNamespacePrefix(emfNamespacePrefix);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case EmfPackage.EMF_NAMESPACE_URI:
            {
                EMFNamespaceURI emfNamespaceURI = (EMFNamespaceURI)theEObject;
                Object result = caseEMFNamespaceURI(emfNamespaceURI);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Class</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Class</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFClass(EMFClass object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Property</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Property</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFProperty(EMFProperty object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Aspect</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Aspect</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFAspect(EMFAspect object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Association</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Association</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFAssociation(EMFAssociation object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Child Association</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Child Association</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFChildAssociation(EMFChildAssociation object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Property Type</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Property Type</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFPropertyType(EMFPropertyType object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Type</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Type</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFType(EMFType object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Namespace Prefix</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Namespace Prefix</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFNamespacePrefix(EMFNamespacePrefix object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EMF Namespace URI</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EMF Namespace URI</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseEMFNamespaceURI(EMFNamespaceURI object)
    {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase(EObject object)
    {
        return null;
    }

} //EmfSwitch
