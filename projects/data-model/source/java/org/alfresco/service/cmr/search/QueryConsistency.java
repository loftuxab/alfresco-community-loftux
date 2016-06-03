package org.alfresco.service.cmr.search;

/**
 * @author Andy
 *
 */
public enum QueryConsistency
{
    EVENTUAL, TRANSACTIONAL, DEFAULT, TRANSACTIONAL_IF_POSSIBLE, HYBRID;
}
