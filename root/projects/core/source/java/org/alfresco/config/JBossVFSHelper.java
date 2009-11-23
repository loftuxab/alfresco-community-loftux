/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.jboss.virtual.VFS;
import org.jboss.virtual.VFSUtils;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileVisitor;
import org.jboss.virtual.VisitorAttributes;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.util.PathMatcher;

/**
 * A path matching helper capable of recursing JBoss VFS file structures. This code is based on JBoss's
 * VFSResourcePatternResolver, as discussed in <a href="https://jira.jboss.org/jira/browse/JBAS-6548">JBAS-6548</a> but
 * with bugfixes so that relative path matching works correctly.
 * 
 * @author dward
 */
public class JBossVFSHelper implements PathMatchingHelper
{
    /*
     * (non-Javadoc)
     * @see org.alfresco.config.PathMatchingHelper#canHandle(java.net.URL)
     */
    public boolean canHandle(URL rootURL)
    {
        return rootURL.getProtocol().startsWith("vfs");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.config.PathMatchingHelper#getResources(org.springframework.util.PathMatcher, java.net.URL,
     * java.lang.String)
     */
    public Set<Resource> getResources(PathMatcher matcher, URL rootURL, String subPattern) throws IOException
    {
        VirtualFile root = VFS.getRoot(rootURL);
        PatternVirtualFileVisitor visitor = new PatternVirtualFileVisitor(matcher, root.getPathName(), subPattern);
        root.visit(visitor);
        return visitor.getResources();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.config.PathMatchingHelper#getResource(java.net.URL)
     */
    public Resource getResource(URL url) throws IOException
    {
        return new VFSResource(VFS.getRoot(url));
    }

    /**
     * A helper class that visits all files and directories under a root file or directory and checks for a match.
     */
    @SuppressWarnings("unchecked")
    private class PatternVirtualFileVisitor implements VirtualFileVisitor
    {

        /** The matcher. */
        private final PathMatcher matcher;

        /** The root path name. */
        private final String rootPathName;

        /** The sub pattern. */
        private final String subPattern;

        /** The resources. */
        private final Set<Resource> resources = CollectionFactory.createLinkedSetIfPossible(16);

        /**
         * Instantiates a new pattern virtual file visitor.
         * 
         * @param matcher
         *            the matcher
         * @param rootPathName
         *            the root path name. All paths will be resolved relative to this.
         * @param subPattern
         *            the ant-style pattern to match
         */
        private PatternVirtualFileVisitor(PathMatcher matcher, String rootPathName, String subPattern)
        {
            this.matcher = matcher;
            this.rootPathName = rootPathName.length() == 0 || rootPathName.endsWith("/") ? rootPathName : rootPathName
                    + "/";
            this.subPattern = subPattern;
        }

        /*
         * (non-Javadoc)
         * @see org.jboss.virtual.VirtualFileVisitor#getAttributes()
         */
        public VisitorAttributes getAttributes()
        {
            return VisitorAttributes.RECURSE;
        }

        /*
         * (non-Javadoc)
         * @see org.jboss.virtual.VirtualFileVisitor#visit(org.jboss.virtual.VirtualFile)
         */
        public void visit(VirtualFile vf)
        {
            // Work out path relative to the root
            if (this.matcher.match(this.subPattern, vf.getPathName().substring(this.rootPathName.length())))
            {
                this.resources.add(new VFSResource(vf));
            }
        }

        /**
         * Gets the resources that matched.
         * 
         * @return the resources that matched
         */
        public Set<Resource> getResources()
        {
            return this.resources;
        }
    }

    /**
     * A VFS-specific {@link Resource} implementation.
     */
    public static class VFSResource extends AbstractResource
    {

        /** The wrapped virtual file. */
        private VirtualFile file;

        /**
         * Instantiates a new VFS resource.
         * 
         * @param file
         *            the virtual file to wrap
         */
        public VFSResource(VirtualFile file)
        {
            if (file == null)
            {
                throw new IllegalArgumentException("Null file");
            }
            this.file = file;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.AbstractResource#exists()
         */
        @Override
        public boolean exists()
        {
            try
            {
                return this.file.exists();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.AbstractResource#getURL()
         */
        @Override
        public URL getURL() throws IOException
        {
            try
            {
                return this.file.toURL();
            }
            catch (URISyntaxException e)
            {
                IOException ioe = new IOException(e.getMessage());
                ioe.initCause(e);
                throw ioe;
            }
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.AbstractResource#getFile()
         */
        @Override
        public File getFile() throws IOException
        {
            try
            {
                return new File(VFSUtils.getCompatibleURI(this.file));
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                IOException ioe = new IOException(e.getMessage());
                ioe.initCause(e);
                throw ioe;
            }
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.AbstractResource#createRelative(java.lang.String)
         */
        @Override
        @SuppressWarnings("deprecation")
        public Resource createRelative(String relativePath) throws IOException
        {
            return new VFSResource(VFS.getRoot(new URL(getURL(), relativePath)));
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.AbstractResource#getFilename()
         */
        @Override
        public String getFilename()
        {
            return this.file.getName();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.AbstractResource#getDescription()
         */
        public String getDescription()
        {
            return this.file.toString();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.io.InputStreamSource#getInputStream()
         */
        public InputStream getInputStream() throws IOException
        {
            return this.file.openStream();
        }
    }

}
