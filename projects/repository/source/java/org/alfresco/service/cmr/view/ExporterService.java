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

import java.io.OutputStream;


/**
 * Exporter Service
 * 
 * @author David Caruana
 */
public interface ExporterService
{
    /**
     * Export a view of the Repository.
     * 
     * All repository information is exported to the single output stream.  This means that any
     * content properties are base64 encoded. 
     * 
     * @param output  the output stream to export to
     * @param location  the location within the Repository to export
     * @param exportChildren  export children as well
     * @param progress  exporter callback for tracking progress of export
     */
    public void exportView(OutputStream output, Location location, boolean exportChildren, Exporter progress)
        throws ExporterException;

    /**
     * Export a view of the Repository.
     * 
     * This export supports the custom handling of content properties.
     * 
     * @param output the output stream to export to
     * @param streamHandler  the custom handler for content properties
     * @param location  the location witihn the Repository to export
     * @param exportChildren  export children as well
     * @param progress  exporter callback for tracking progress of export
     */
    public void exportView(OutputStream output, ExportStreamHandler streamHandler, Location location, boolean exportChildren, Exporter progress)
        throws ExporterException;

}
