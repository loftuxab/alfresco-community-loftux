/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.search.impl.lucene.analysis.PathAnalyser;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.namespace.QName;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Analyse properties according to the property definition.
 * 
 * The default is to use the standard tokeniser. The tokeniser should not have
 * been called when indexeing properties that require no tokenisation. (tokenise
 * should be set to false when adding the field to the document)
 * 
 * @author andyh
 * 
 */

public class LuceneAnalyser extends Analyzer
{

    private DictionaryService dictionaryService;

    private Analyzer defaultAnalyser;

    private Map<String, Analyzer> analysers = new HashMap<String, Analyzer>();

    /**
     * Constructs with a default standard analyser
     * 
     * @param defaultAnalyzer
     *            Any fields not specifically defined to use a different
     *            analyzer will use the one provided here.
     */
    public LuceneAnalyser(DictionaryService dictionaryService)
    {
        this(new StandardAnalyzer());
        this.dictionaryService = dictionaryService;
    }

    /**
     * Constructs with default analyzer.
     * 
     * @param defaultAnalyzer
     *            Any fields not specifically defined to use a different
     *            analyzer will use the one provided here.
     */
    public LuceneAnalyser(Analyzer defaultAnalyser)
    {
        this.defaultAnalyser = defaultAnalyser;
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        Analyzer analyser = (Analyzer) analysers.get(fieldName);
        if (analyser == null)
        {
            analyser = findAnalyser(fieldName);
        }
        return analyser.tokenStream(fieldName, reader);
    }

    private Analyzer findAnalyser(String fieldName)
    {
        Analyzer analyser;
        if (fieldName.equals("PATH"))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals("QNAME"))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals("TYPE"))
        {
            throw new UnsupportedOperationException("TYPE mut not be tokenised");
        }
        else if (fieldName.equals("ASPECT"))
        {
            throw new UnsupportedOperationException("ASPECT mut not be tokenised");
        }
        else if (fieldName.equals("ANCESTOR"))
        {
            analyser = new WhitespaceAnalyzer();
        }
        else if (fieldName.startsWith("@"))
        {
            QName propertyQName = QName.createQName(fieldName.substring(1));
            PropertyDefinition propertyDef = dictionaryService.getProperty(propertyQName);
            PropertyTypeDefinition propertyType = (propertyDef == null) ? dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT) : propertyDef.getPropertyType();
            String analyserClassName = propertyType.getAnalyserClassName();
            try
            {
                Class<?> clazz = Class.forName(analyserClassName);
                analyser = (Analyzer)clazz.newInstance();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Unable to load analyser for property " + fieldName.substring(1) + " of type " + propertyType.getName() + " using " + analyserClassName);
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException("Unable to load analyser for property " + fieldName.substring(1) + " of type " + propertyType.getName() + " using " + analyserClassName);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Unable to load analyser for property " + fieldName.substring(1) + " of type " + propertyType.getName() + " using " + analyserClassName);
            }
        }
        else
        {
            analyser = defaultAnalyser;
        }
        analysers.put(fieldName, analyser);
        return analyser;
    }
}
