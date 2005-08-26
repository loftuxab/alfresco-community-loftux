/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * @author andyh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class IntegerTokenFilter extends Tokenizer
{
    public final static String INTEGER_FORMAT = "0000000000";

    Tokenizer baseTokeniser;
    
    public IntegerTokenFilter(Reader in)
    {
        super(in);
        baseTokeniser = new StandardTokenizer(in);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.TokenStream#next()
     */

    public Token next() throws IOException
    {
        NumberFormat nf = new DecimalFormat(INTEGER_FORMAT);
        Token candidate;
        while((candidate = baseTokeniser.next()) != null)
        {
            Integer integer = Integer.valueOf(candidate.termText());
            String valueString = nf.format(integer);
            Token integerToken = new Token(valueString, candidate.startOffset(), candidate.startOffset(),
                    candidate.type());
            return integerToken;
        }
        return null;
    }
}