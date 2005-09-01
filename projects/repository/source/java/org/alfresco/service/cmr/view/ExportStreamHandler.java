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
package org.alfresco.service.cmr.view;

import java.io.InputStream;


/**
 * Contract for a custom content property exporter.
 * 
 * @author David Caruana
 *
 */
public interface ExportStreamHandler
{

    /**
     * Call-back for handling the export of content stream.
     * 
     * @param exportStream stream to export
     * @return the URL to the location of the exported content
     */
    public String exportStream(InputStream exportStream);
    
}
