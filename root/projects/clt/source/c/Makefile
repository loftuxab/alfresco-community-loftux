#------------------------------------------------------------------------------
# Copyright 2005-2007 Alfresco Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
# exception to the terms and conditions of version 2.0 of the GPL, you may
# redistribute this Program in connection with Free/Libre and Open Source
# Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
# You should have received a copy of the text describing the FLOSS exception,
# and it is also available here:   http://www.alfresco.com/legal/licensing
#
#
# Author  Jon Cox  <jcox@alfresco.com>
# File    Makefile
# Use	  Compiles the Alfresco CLTs
#	  Currently, this is just an examlar of how it will be done.
#
#------------------------------------------------------------------------------
#
# NOTES:
#	This Makefile uses GCC to handle header dependencies automatically.
#
#	Currently, this Makefile only runs on Linux, but it can can do
#	cross-compliation for a limited number of other platforms
#	(assuming your build environment is set up properly).
#	
#	The targeted  platform is specified by the command-line argument
#	"TARGET_PLATFORM=...", where the supported platforms are currently
#	linux/x86 and win32/x86.   
#
#	Thus the command:            make
#	is equivalent to:            make TARGET_PLATFORM=linux/x86
#	but for a win32/x86, say:    make TARGET_PLATFORM=win32/x86
#
#	The easiest way to set up a cross-compilation environment
#	on Debian is to install the 'mingw32' package.
#
#       It might be worth investigating performance differences someday
#       between different x86 versions.  An i686 CPU can run i586 and i386
#       instructions but it may not execute as optimally as one specifically
#       compiled for an i686.  The most common options are:
#
#       x86_64 Processors
#       -----------------
#         o AMD's Athlon 64, Athlon 64-FX, and Opteron
#         o Intel EMT64 - Nocona Xeon, Pentium 4's using revision EO
#                         of the Prescott core (Pentium 4, model F)
#         
#       i686 Processors
#       -----------------
#         o All Intel 32-bit Pentiums (excluding Pentium 1 and Pentium MMX)
#         o All AMD 32-bit Athlons
#         
#       i586 Processors
#       -----------------
#         o All 32-bit AMD Ks
#         o Pentium 1
#         o Pentium MMX
#         
#       i386 Processors
#       -----------------
#         o Lowest common denominator for Intel 80386 compatible CPUs.
#           This includes all of the above, but does not take advantage 
#           of various extended instructions.
#
# 	See also:  http://www.fortran-2000.com/ArnaudRecipes/sharedlib.html
#
#------------------------------------------------------------------------------
#
#    Official gcc naming scheme for cross compilation 
#    ------------------------------------------------
#
#	The gcc cross tool chain has adopted the following scheme to
#	identify a host/build/target platform:
#
#		<cpu>-<manufacturer>-<kernel>-<os>
#
#	For example:
#	
#		i686-pc-linux-gnu 
#
#	This is the instruction set of an i686, and is an IBM compatible PC
#	running the linux kernel on the GNU os.  The command "uname -smo" says:
#
#		Linux i686 GNU/Linux
#
#
#
#    Cross compilation data: (from gccinstall.info)
#    ----------------------------------------------
#
#	Here are the possible CPU types:
#
#	     1750a, a29k, alpha, arm, avr, cN, clipper, dsp16xx, elxsi, fr30,
#	     h8300, hppa1.0, hppa1.1, i370, i386, i486, i586, i686, i786, i860,
#	     i960, ip2k, m32r, m68000, m68k, m6811, m6812, m88k, mcore, mips,
#	     mipsel, mips64, mips64el, mn10200, mn10300, ns32k, pdp11, powerpc,
#	     powerpcle, romp, rs6000, sh, sparc, sparclite, sparc64, v850, vax,
#	     we32k.
#
#	Here are the recognized company names.  As you can see, customary
#	abbreviations are used rather than the longer official names.
#
#	     acorn, alliant, altos, apollo, apple, att, bull, cbm, convergent,
#	     convex, crds, dec, dg, dolphin, elxsi, encore, harris, hitachi,
#	     hp, ibm, intergraph, isi, mips, motorola, ncr, next, ns, omron,
#	     plexus, sequent, sgi, sony, sun, tti, unicom, wrs.
#
#	The company name is meaningful only to disambiguate when the rest of
#	the information supplied is insufficient.  You can omit it, writing
#	just `CPU-SYSTEM', if it is not needed.  For example, `vax-ultrix4.2'
#	is equivalent to `vax-dec-ultrix4.2'.
#
#	Here is a list of system types:
#
#	    386bsd, aix, acis, amigaos, aos, aout, aux, bosx, bsd, clix, coff,
#	    ctix, cxux, dgux, dynix, ebmon, ecoff, elf, esix, freebsd, hms,
#	    genix, gnu, linux, linux-gnu, hiux, hpux, iris, irix, isc, luna,
#	    lynxos, mach, minix, msdos, mvs, netbsd, newsos, nindy, ns, osf,
#	    osfrose, ptx, riscix, riscos, rtu, sco, sim, solaris, sunos, sym,
#	    sysv, udi, ultrix, unicos, uniplus, unos, vms, vsta, vxworks,
#	    winnt, xenix.
#
#    Chip Arch.       "Common chip names"
#   ----------------+---------------------------------------------------------
#     8086 = 8-bit  |
#   ----------------+---------------------------------------------------------
#    80186 = 8-bit  |
#   ----------------+---------------------------------------------------------
#    80286 = 16-bit |
#   ----------------+---------------------------------------------------------
#    80386 = 32-bit |
#   ----------------+---------------------------------------------------------
#    80486 = 32-bit |
#   ----------------+---------------------------------------------------------
#    80586 = 32-bit | Pentium, Pentium MMX, K5, K6, K6-II, K6-III
#   ----------------+---------------------------------------------------------
#    80686 = 32-bit | Pentium Pro, Pentium II, Pentium III, Athlon, Athlon XP, 
#                   | Opteron*, Athlon FX-51*, Athlon FX-53*, Athlon 64*
#                   |
#                   | *= 32-bit/64-bit
#   ----------------+---------------------------------------------------------
#    80786 = 32-bit | Pentium 4
#   ----------------+---------------------------------------------------------
#
#
#------------------------------------------------------------------------------


