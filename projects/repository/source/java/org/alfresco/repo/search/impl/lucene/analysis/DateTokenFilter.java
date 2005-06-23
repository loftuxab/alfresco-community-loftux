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
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.util.CachingDateFormat;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * @author andyh
 */
public class DateTokenFilter extends Tokenizer
{
    Tokenizer baseTokeniser;
    
    public DateTokenFilter(Reader in)
    {
        super(in);
        baseTokeniser = new WhitespaceTokenizer(in);
    }

    public Token next() throws IOException
    {
        SimpleDateFormat df = CachingDateFormat.getDateFormat();
        SimpleDateFormat dof = CachingDateFormat.getDateOnlyFormat();
        Token candidate;
        while((candidate = baseTokeniser.next()) != null)
        {
            Date date;
            try
            {
                date = df.parse(candidate.termText());
            }
            catch (ParseException e)
            {
               continue;
            }
            String valueString = dof.format(date);
            Token integerToken = new Token(valueString, candidate.startOffset(), candidate.startOffset(),
                    candidate.type());
            return integerToken;
        }
        return null;
    }
}