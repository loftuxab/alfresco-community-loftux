/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have recieved a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMBulkLoader.java
*  
*
*  TODO:
*        Provide metadata for files/dirs that you want present
*        in a "deleted state".
*
*        Deal with setNodeProperties(String path, 
*                                    Map<QName, PropertyValue> properties)
*
*        Verify: Are the node properties working like overlay/opaque children?
*
*        How should I accumulate repo properties?
*----------------------------------------------------------------------------*/

package org.alfresco.jndi;

import java.io.*;
import java.util.HashMap;
import java.util.SortedMap;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.avm.AVMException;
import org.alfresco.service.cmr.avm.AVMExistsException;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avm.AVMNotFoundException;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.namespace.QName;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.Node;


/**
* The AVMBulkLoader provides a portable way to initialize 
* an AVM repository with data from an OS-native file system.
*
* <p>
*
* Collections of directories containing 
* <code><strong>.alfresco_metadata</strong></code> files
* (see <a href='http://en.wikipedia.org/wiki/Document_Type_Definition'>
* DTD</a> below)    
* can represent arbitrary AVM layering structures.  For example, 
* suppose you have a directory named <tt>test-data</tt>
* and you'd like to do the following:
*
*   <ul>
*     <li> The files contained in <tt>test-data/repo-1</tt>
*          should be loaded into AVM <tt>repo1:/</tt>
*     <li> The files contained in <tt>test-data/repo-2</tt>
*          should be loaded into AVM <tt>repo2:/</tt>
*     <li> The directory <tt>test-data/repo-2/bob</tt>
*          should be a transparent overlay on AVM <tt>repo-1:/repo-1/alice</tt>
*     <li> After everything has been loaded into the AVM, take a version "snapshot".
*   </ul>
*
* The native file system directory structure would 
* look something like this:
* <pre>
*
*       test-data/  
*                |
*                +-- repo-1/
*                |         |
*                |         |-- .alfresco_metadata
*                |         |
*                |         +-- alice/
*                |                  |
*                |                  ...
*                |
*                +-- repo-2/
*                          |
*                          |-- .alfresco_metadata
*                          |
*                          +-- bob/
*                                 |
*                                 |-- .alfresco_metadata
*
* </pre>
* To create the proper layering, here's what each 
* <code><strong>.alfresco_metadata</strong></code>
* file would contain:
* <dl>
*      <dt><code><strong>test-data/repo-1/.alfresco_metadata</strong></code>
*      <dd>&lt;alfresco_metadata  repo_parent_path="repo-1:/" /&gt;<p>
*
*      <dt><code><strong>test-data/repo-2/.alfresco_metadata</strong></code>
*      <dd>&lt;alfresco_metadata  repo_parent_path="repo-2:/" /&gt;<p>
*
*      <dt><code><strong>test-data/repo-2/bob/.alfresco_metadata</strong></code>
*      <dd>&lt;alfresco_metadata  target="repo-1:/repo-1/alice" /&gt;
* </dl>
* <p>
* 
*  The code that loads this structure into the AVM would look like this:
* <pre>
*
*        AVMService    service;
*        // ...
*        // ... initialize AVMService object via Spring, etc.
*        // ...
*
*        AVMBulkLoader bulk        = new AVMBulkLoader(service); 
*        File          import_base = new File("test-data");
*
*        for ( String child : import_base.list() )
*        {
*            File import_dir = new File(import_base, child);
*            if ( ! import_dir.isDirectory()) { continue; }
*
*            import_dir = import_dir.getAbsolutePath();
*            bulk.importAVMdataFromDirectory( import_dir );
*        }
*        bulk.snapshot();
*
* </pre>
*
*
* File systems 
* <a href='http://en.wikipedia.org/wiki/File_system'>differ</a>  
* in their ability to represent custom metadata, version histories,
* and approximate 
* <a href='http://wiki.alfresco.com/wiki/Transparent_Layers'>
* transparent layers</a>.  The AVMBulkLoader 
* achieves portability by fetching this kind of information 
* from optional, per-directory 
* <code><strong>.alfresco_metadata</strong></code> files
* described by the following DTD:
*
* <pre>
*
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!--                  DTD for .alfresco_metadata files.                  --&gt;
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*
*
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!-- ELEMENT alfresco_metadata                                           --&gt;
*  &lt;!--        Top-level node describing the metadata associated with       --&gt;
*  &lt;!--        with the directory in which this .alfresco_metadata          --&gt;
*  &lt;!--        file resides.  Also contains meta information regarding      --&gt;
*  &lt;!--        the files within this directory.                             --&gt;
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!ELEMENT alfresco_metadata  (file)*&gt;
*
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!-- ATTLIST alfresco_metadata                                           --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  deleted                                                            --&gt;
*  &lt;!--        If true, the directory in which this .alfresco_metadata      --&gt;
*  &lt;!--        file resides is in a "deleted" state.  None of its           --&gt;
*  &lt;!--        contents will be imported the repository, and if a           --&gt;
*  &lt;!--        corresponding directory exists within the repository,        --&gt;
*  &lt;!--        it will be removed.                                          --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  hostname                                                           --&gt;
*  &lt;!--        Specifies the dns hostname that the AVMUrlValve should       --&gt;
*  &lt;!--        associate with this repository.  This attribute is ignored   --&gt;
*  &lt;!--        on all but top-level .alfresco_metadata files.               --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  opaque                                                             --&gt;
*  &lt;!--        When true, if the directory into which data is being loaded  --&gt;
*  &lt;!--        is within a transparent layer, the opacity flag is set,      --&gt;
*  &lt;!--        and any content from background layers becomes invisible.    --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  replacement                                                        --&gt;
*  &lt;!--        If true, prior to uploading data from the directory in       --&gt;
*  &lt;!--        which this .alfresco_metadata file resides, the contents     --&gt;
*  &lt;!--        of the corresponding directory in the repository are first   --&gt;
*  &lt;!--        deleted.  Thus, the file system's directory "replaces"       --&gt;
*  &lt;!--        the respository's directory.                                 --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  repo_parent_path                                                   --&gt;
*  &lt;!--        Allows the location that file system data will be loaded     --&gt;
*  &lt;!--        into the repository to be specified by parent path.          --&gt;
*  &lt;!--        The leaf directory name in the repository will be taken      --&gt;
*  &lt;!--        from the directory name in the file system being loaded.     --&gt;
*  &lt;!--        The attributes 'repo_parent_path' and 'repo_path' are        --&gt;
*  &lt;!--        mutually exclusive.   This attribute is ignored on all       --&gt;
*  &lt;!--        but top-level .alfresco_metadata files (relative             --&gt;
*  &lt;!--        placement of the file system tree in the repository is       --&gt;
*  &lt;!--        enforced on children, recursively).                          --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  repo_path                                                          --&gt;
*  &lt;!--        Allows fileystem data to be loaded into an absolute          --&gt;
*  &lt;!--        location within the repository.   The attributes             --&gt;
*  &lt;!--        'repo_parent_path' and 'repo_path' are mutually              --&gt;
*  &lt;!--        exclusive.  This attribute is ignored on all but             --&gt;
*  &lt;!--        top-level .alfresco_metadata files (relative placement       --&gt;
*  &lt;!--        of the file system tree in the repository is enforced        --&gt;
*  &lt;!--        on children recursively).                                    --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  repo_vers                                                          --&gt;
*  &lt;!--        If the data from this directory came from the repository,    --&gt;
*  &lt;!--        the 'repo_vers' indicates the repository version number.     --&gt;
*  &lt;!--        The value "-1" means "the latest/unversioned content".       --&gt;
*  &lt;!--        When importing content via AVMBulkLoader, it is always       --&gt;
*  &lt;!--        uploaded as "the latest/unversioned content" because         --&gt;
*  &lt;!--        non-negative versions are immutable.                         --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  snapshot                                                           --&gt;
*  &lt;!--        Schedules the repository being written to for versioning     --&gt;
*  &lt;!--        the next time snapshot() is called within the bulk loader.   --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  target                                                             --&gt;
*  &lt;!--        If this attribute is missing, the current directory is       --&gt;
*  &lt;!--        is imported as a plain directory within the AVM.             --&gt;
*  &lt;!--        Otherwise, the current directory is imported as a            --&gt;
*  &lt;!--        transparent layer, using the value of 'target' as the        --&gt;
*  &lt;!--        AVM path to overlay.                                         --&gt;
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!ATTLIST alfresco_metadata
*                          deleted               (true|false)   "false"
*                          hostname              CDATA          #IMPLIED
*                          opaque                (true|false)   "false"
*                          replacement           (true|false)   "false"
*                          repo_parent_path      CDATA          #IMPLIED
*                          repo_path             CDATA          #IMPLIED
*                          repo_vers             CDATA          "-1"
*                          snapshot              (true|false)   "true"
*                          target                CDATA          #IMPLIED&gt;
*
*
*
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!-- ELEMENT file                                                        --&gt;
*  &lt;!--        Associates metadata and/or a target with a file contained    --&gt;
*  &lt;!--        the directory in which this .alfresco_metadata file resides. --&gt;
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!ELEMENT file EMPTY&gt;   &lt;!-- non-EMPTY once aspects are incorporated --&gt;
*
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!-- ATTLIST file                                                        --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  name                                                               --&gt;
*  &lt;!--        The name of a file contained within this directory.          --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  deleted                                                            --&gt;
*  &lt;!--        When true, this file is in a "deleted" state.                --&gt;
*  &lt;!--        If a corresponding file exists within the repository,        --&gt;
*  &lt;!--        it will be removed.                                          --&gt;
*  &lt;!--                                                                     --&gt;
*  &lt;!--  target                                                             --&gt;
*  &lt;!--        Indicates the target of a transparent file.                  --&gt;
*  &lt;!--        If 'target' is empty or unused, this is a plain file.        --&gt;
*  &lt;!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --&gt;
*  &lt;!ATTLIST file          name                 CDATA           #REQUIRED
*                          deleted              (true|false)    "false"
*                          target               CDATA           #IMPLIED&gt;
*
* </pre>
*
*  Note that <code><strong>.alfresco_metadata</strong></code> files themselves
*  are not imported into the AVM repository.  If you <em>really</em> want
*  to import a file named <code><strong>.alfresco_metadata</strong></code>
*  into the repository, its file system representation should be named
*  <code><strong>.alfresco_metadata.alfresco_metadata</strong></code>
*  (the AVMBulkLoader imports
*  <code><strong>.alfresco_metadata<i>XXX</i></code></strong> as
*  <code><strong><i>XXX</i></code></strong> to enable this).
*/