all	: clt
.PHONY	: all clt check-platform mkdirs clean clean-platform

TOP			= .
SOURCE_DIR		= $(TOP)
BUILD_DIR_BASE		= $(TOP)/../../build/obj
DIST_DIR_BASE		= $(TOP)/../../build/dist
INCLUDE			= $(SOURCE_DIR)
INCLUDES 		= -I $(INCLUDE)
LDFLAGS			= -L$(DIST_DIR)
SH_LDFLAGS		= -shared
OBJ_EXT			= .o
BUILD_DIR		= $(BUILD_DIR_BASE)/$(TARGET_PLATFORM)
DIST_DIR		= $(DIST_DIR_BASE)/$(TARGET_PLATFORM)

#-----------------------------------------------------------------------------
# Misc intermediate variables
#-----------------------------------------------------------------------------

TARGET_PLATFORM=linux/x86


ifeq ($(TARGET_PLATFORM),linux/x86)
	CC		= gcc
	CFLAGS		= -fPIC -Wall
	LIBS		=
	SHARED_LIB_EXT	= .so
	EXE_EXT		=
	#
	# TODO:  Create controlled build environment on Linux 
	#	 rather than rely on whatever is present.
	#
else ifeq ($(TARGET_PLATFORM),win32/x86)
	CC		= i386-mingw32-gcc  
	CFLAGS		= -Wall
	LIBS		= -lws2_32
	SHARED_LIB_EXT	= .dll
	EXE_EXT		= .exe

	# Set the compiler prefix on a linux machine
	compiler_prefix = $(subst Linux-i386,$(MINGW_ROOT)/usr,$(BUILD_PLATFORM))

	# Massage variables & PATH to deal with compilers on each build platform
	PATH	:=   $(compiler_prefix)/bin:$(PATH)
	CFLAGS	+= -I$(compiler_prefix)/include
	LDFLAGS	+= -B$(compiler_prefix)/lib
else
	CC		= echo "ERROR!  unsupported platform: $(TARGET_PLATFORM)"
	UNSUPPORTED	= true
endif



#-----------------------------------------------------------------------------
# Source code for the CLT drivers & the lib they use go here
#-----------------------------------------------------------------------------
CLT_PROGRAM_SOURCES	= 
CLT_LIBRARY_SOURCES	=
CLT_LIBRARY		=

