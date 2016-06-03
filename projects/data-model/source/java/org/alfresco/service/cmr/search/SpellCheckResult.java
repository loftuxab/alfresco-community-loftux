
package org.alfresco.service.cmr.search;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jamal Kaabi-Mofrad
 * @since 5.0
 */
public class SpellCheckResult implements Serializable
{
    private static final long serialVersionUID = -4270859221984496771L;

    private final String resultName;
    private final List<String> results;
    private final boolean searchedFor;
    private final boolean spellCheckExist;

    public SpellCheckResult(String resultName, List<String> results, boolean searchedFor)
    {
        this.resultName = resultName;
        this.results = results;
        this.searchedFor = searchedFor;
        this.spellCheckExist = (resultName == null) ? false : true;
    }

    /**
     * @return the resultName
     */
    public String getResultName()
    {
        return this.resultName;
    }

    /**
     * @return the results
     */
    public List<String> getResults()
    {
        return this.results;
    }

    /**
     * @return the searchedFor
     */
    public boolean isSearchedFor()
    {
        return this.searchedFor;
    }

    /**
     * @return the spellCheckExist
     */
    public boolean isSpellCheckExist()
    {
        return this.spellCheckExist;
    }
}
