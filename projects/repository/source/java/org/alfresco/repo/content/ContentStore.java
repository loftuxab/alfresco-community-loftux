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
package org.alfresco.repo.content;

import java.util.List;

import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;

/**
 * Provides low-level retrieval of content
 * {@link org.alfresco.service.cmr.repository.ContentReader readers} and
 * {@link org.alfresco.service.cmr.repository.ContentWriter writers}.
 * <p>
 * Implementations of this interface should be soley responsible for
 * providing persistence and retrieval of the content against a
 * <code>NodeRef</code>.  Problems such as whether the node exists or
 * not are irrelevant - rather the <code>NodeRef</code> should be regarded
 * as key against which to store the content.
 * <p>
 * The nature of the API means that it is <b>never</b> possible to
 * dictate the location of a write operation.
 * 
 * @author Derek Hulley
 */
public interface ContentStore
{
    /**
     * Get the accessor with which to read from the content
     * at the given URL.  The reader is <b>stateful</b> and
     * can <b>only be used once</b>.
     * 
     * @param contentUrl the store-specific URL where the content is located
     * @return Returns a read-only content accessor for the given URL.  There may
     *      be no content at the given URL, but the reader must still be returned.
     * @throws ContentIOException
     */
    public ContentReader getReader(String contentUrl) throws ContentIOException;
    
    /**
     * Get an accessor with which to write content to an anonymous location
     * within the store.  The writer is <b>stateful</b> and can
     * <b>only be used once</b>.
     * <p>
     * Every call to this method will return a writer onto a <b>new</b>
     * content URL.  It is never possible to write the same physical
     * location twice.
     *  
     * @return Returns a write-only content accessor
     * @throws ContentIOException
     *
     * @see #getWriter(NodeRef)
     * @see ContentWriter#addListener(ContentStreamListener)
     * @see ContentWriter#getContentUrl()
     */
    public ContentWriter getWriter() throws ContentIOException;

    /**
     * Get a list of all content in the store
     * 
     * @return Returns a complete list of the URLs of all available content
     *      in the store
     * @throws ContentIOException
     */
    public List<String> listUrls() throws ContentIOException;
    
    /**
     * Deletes the content at the given URL.
     * <p>
     * A delete cannot be forced since it is much better to have the
     * file remain longer than desired rather than deleted prematurely.
     * The store implementation may choose to safeguard files for certain
     * minimum period, in which case all files younger than a certain
     * age will not be deleted.
     * 
     * @param contentUrl the URL of the content to delete
     * @return Return true if the content was deleted (either by this or
     *      another operation), otherwise false
     * @throws ContentIOException
     */
    public boolean delete(String contentUrl) throws ContentIOException;
}