public class AVMBulkLoader
{
    /**
    *  The name of the per-directory metadata file 
    *  (<i>i.e.</i>: ".alfresco_metadata").
    */
    public static final String ALFRESCO_METADATA = ".alfresco_metadata";

    /** @exclude */
    public static final int    BUFFER_SIZE  = 2048;

    /** @exclude */
    public static final String FILE_ELEMENT = "file";

    /** @exclude */
    protected AVMService service_;

    /** 
    * @exclude 
    *      All repos modified during import that
    *      require a 'snapshot' later.
    */
    protected HashMap<String, Object> repo_to_snapshot_ = 
                                          new HashMap<String, Object>();

    public AVMBulkLoader( AVMService service ) { service_ = service; }


    /**
    *  Loads a directory into the AVM repository.
    *  The top-level directory being loaded must contain
    *  an <code><strong>.alfresco_metadata</strong></code> file, 
    *  but subdirectories don't unless they need to specify metadata or
    *  transparency information for that directory
    *  or its files.
    */
    public void 
    importAVMdataFromDirectory(File directory) throws Exception
    {
        if ( ! directory.isDirectory() )
        {
            throw new Exception("Not a directory: " + directory.getName());
        }

        // Silently ignore Subversion .svn directories
        if ( directory.getName().equals(".svn") ) { return; }

        File    metafile = new File(directory, ALFRESCO_METADATA);

        if ( ! metafile.exists()) 
        { 
            throw new Exception(
                   "Import directory '" + metafile.getAbsolutePath() + 
                   "' needs '" + ALFRESCO_METADATA + "' file");
        }

        Element meta_elem = getMetadata( metafile );

        String  repo_name;
        String  leaf_name;
        String  repo_parent_path = 
                    meta_elem.attributeValue("repo_parent_path");


        if ( repo_parent_path != null ) { leaf_name = directory.getName(); }
        else
        {
            String maybe_trailing_slash = "";
            String repo_path = meta_elem.attributeValue("repo_path");

            if ( repo_path.charAt( repo_path.length() -1) == '/' )
            {   
                repo_path = repo_path.substring(0, repo_path.length() -1);
                maybe_trailing_slash = "/";
            }

            // If necessary, create dest AVM dir
            //
            int    slash = repo_path.lastIndexOf('/');
            if (slash < 0) 
            { 
                throw new Exception(
                      "Bad repo_path (format: <reponame>:/<nonemptypath>) : " +
                      repo_path + maybe_trailing_slash);
            }

            repo_parent_path = repo_path.substring(0,slash);
            leaf_name        = repo_path.substring(slash+1, repo_path.length());

            if (leaf_name.equals("")) 
            { 
                throw new Exception(
                      "Bad repo_path (format: <reponame>:/<nonemptypath>) : " +
                      repo_path + maybe_trailing_slash);
            }
        }

        repo_name = makeRepoBasePath( repo_parent_path, meta_elem );

        // Set meta information on the repository for AVMHostConfig

        String  hostname = meta_elem.attributeValue("hostname");
        if ( hostname == null )
        {
                throw new Exception(
                      "Top-level '" + ALFRESCO_METADATA + "' file " +
                      "must set 'hostname' attribute");
        }

        String  docroot = meta_elem.attributeValue("docroot");
        if ( docroot == null )
        {
                throw new Exception(
                      "Top-level '" + ALFRESCO_METADATA + "' file " +
                      "must set 'docroot' attribute");
        }

        String dnsname_key =  ".dns." + hostname;
        String dnsname_val =  docroot;

        try 
        { 
            service_.setStoreProperty( 
                repo_name,
                QName.createQName(null, dnsname_key ),
                new PropertyValue(null, dnsname_val )
            );
        }
        catch (AVMException e) 
        { 
            throw new Exception(
                "Could not setStoreProperty: " + 
                        dnsname_key + " = "    +
                        dnsname_val + " on repo_name", e);
        }
        

        importDirectory( directory,
                         repo_parent_path, 
                         leaf_name,
                         repo_name,
                         meta_elem
                       );
    }

