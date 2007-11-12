#------------------------------------------------------------------------------
# Copyright 2005-2007 Alfresco Inc.
# 
# File    README.txt
#------------------------------------------------------------------------------

 DESCRIPTION

        Mingw is a cross compiler that creates executables for Win/i386.
        It isn't built (or installed in 3rd-party) by default because:

           [1]  Mingw takes 15-20 minutes to compile
           [2]  Changes to mingw will be infrequent
           [3]  Mingw takes up about 80M when compiled

        In the <branch>/root directory, if you type 'ant mingw',
        all the necessary work is done to built and install mingw
        into:  <branch>/root/projects/3rd-party/bin/linux/i386/mingw

        To build the C-based Alfresco CLTs (via 'ant incremental-clt'),
        you must build this project;  currently, you can only build
        'mingw' and 'clt' from linux/i386.
         
        The Mingw tool for creating a cross compiler used by 'ant mingw' was 
        obtained from:
        http://sourceforge.net/project/showfiles.php?group_id=2435
              &package_id=12644&release_id=17892

        While not used directly, it's also worth having a look at:
        http://www.profv.de/mingw_cross_env/
 
        In the end, neither the sourceforge nor the profv.de 
        solutions were entirely satisfactory as-is, but there 
        were some goood ideas in both.


  RATIONALE
        It's convenient to have mingw & the clt project in the same branch 
        as everything else in Alfresco;  however, it's undesirable to bloat 
        the source tree with 80M of infrequently changing infrastructure that 
        most people won't ever need.  Thus, the 'mingw' built target is only
        intended to be run by hand on developer machines that must build
        the cross-compiled Alfresco CLTs.  These CLTs then need to be checked
        in as pre-built binaries (akin to 3rd party, but built by Alfresco).


  LIMITATIONS
        
        Currently, you've got to be on linux to build the 'clt' project.
        It would be nice to be able to build the clts for all supported
        platforms *from* all supported platforms (and OS X).


  BACKGROUND

        Here's an overview of different compiler types, so you can see where
        this mingw cross compiler (build/host=linux/i386;  target=win32/i386)
        fits into the scheme of things (source: The Definitive Guide to GCC,
        by Wililam Von Hagen):


   Build  Host   Target   Compiler Type    Result
   -----+-------+-------+----------------+-------------------------------------
   x86  | x86   | x86   | Native         | Built on an x86 to run on an x86
        |       |       |                | to generate binaries for an x86
   -----+-------+-------+----------------+-------------------------------------
   SH   | SH    | ARM   | Cross          | Built on a SuperH to run on a SuperH
        |       |       |                | to generate binaries for an ARM
   -----+-------+-------+----------------+-------------------------------------
   x86  | MIPS  | x86   | Crossback      | Built on an X86 to run on a MIPS
        |       |       |                | to generate binaries for an x86
   -----+-------+-------+----------------+-------------------------------------
   PPC  | SPARC | SPARC | Crossed native | Built on a PPC to run on a SPARC
        |       |       |                | to generate code for a SPARC
   -----+-------+-------+----------------+-------------------------------------
   ARM  | SH    | MIPS  | Canadian       | Built on an ARM to run on a SuperH
        |       |       |                | to generaete code for a MIPS
   -----+-------+-------+----------------+-------------------------------------


       
