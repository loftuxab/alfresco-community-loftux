/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.AssociationRef;
import org.alfresco.repo.dictionary.metamodel.M2Association;
import org.alfresco.repo.dictionary.metamodel.M2AssociationDefinition;
import org.alfresco.repo.dictionary.metamodel.M2Class;
import org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation;
import org.alfresco.repo.dictionary.metamodel.emf.EMFClass;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Association</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl#getEmfName <em>Emf Name</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl#getEmfContainerClass <em>Emf Container Class</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl#getEmfProtected <em>Emf Protected</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl#getEmfMandatory <em>Emf Mandatory</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl#getEmfMultiple <em>Emf Multiple</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl#getEmfRequiredToClasses <em>Emf Required To Classes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMFAssociationImpl extends EObjectImpl implements EMFAssociation, M2Association
{
    /**
     * The default value of the '{@link #getEmfName() <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfName()
     * @generated
     * @ordered
     */
    protected static final String EMF_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfName() <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfName()
     * @generated
     * @ordered
     */
    protected String emfName = EMF_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfProtected() <em>Emf Protected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfProtected()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_PROTECTED_EDEFAULT = new Boolean(false);

    /**
     * The cached value of the '{@link #getEmfProtected() <em>Emf Protected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfProtected()
     * @generated
     * @ordered
     */
    protected Boolean emfProtected = EMF_PROTECTED_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfMandatory() <em>Emf Mandatory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMandatory()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_MANDATORY_EDEFAULT = new Boolean(false);

    /**
     * The cached value of the '{@link #getEmfMandatory() <em>Emf Mandatory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMandatory()
     * @generated
     * @ordered
     */
    protected Boolean emfMandatory = EMF_MANDATORY_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfMultiple() <em>Emf Multiple</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMultiple()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_MULTIPLE_EDEFAULT = new Boolean(true);

    /**
     * The cached value of the '{@link #getEmfMultiple() <em>Emf Multiple</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMultiple()
     * @generated
     * @ordered
     */
    protected Boolean emfMultiple = EMF_MULTIPLE_EDEFAULT;

    /**
     * The cached value of the '{@link #getEmfRequiredToClasses() <em>Emf Required To Classes</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfRequiredToClasses()
     * @generated
     * @ordered
     */
    protected EList emfRequiredToClasses = null;

    /**
     * Association Reference
     */
    protected AssociationRef assocRef = null;
    
    /**
     * Read-only Association Definition
     */
    protected AssociationDefinition associationDefinition = null;
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFAssociationImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass()
    {
        return EmfPackage.eINSTANCE.getEMFAssociation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmfName()
    {
        return emfName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfName(String newEmfName)
    {
        String oldEmfName = emfName;
        emfName = newEmfName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_ASSOCIATION__EMF_NAME, oldEmfName, emfName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFClass getEmfContainerClass()
    {
        if (eContainerFeatureID != EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS) return null;
        return (EMFClass)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfProtected()
    {
        return emfProtected;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfProtected(Boolean newEmfProtected)
    {
        Boolean oldEmfProtected = emfProtected;
        emfProtected = newEmfProtected;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_ASSOCIATION__EMF_PROTECTED, oldEmfProtected, emfProtected));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfMandatory()
    {
        return emfMandatory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfMandatory(Boolean newEmfMandatory)
    {
        Boolean oldEmfMandatory = emfMandatory;
        emfMandatory = newEmfMandatory;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_ASSOCIATION__EMF_MANDATORY, oldEmfMandatory, emfMandatory));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfMultiple()
    {
        return emfMultiple;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfMultiple(Boolean newEmfMultiple)
    {
        Boolean oldEmfMultiple = emfMultiple;
        emfMultiple = newEmfMultiple;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_ASSOCIATION__EMF_MULTIPLE, oldEmfMultiple, emfMultiple));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getEmfRequiredToClasses()
    {
        if (emfRequiredToClasses == null)
        {
            emfRequiredToClasses = new EObjectResolvingEList(EMFClass.class, this, EmfPackage.EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES);
        }
        return emfRequiredToClasses;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
    {
        if (featureID >= 0)
        {
            switch (eDerivedStructuralFeatureID(featureID, baseClass))
            {
                case EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
    {
        if (featureID >= 0)
        {
            switch (eDerivedStructuralFeatureID(featureID, baseClass))
            {
                case EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS:
                    return eBasicSetContainer(null, EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs)
    {
        if (eContainerFeatureID >= 0)
        {
            switch (eContainerFeatureID)
            {
                case EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS:
                    return eContainer.eInverseRemove(this, EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS, EMFClass.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_ASSOCIATION__EMF_NAME:
                return getEmfName();
            case EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS:
                return getEmfContainerClass();
            case EmfPackage.EMF_ASSOCIATION__EMF_PROTECTED:
                return getEmfProtected();
            case EmfPackage.EMF_ASSOCIATION__EMF_MANDATORY:
                return getEmfMandatory();
            case EmfPackage.EMF_ASSOCIATION__EMF_MULTIPLE:
                return getEmfMultiple();
            case EmfPackage.EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                return getEmfRequiredToClasses();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_ASSOCIATION__EMF_NAME:
                setEmfName((String)newValue);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_PROTECTED:
                setEmfProtected((Boolean)newValue);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_MANDATORY:
                setEmfMandatory((Boolean)newValue);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_MULTIPLE:
                setEmfMultiple((Boolean)newValue);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                getEmfRequiredToClasses().clear();
                getEmfRequiredToClasses().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_ASSOCIATION__EMF_NAME:
                setEmfName(EMF_NAME_EDEFAULT);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_PROTECTED:
                setEmfProtected(EMF_PROTECTED_EDEFAULT);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_MANDATORY:
                setEmfMandatory(EMF_MANDATORY_EDEFAULT);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_MULTIPLE:
                setEmfMultiple(EMF_MULTIPLE_EDEFAULT);
                return;
            case EmfPackage.EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                getEmfRequiredToClasses().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_ASSOCIATION__EMF_NAME:
                return EMF_NAME_EDEFAULT == null ? emfName != null : !EMF_NAME_EDEFAULT.equals(emfName);
            case EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS:
                return getEmfContainerClass() != null;
            case EmfPackage.EMF_ASSOCIATION__EMF_PROTECTED:
                return EMF_PROTECTED_EDEFAULT == null ? emfProtected != null : !EMF_PROTECTED_EDEFAULT.equals(emfProtected);
            case EmfPackage.EMF_ASSOCIATION__EMF_MANDATORY:
                return EMF_MANDATORY_EDEFAULT == null ? emfMandatory != null : !EMF_MANDATORY_EDEFAULT.equals(emfMandatory);
            case EmfPackage.EMF_ASSOCIATION__EMF_MULTIPLE:
                return EMF_MULTIPLE_EDEFAULT == null ? emfMultiple != null : !EMF_MULTIPLE_EDEFAULT.equals(emfMultiple);
            case EmfPackage.EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                return emfRequiredToClasses != null && !emfRequiredToClasses.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString()
    {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (emfName: ");
        result.append(emfName);
        result.append(", emfProtected: ");
        result.append(emfProtected);
        result.append(", emfMandatory: ");
        result.append(emfMandatory);
        result.append(", emfMultiple: ");
        result.append(emfMultiple);
        result.append(')');
        return result.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#getReference()
     */
    public AssociationRef getReference()
    {
        if (assocRef == null)
        {
            assocRef = new AssociationRef(getContainerClass().getClassDefinition().getReference(),
                    getName());
        }
        return assocRef;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#getContainerClass()
     */
    public M2Class getContainerClass()
    {
        return (M2Class)getEmfContainerClass();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#getName()
     */
    public String getName()
    {
        return getEmfName();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#setName(java.lang.String)
     */
    public void setName(String name)
    {
        setEmfName(name);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#isProtected()
     */
    public boolean isProtected()
    {
        return getEmfProtected();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#setProtected(boolean)
     */
    public void setProtected(boolean isProtected)
    {
        setEmfProtected(isProtected);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#isMandatory()
     */
    public boolean isMandatory()
    {
        return getEmfMandatory();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#setMandatory(boolean)
     */
    public void setMandatory(boolean isMandatory)
    {
        setEmfMandatory(isMandatory);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#isMultiValued()
     */
    public boolean isMultiValued()
    {
        return getEmfMultiple();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#setMultiValued(boolean)
     */
    public void setMultiValued(boolean isMultiValued)
    {
        setEmfMultiple(isMultiValued);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#getRequiredToClasses()
     */
    public List<M2Class> getRequiredToClasses()
    {
        return (List)getEmfRequiredToClasses();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Association#getAssociationDefintion()
     */
    public AssociationDefinition getAssociationDefintion()
    {
        if (associationDefinition == null)
        {
            associationDefinition = M2AssociationDefinition.create(this);
        }
        return associationDefinition;
    }

} //EMFAssociationImpl