    /** @exclude */ 
    protected void 
    importDirectory( File    dir,
                     String  repo_parent_path,
                     String  leaf_name,
                     String  repo_name,
                     Element meta
                   ) throws  DocumentException , IOException
    {
        HashMap<String, Element> file_meta = null;
        String                   repo_path = repo_parent_path + "/" + leaf_name;

        String deleted     = null;
        String opaque      = null;
        String replacement = null;
        String snapshot    = null;
        String target      = null;

        if ( meta != null) 
        { 
            file_meta   = getFileMetadataMap( meta );

            deleted     = meta.attributeValue("deleted");
            opaque      = meta.attributeValue("opaque");
            replacement = meta.attributeValue("replacement");
            snapshot    = meta.attributeValue("snapshot"); 
            target      = meta.attributeValue("target");
        }

        boolean deleted_mode = ((deleted != null) &&
                                 deleted.equals("true")
                               ) ? true : false; 

        if  ( deleted_mode )
        {
            try { service_.removeNode( repo_parent_path, leaf_name); }
            catch (AVMNotFoundException e2) { /* benign race */ }
            return;
        }


        boolean opaque_mode  = ((opaque != null) && opaque.equals("true")
                               ) ? true : false; 


        boolean replace_mode = ((replacement != null) &&
                                 replacement.equals("true")
                                 ) ? true : false; 


        if ((snapshot == null) || snapshot.equals("true") )
        {
            repo_to_snapshot_.put( repo_name, null);  // only the key matters
        }


        if ( target == null )   // plain or implicit layer
        {
            try { service_.createDirectory(repo_parent_path, leaf_name); }
            catch (AVMExistsException e)  
            { 
                if ( replace_mode ) 
                {
                    try { service_.removeNode( repo_parent_path, leaf_name); }
                    catch (AVMNotFoundException e2) { /* benign race */ }

                    try { service_.createDirectory(repo_parent_path,leaf_name);}
                    catch (AVMExistsException e2)   { /* benign race */ }
                }
            }
        }
        else            // This is an explicit layered node
        {
            try 
            { 
                service_.createLayeredDirectory(
                        target,repo_parent_path,leaf_name); 
            }
            catch (AVMExistsException e) 
            { 
                try { service_.removeNode( repo_parent_path, leaf_name ); }
                catch (AVMNotFoundException e2) { /* benign race */ }

                try
                {
                    service_.createLayeredDirectory(
                        target, repo_parent_path, leaf_name);
                }
                catch (AVMExistsException e2) { /* benign race */ }
            }

            if ( replace_mode )
            {
                // TODO:  what's the best thing to do if this call fails?
                //
                SortedMap<String, AVMNodeDescriptor> cwd_repo_listing =
                    service_.getDirectoryListing( -1, repo_path);

                for (String child : cwd_repo_listing.keySet() )
                {
                    try { service_.removeNode(repo_path, child); }
                    catch (AVMNotFoundException e) { /* benign race */ }
                }
            }
        }

        // Even if there was no explicit target, it's still possible
        // that this was an non-plain (implicit transparent) node.
        // Deal with opacity here.

        if  ( opaque_mode )
        {
            try { service_.setOpacity( repo_path, true ); }
            catch (AVMException e) { /* benign race or "opaque" plain dir */ }
        }


        for ( String  child_name : dir.list() )
        {
            File child_entry = new File( dir, child_name );

            if ( child_entry.isFile() )
            {
                // throw IOException if file can't be read, etc.

                importFile( child_entry,   // child file
                            repo_path,     // this parent: repo-1:/repo-1/x
                            child_name,    // contained file leaf name
                            repo_name,     // repo-1

                            // Metadata element for this file

                            ((  file_meta != null) 
                              ? file_meta.get( child_name )
                              : null
                            ));
            }
            else if ( child_entry.isDirectory() )
            {
                // Silently ignore Subversion .svn directories
                if ( child_name.equals(".svn") ) { continue; }

                File    metafile   = new File(child_entry, ALFRESCO_METADATA);
                Element child_meta = null;
                if (metafile.exists()) 
                {
                    child_meta = getMetadata( metafile );
                }


                importDirectory( child_entry,  // child dir
                                 repo_path,    // this parent:  repo-1:/repo-1/x
                                 child_name,   // sub directory leaf name
                                 repo_name,    // repo-1
                                 child_meta
                               );
            }
        }
    }

