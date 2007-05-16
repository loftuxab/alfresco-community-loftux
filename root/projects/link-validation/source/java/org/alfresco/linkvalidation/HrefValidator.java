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
*  You should have received a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    HrefValidator.java
*----------------------------------------------------------------------------*/

package org.alfresco.linkvalidation;

import org.alfresco.config.JNDIConstants;
import org.alfresco.repo.attributes.Attribute;
import org.alfresco.repo.attributes.MapAttribute;
import org.alfresco.repo.attributes.MapAttributeValue;
import org.alfresco.repo.attributes.StringAttribute;
import org.alfresco.repo.attributes.IntAttribute;
import org.alfresco.repo.attributes.IntAttributeValue;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.sandbox.SandboxConstants;
import org.alfresco.service.cmr.attributes.AttrAndQuery;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttrNotQuery;
import org.alfresco.service.cmr.attributes.AttrOrQuery;
import org.alfresco.service.cmr.attributes.AttrQueryEquals;
import org.alfresco.service.cmr.attributes.AttrQueryGT;
import org.alfresco.service.cmr.attributes.AttrQueryGTE;
import org.alfresco.service.cmr.attributes.AttrQueryLike;
import org.alfresco.service.cmr.attributes.AttrQueryLT;
import org.alfresco.service.cmr.attributes.AttrQueryLTE;
import org.alfresco.service.cmr.attributes.AttrQueryNE;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;

import  java.util.Map;

/**

Here's a sketch of the algorithm

<pre>

 Before starting, create an empty url cache for this changeset update.
 This will allow us to skip certain redundant ops like status checks.

 For each file F, calulate a url U.

  [1]   Given a source file, what hrefs appear in it explicitly/implicitly:
            md5_source_to_md5_href[ md5( file ) ]  --> map { md5( url ) }
            .href/mysite/.-2/md5_source_to_md5_href/

  [2]   Given an href, in what source files does it appear 
        explicitly or implicitly (via dead reconing):
            md5_href_to_md5_source[ md5( url ) ]  --> map { md5( file ) }
            .href/mysite/.-2/status_to_md5_href/

  [3]   Given an href, what's its status?
            md5_href_to_status[   md5( url ) ]  --> 200/404, etc.
            .href/mysite/.-2/md5_href_to_status/

  [4]   Given a status, what hrefs have it?
            status_to_md5_href[   status  ]  --> map { md5( url ) }
            .href/mysite/.-2/status_to_md5_href/

  [5]   Given an md5, what's the filename?
            md5_to_file[          md5( file ) ]  --> String( file )
            .href/mysite/.-2/md5_to_file/
        
  [6]   Given an md5, what's the href?
            md5_to_href[          md5( url ) ]  --> String( url )
            .href/mysite/.-2/md5_to_href/

  [7]   Given an href what files does it depend on?
            md5_href_to_md5_fdep[ md5( url ) ]  --> map { md5( file ) } 
            .href/mysite/.-2/md5_href_to_md5_fdep/

  [8]   Given a file, what hrefs depend on it
            md5_fdep_to_md5_href[ md5( file ) ]  --> map { md5( url ) }     
            .href/mysite/.-2/md5_fdep_to_md5_href/

  On file creation Fj
  --------------------
  [c0]  Calculate href Ui for Fj, and enter md5(Ui) into ephemeral url cache
  [c1]  Pull on Ui, get file set accessed F 
        This is usually just Fj but could be Fj,...Fk).
            If F = {} or does not include Fj either we failed to compute 
            Ui from Fj or an error has occured when we pulled on Ui.  
            skip all remaining work on Fj & track the error (possibly
            report to gui or just log).  This is a "soft" failure because
            the link's existence is implied by the file's existence 
            (we didn't *actually* extract it from a returned html page).

  [c2]  Update [1] with md5(Fj) -> md5(Ui)  (handles implicit link)
  [c3]  Update [2] with md5(Ui) -> md5(Fj)  (handles implicit link)
  [c4]  Update [3] with md5(Ui) -> status
  [c5]  Update [4] with status  -> md5(Ui)
  [c6]  Update [5] with md5(Fj) -> Fj,       md5(Fk) -> Fk,  ...
  [c7]  Update [6] with md5(Ui) -> Ui
  [c8]  Update [7] with md5(Ui) -> md5(Fj),  md5(Ui) -> md5(Fk), ...
  [c9]  Update [8] with md5(Fj) -> md5(Ui),  md5(Fk) -> md5(Ui), ... 

        For every url Ux in the page (but not the dead-reconed one Ui),
        If Ux is already in the ephemeral url cache,
  [c10]     Next Ux
        Else
  [c11]     Pull on Ux and regardless of what happens:
  [c12]     Update [1] with md5(Fj) -> md5(Ux)
  [c13]     Update [2] with md5(Ux) -> md5(Fj)
  [c14]     Update [3] with md5(Ux) -> status
  [c15]     Update [4] with status  -> md5(Ux)
  [c16] 
        If status==success, determine which files are accessed Fx,Fy, ...
  [c17]     Update [5] with md5(Fx) -> Fx,  md5(Fy) -> Fy, ...
  [c18]     Update [6] with md5(Ux) -> Ux
  [c19]     Update [7] with md5(Ux) -> Fx,  md5(Ux) -> Fy, ...
  [c20]     Update [8] with md5(Fx) -> md5(Ux), md5(Fy) -> md5(Ux), ...


  On file modification
  --------------------

  [m0] Calculate href Ui for Fj, if already in ephermal cache, do next Fj
  [m1] == [c1]
  [m2] == [c2] but it's a no-op
  [m3] == [c3] but it's a no-op
  [m4] == [c4]
  [m5] == [c5]
  [m6] == [c6] but it's a no-op for md5(Fj)
  [m7] == [c7] but it's a no-op
  [m8] == [c8] but it's a no-op for md5(Ui) -> md5(Fj)
  [m9] == [c9] but it's a no-op for md5(Fj) -> md5(Ui)

  [m10]  Parse & get list of hrefs curently in page, plus link to self: Ucurr
  [m11]  Using [1], get previous href list:    Uprev
  [m12]  Subtracing lists, find urls now gone: Ugone
         Note:  implicit link to self never 
                appears in Ugone on modifiction
                becuse [m10] includes implicit 
                link to self.  
        
         For each unique URL Ux in Ugone (no cache check):
  [m13]         Update [1], removing  md5(Fj) -> md5(Ux)
  [m14]         Update [2], removing  md5(Ux) -> md5(Fj)
  [m15]         If [2] shows that href no longer appears anywhere, then:
  [m16]              Remove from [2]  md5(Ux)
  [m17]              Remove from [6]  md5(Ux) -> Ux
  [m18]              Using [7], 
                     for each file md5(Fx) depending on the defunct md5(Ux):
                          Remove from [8]  md5(Fx) -> md5(Ux)
  [m19]              Remove from [7] md5(Ux)
  [m20]              Using [3], fetch status of Ux. If there's a status 
                     then:
  [m21]                      Remove from [3]  md5(Ux) -> status
  [m22]                      Remove from [4]  status  -> md5(Ux)

         For each unique URL Ux in Ucurr
         If Ux isn't already in the ephermal url cache, do:
  [m24..m34] ==  [c10..20]



  On proposed file deletion
  -------------------------
   [p0]  Use [8] to get raw list of URLs that depend on Fj.

         For each URL Ux in [8]:
   [p1]     Use [2] to get list of files with newly broken links
            but omit from this list the Fj itself
            (because it's going away).


  On file deletion Fj
  --------------------
  [d0]  Use [1] to get list of hrefs that appear in Fj explicitly 
        and implicitly.  Call this set Ugone.  

        For each URL Ux in Ugone
  [d1-d9] == [m14..m22] 

  [d10-d11] == [p0-p1]

  [d12]  For each broken link discovered in [d10-d11], update [3] and [4] 
         with 404 status (assumed)... or perhaps some other 4xx status
         to denote "presumed broken" if you don't really test it.

  [d13]  Update [1] by removing md5( Fj )   (remove all urls in one shot)

  [d14]  Using [8], if nothing depends on md5(Fj), 
         then remove md5(Fj) from [5] and [8].

</pre>
*/
public class HrefValidator
{
    static String HREF_KEY                 = ".href";    // top level href key

