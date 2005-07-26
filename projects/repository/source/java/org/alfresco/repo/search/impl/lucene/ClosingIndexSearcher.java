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
 *
 * Created on 26-Jul-2005
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public class ClosingIndexSearcher extends IndexSearcher
{
    IndexReader reader;

    public ClosingIndexSearcher(String path) throws IOException
    {
        super(path);
    }

    public ClosingIndexSearcher(Directory directory) throws IOException
    {
        super(directory);
    }

    public ClosingIndexSearcher(IndexReader r)
    {
        super(r);
        this.reader = r;
    }

    @Override
    public void close() throws IOException
    {
        super.close();
        if(reader != null)
        {
            reader.close();
        }
    }

}