    void importFile( File    file,
                     String  repo_parent_path,
                     String  leaf_name,
                     String  repo_name,
                     Element meta) throws IOException
    {
        if ( leaf_name.equals( ALFRESCO_METADATA )) { return; } 

        //  Transform:  .alfresco_metadataXXX 
        //         to:  XXX
        //
        //  Thus, if you really want to insert a .alfresco_metadata file 
        //  into the AVM repository, you can encode it as 
        //
        //            .alfresco_metadata.alfresco_metadata

        if ( leaf_name.startsWith( ALFRESCO_METADATA ) ) 
        { 
            leaf_name = leaf_name.substring( ALFRESCO_METADATA.length() );
        }

        String target  =null;
        String deleted = null;

        if ( meta != null) 
        { 
            deleted = meta.attributeValue("deleted");
            target  = meta.attributeValue("target"); 
            target  = makeTargetAbsolute( target, repo_name, repo_parent_path );
        }

        boolean deleted_mode = ((deleted != null) &&
                                 deleted.equals("true")
                               ) ? true : false; 


        if  ( deleted_mode )
        {
            try { service_.removeNode( repo_parent_path, leaf_name); }
            catch (AVMNotFoundException e) { /* benign race */ }
            return;
        }


        if ( target != null )           //  create layered file
        {
            try 
            {
                service_.createLayeredFile( target,
                                            repo_parent_path , 
                                            leaf_name
                                          );
            }
            catch (AVMExistsException e)
            {
                try { service_.removeNode( repo_parent_path, leaf_name ); }
                catch (AVMNotFoundException e2) { /* benign race */ }

                try 
                {
                    service_.createLayeredFile( target,
                                                repo_parent_path , 
                                                leaf_name
                                              );
                }
                catch (AVMExistsException e2) { /* benign race */ }
            }
        }
        else
        {
            InputStream  istream = null; 
            OutputStream ostream = null; 
            byte buffer[] = new byte[BUFFER_SIZE];
            int len = -1 ;

            try 
            {
                istream =  new FileInputStream( file.getAbsolutePath() ); 

                try {ostream =  service_.createFile( repo_parent_path , leaf_name);}
                catch (AVMExistsException e)
                {
                    ostream = service_.getFileOutputStream( 
                                          repo_parent_path + "/" + leaf_name );
                }

                while( true )
                {
                    len = istream.read( buffer );
                    if ( len == -1 ) { break ; }
                    ostream.write( buffer, 0, len );
                }
            }
            finally
            {
                if ( ostream != null ) { ostream.close() ; } 
                if ( istream != null ) { istream.close() ; } 
            }
        }
    }



