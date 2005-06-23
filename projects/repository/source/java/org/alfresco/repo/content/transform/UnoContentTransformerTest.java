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
package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @see org.alfresco.repo.content.transform.UnoContentTransformer
 * 
 * @author Derek Hulley
 */
public class UnoContentTransformerTest extends AbstractContentTransformerTest
{
    private static String MIMETYPE_RUBBISH = "text/rubbish";
    
    private static final Log logger = LogFactory.getLog(UnoContentTransformerTest.class);

    private UnoContentTransformer transformer;
    
    public void onSetUpInTransaction() throws Exception
    {
        transformer = new UnoContentTransformer(mimetypeMap);
    }
    
    /**
     * @return Returns the same transformer regardless - it is allowed
     */
    protected ContentTransformer getTransformer(String sourceMimetype, String targetMimetype)
    {
        return transformer;
    }

    public void testSetUp() throws Exception
    {
        super.testSetUp();
        assertNotNull(mimetypeMap);
    }
    
    public void testReliability() throws Exception
    {
        if (!transformer.isConnected())
        {
            // no connection
            return;
        }
        double reliability = 0.0;
        reliability = transformer.getReliability(MIMETYPE_RUBBISH, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_TEXT_PLAIN, MIMETYPE_RUBBISH);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_WORD);
        assertEquals("Mimetype should be supported", 1.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_WORD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should be supported", 1.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_EXCEL, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should be supported", 0.8, reliability);
    }
}