# CLT_PROGRAM_SOURCES	= driver_1.c driver_2.c
# CLT_LIBRARY_SOURCES	= xxx.c yyy.c
# CLT_LIBRARY_STEM	= alfresco-clt
# CLT_LIBRARY		= lib$(CLT_LIBRARY_STEM)



#=============================================================================
#
#  Gory details... 
#
#=============================================================================

ALL_SOURCES	=  $(patsubst %,$(SOURCE_DIR)/%, 	   		\
				$(CLT_PROGRAM_SOURCES)	   		\
				$(CLT_LIBRARY_SOURCES))

ALL_OBJS	=  $(patsubst %.c,$(BUILD_DIR)/%.o,	   		\
				$(notdir $(ALL_SOURCES)))

CLT_PROGS	= $(patsubst %,$(DIST_DIR)/%$(EXE_EXT), 		\
				$(basename $(CLT_PROGRAM_SOURCES)))

CLT_LIB		=  $(DIST_DIR)/$(CLT_LIBRARY)$(SHARED_LIB_EXT)

CLT_PROG_OBJ	=  $(patsubst %,$(BUILD_DIR)/%$(OBJ_EXT), 		\
				$(basename $(CLT_PROGRAM_SOURCES)))

CLT_LIB_OBJ	=  $(patsubst %,$(BUILD_DIR)/%$(OBJ_EXT), 		\
				$(basename $(CLT_LIBRARY_SOURCES)))


#-----------------------------------------------------------------------------
# Default target:  build all the CLTs
#-----------------------------------------------------------------------------
clt:   check-platform mkdirs $(CLT_PROGS)


#-----------------------------------------------------------------------------
# Remove all build artifacts from current build platform
#-----------------------------------------------------------------------------
check-platform:
	@if [ -n "$(UNSUPPORTED)" ] ; then  				     \
	    echo "ERROR:  Unsupported target platform '$(TARGET_PLATFORM)'"; \
	    exit 1;							     \
	fi

clean:	check-platform
	echo path is $(PATH)
	rm -rf  $(BUILD_DIR_BASE)/*
	rm -rf  $(DIST_DIR_BASE)/*


clean-platform:  check-platform
	rm -rf  $(BUILD_DIR_BASE)/$(TARGET_PLATFORM)/*
	rm -rf  $(DIST_DIR_BASE)/$(TARGET_PLATFORM)/*


#-----------------------------------------------------------------------------
# Include autogenerated dependendency info
#-----------------------------------------------------------------------------
-include $(wildcard $(BUILD_DIR)/*.d)


#-----------------------------------------------------------------------------
# Make sure the build & dist dirs exist
#-----------------------------------------------------------------------------
mkdirs: check-platform	$(BUILD_DIR) $(DIST_DIR)

$(BUILD_DIR):
	[ -d $(BUILD_DIR) ] || mkdir -p $(BUILD_DIR)

$(DIST_DIR):
	[ -d $(DIST_DIR) ] || mkdir -p $(DIST_DIR)


#-----------------------------------------------------------------------------
# Create the shared library
#-----------------------------------------------------------------------------
$(CLT_LIB): $(CLT_LIB_OBJ)
	$(CC) $(SH_LDFLAGS) $(LDFLAGS) -o  $@  $(CFLAGS) $(CLT_LIB_OBJ)


#-----------------------------------------------------------------------------
# Default rule for building object files, and their dependencies	
#	Cute sed trick, ey?  :)
#-----------------------------------------------------------------------------
$(BUILD_DIR)/%$(OBJ_EXT):
	$(CC) -MM  $(SOURCE_DIR)/$*.c | sed -e "s#^#$(BUILD_DIR)/#g" > $@.d; \
	$(CC) $(CFLAGS) -c -o $@ $(SOURCE_DIR)/$*.c


#-----------------------------------------------------------------------------
# Build all the CLTs
#	Note:	This target goes at the end so as to limit the
#		set of targets subject to ".SECONDEXPANSION"
#-----------------------------------------------------------------------------
.SECONDEXPANSION:
$(CLT_PROGS): $(BUILD_DIR)/$$(basename $$(notdir $$@))$(OBJ_EXT)  $(CLT_LIB)
	$(CC) -o $@ $(BUILD_DIR)/$(basename $(notdir $@))$(OBJ_EXT) \
            $(LDFLAGS) -l$(CLT_LIBRARY_STEM)  $(LIBS)