    /**
    * @exclude
    *
    *  Creates a basepath (and possibly a repository) within AVM.
    *  @return the repository name portion of the basepath
    */
    public String 
    makeRepoBasePath(String  basepath, 
                     Element meta) throws AVMException, Exception
    {
        int colon_index = basepath.indexOf(':');

        if (colon_index < 0)
        {
            throw new Exception("Bad repo basepath (no colon): " + basepath);
        }

        String repo_name = basepath.substring(0,colon_index);

        try { service_.createStore( repo_name ); }
        catch (AVMExistsException e )  { /* ok */ }

        if (service_.lookup(-1, basepath) == null) // start off optimistic
        {
            makePath( basepath ); 
        }

        return repo_name;
    }

    /**
    * @exclude 
    *
    *  Creates a path within existing AVM repo (similar to "mkdir -p").
    */
    protected void 
    makePath(String repo_path) throws AVMException
    {
        int     head   = repo_path.indexOf('/',0) + 1;
        int     offset = 0;
        int     tail;

        while( head <= repo_path.length() )
        {   
            tail = repo_path.indexOf('/',head +1);
            if ( tail < 0 )   { tail = repo_path.length(); }
            if (tail == head) { break ;}

            try 
            {
                service_.createDirectory( 
                        repo_path.substring(0, head - offset),
                        repo_path.substring(head, tail) );
            }
            catch (AVMExistsException e) { /* benign race */ }

            head   = tail +1;  // next segment
            offset = 1;        // skip trailing slash on all dirs but reponame:/
        }
    }