    static String LATEST_VERSION_KEY       = ".latest";  // numerical version
    static String LATEST_VERSION_ALIAS_KEY = ".-2";      // alias for numerical

    static String SOURCE_TO_HREF_KEY       = "source_to_href";
    static String HREF_TO_SOURCE_KEY       = "href_to_source";

    static String HREF_TO_STATUS_KEY       = "href_to_status";
    static String STATUS_TO_HREF_KEY       = "status_to_href";

    static String MD5_TO_FILE_KEY          = "md5_to_file";
    static String MD5_TO_HREF_KEY          = "md5_to_href";

    static String HREF_TO_FDEP_KEY         = "href_to_fdep";
    static String FDEP_TO_HREF_KEY         = "fdep_to_href";


    AVMRemote        avm_;
    AttributeService attr_;

    public HrefValidator( AVMRemote avmRemote, AttributeService attributeService)
    {
        avm_  = avmRemote;
        attr_ = attributeService;
    }

    // Example    mysite:/www/avm_webapps/ROOT

    /**
    * Attemtps to revalidate the status of all hrefs in all webapps
    * in the named store.  
    *
    * @return true iff all webapps were sucessfully revalidated
    */
    //-------------------------------------------------------------------------
    public boolean revalidateAllWebappsInStore( String store_name )
    {
        String app_base = store_name + ":/" + 
                          JNDIConstants.DIR_DEFAULT_WWW + "/" +
                          JNDIConstants.DIR_DEFAULT_APPBASE;

        Map<String, AVMNodeDescriptor> webapp_entries = null;
        try
        {
            // e.g.:   -1, "mysite:/www/avm_webapps"
            webapp_entries = avm_.getDirectoryListing(-1, app_base );
        }
        catch (Exception e)     // TODO: just AVMNotFoundException ?
        {
            return false;
        }

        boolean result = true;
        for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  :
                      webapp_entries.entrySet()
                    )
        {
            String webapp_name = webapp_entry.getKey();    //  my_webapp

            if ( webapp_name.equalsIgnoreCase("META-INF")  ||
                 webapp_name.equalsIgnoreCase("WEB-INF")
               )
            {
                continue;
            }

            result = revalidateWebapp( store_name, app_base + "/" + webapp_name )
                     && result;
        }
        return result;
    }

    String lookupStoreDNS( String store )
    {
        Map<QName, PropertyValue> props = 
                avm_.queryStorePropertyKey(store, 
                     QName.createQName(null, SandboxConstants.PROP_DNS + '%'));

        return ( props.size() != 1 
                 ? null
                 : props.keySet().iterator().next().getLocalName().
                         substring(SandboxConstants.PROP_DNS.length())
               );
    }

    /**
    *  Creates keys corresponding to the store being valiated,
    *  and returns the final key path.   If the leaf key already
    *  exists and 'clobber_leaf' is false, then the pre-existing
    *  leaf key will be reused;  otherwise,  this function creates
    *  a new leaf (potentially clobbering the pre-existing one).
    */
    String createAttributeStemForStore( String  store, 
                                        boolean clobber_leaf )
    {
        // Given a store name X has a dns name   a.b.c
        // The attribute key pattern used is:   .href/c/b/a
        // 
        // This guarantees if a segment contains a ".", it's not a part 
        // of the store's fqdn.  Thus, "." can be used to delimit the end 
        // of the store, and the begnining of the version-specific info.
        // 

        String  dns_name = lookupStoreDNS( store );
        if ( dns_name == null ) { return null; }


        // Construct path & create coresponding attrib entries
        StringBuilder str  = new StringBuilder( dns_name.length() );
        str.append( HREF_KEY );

        // Create top level .href key if necessary
        if ( attr_.getAttribute( HREF_KEY ) == null )
        {
            MapAttribute map = new MapAttributeValue();
            attr_.setAttribute("", HREF_KEY, map );
        }

        String [] seg = dns_name.split("\\.");
        String pth;
        for (int i= seg.length -1 ; i>=0; i--) 
        { 
            pth = str.toString();
            if ( ((i==0) && clobber_leaf == true ) ||
                 attr_.getAttribute( pth + "/" + seg[i] ) == null 
               )
            {
                MapAttribute map = new MapAttributeValue();
                attr_.setAttribute( pth , seg[i], map );
            }
            str.append("/" + seg[i] ); 
        }
        return str.toString();
    }

    public boolean revalidateWebapp(String store, String webapp_path)
    {
        // Example:           ".href/mysite"
        String attr_stem    = createAttributeStemForStore( store, true );
        if ( attr_stem == null ) { return false; }

        // .href/mysite/.latest/42
        attr_.setAttribute( attr_stem , 
                            LATEST_VERSION_KEY, 
                            new IntAttributeValue( avm_.getLatestSnapshotID( store ) )
                          );
                             

        // In the AVM, -1 referrs to the latest read/write version, but 
        // there's no convention for referring to the last snapshot.  
        // This is because it's a relative and changing value that is
        // largely unnecessary for AVM ops.  However, here it's nice
        // to create an alias so later lookups don't have to hunt for 
        // a specific numeric version.  Therefore, the following 
        // convention is used:  version <==> (version - max - 2) %(max+2)
        //
        // The only case that ever matters for now is that:
        // -2 is an alias for the last snapshot
        //
        // Thus href attribute info for the  "last snapshot" of 
        // a store with the dns name:  preview.alice.mysite is
        // stored within attribute service under the keys: 
        //
        //      .href/mysite/alice/preview/.-2
        //     
        // This allows entire subtrees to be eliminated easily:
        //
        //      .href/mysite/<removed>
        //
        // Note that because no DNS segment contains "."
        // it's always easy to tell them apart from version
        // info (which contains a leading "."  So without 
        // reserving any more chars the following can coexist:
        //
        //      .href/mysite/.-2/<...link info...>
        //      .href/mysite/alice/.-2/<...link info...>
        
        MapAttribute map = new MapAttributeValue();
        attr_.setAttribute( attr_stem ,  LATEST_VERSION_ALIAS_KEY, map );
        String href_attr =  attr_stem +  "/" + LATEST_VERSION_ALIAS_KEY;


        //      .href/mysite/.-2/
        //                      source_to_href
        //                      href_to_source
        //                      href_to_status
        //                      status_to_href
        //                      md5_to_file
        //                      md5_to_href
        //                      href_to_fdep
        //                      fdep_to_href

        
        return true;
    }


}