    /**
    *  Creates a shapshot of all directories loaded 
    *  since the last snapshot() that require versioning.
    *  By default, a directory requires versioning when loaded.
    *  You can change this default by setting the &lt;alfresco_metadata&gt;
    *  attribute 'shapshot' to 'false' in the 
    *  <code><strong>.alfresco_metadata</strong></code>
    *  file of the directory being loaded.  For example:
    *  <pre>
    *        &lt;alfresco_metadata  snapshot='false' ... &gt;
    *  </pre>
    *
    *  Note that you can call 
    *  {@link #importAVMdataFromDirectory importAVMdataFromDirectory}
    *  multiple times prior to invoking snapshot().
    *  <p>
    *  After this function completes, the pending list of 
    *  repositories to snapshot is cleared.
    */
    public void snapshot()
    {
        for (String modified_repo : repo_to_snapshot_.keySet() )
        {
             // TODO Put some nice autogenerated data into the comment fields.
             try { service_.createSnapshot( modified_repo, null, null); }
             catch (Exception e) { /* TODO ? */ }
        }
        repo_to_snapshot_.clear();
    }

    /**
    * @exclude
    *
    *  Parses an .alfresco_metadata file.
    *
    *  @return  The root node of the parsed .alfresco_metadata file. 
    */
    protected Element 
    getMetadata(File metadata) throws DocumentException
    {
        Element root;
        SAXReader reader   = new SAXReader();
        Document  document = reader.read( metadata ); 

        return document.getRootElement();
    }


    /** @exclude */
    protected HashMap<String, Element> 
    getFileMetadataMap( Element element )
    {
        HashMap<String, Element> metadata = new HashMap<String, Element>();

        for (int i=0, size = element.nodeCount() ; i< size; i++)
        {
            Node node = element.node(i);
            if ( node.getNodeType() != Node.ELEMENT_NODE ) { continue; }

            Element elem       = (Element) node;
            if ( ! elem.getName().equals( FILE_ELEMENT ) ) { continue; }

            String  child_name = elem.attributeValue("name");
            metadata.put( child_name, elem );
        }
        return metadata;
    }

    /** @exclude */
    protected String 
    makeTargetAbsolute(String target, 
                       String repo_name, 
                       String repo_parent_path)
    {
        if ( target == null)     { return null; }
        if ( target.equals("") ) { return null; }

        int colon_index = target.indexOf(':');

        if (colon_index > 0) { return target; }  // already repo-absolute

        if ( target.charAt(0) == '/')            // absolute within current repo
        {
            return repo_name + ":" + target;     // default to current repo
        }
        
        // default to current repo path
        return repo_parent_path + "/" + target;
    }
}
