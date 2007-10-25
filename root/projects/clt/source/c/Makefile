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
#	"PLATFORM=...", where the supported platforms are currently
#	linux/i86 and win32/i86.   
#
#	Thus the command:            make
#	is equivalent to:            make PLATFORM=linux/i86
#	but for a win32/i86, say:    make PLATFORM=win32/i86
#
#	The easiest way to set up a cross-compilation environment
#	on Debian is to install the 'mingw32' package.
#
#
# SEE ALSO: 	
#	http://www.fortran-2000.com/ArnaudRecipes/sharedlib.html
#------------------------------------------------------------------------------

all	: clt
.PHONY	: all clt check-platform mkdirs clean clean-platform

TOP			= .
SOURCE_DIR		= $(TOP)
BUILD_DIR_BASE		= $(TOP)/../../build/obj
DIST_DIR_BASE		= $(TOP)/../../build/dist
INCLUDE			= $(SOURCE_DIR)
INCLUDES 		= -I $(INCLUDE)


#-----------------------------------------------------------------------------
# Misc intermediate variables
#-----------------------------------------------------------------------------

PLATFORM=linux/i86

ifeq ($(PLATFORM),linux/i86)
	BUILD_DIR	= $(BUILD_DIR_BASE)/$(PLATFORM)
	DIST_DIR	= $(DIST_DIR_BASE)/$(PLATFORM)
	CC		= gcc
	CFLAGS		= -fPIC -Wall
	LINK_FLAGS	= -shared -L$(DIST_DIR)
	SHARED_LIB_EXT	= .so
	OBJ_EXT		= .o
	EXE_EXT		=
else ifeq ($(PLATFORM),win32/i86)
	BUILD_DIR	= $(BUILD_DIR_BASE)/$(PLATFORM)
	DIST_DIR	= $(DIST_DIR_BASE)/$(PLATFORM)
	CC		= i586-mingw32msvc-cc
	CFLAGS		= -Wall
	LINK_FLAGS	= -shared -L$(DIST_DIR)
	SHARED_LIB_EXT	= .dll
	OBJ_EXT		= .o
	EXE_EXT		= .exe
else
	CC		= echo "ERROR!  unsupported platform: $(PLATFORM)"
	UNSUPPORTED	= true
endif


#-----------------------------------------------------------------------------
# Source code for the CLT drivers & the lib they use go here
#-----------------------------------------------------------------------------
CLT_PROGRAM_SOURCES	= 
CLT_LIBRARY_SOURCES	=
CLT_LIBRARY		=
#
# CLT_PROGRAM_SOURCES	= driver_1.c driver_2.c
# CLT_LIBRARY_SOURCES	= xxx.c yyy.c
# CLT_LIBRARY		= libalfresco-clt
#


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
	@if [ -n "$(UNSUPPORTED)" ] ; then  				    \
	    echo "ERROR:  Unsupported Alfresco CLT platform '$(PLATFORM)'"; \
	    exit 1;							    \
	fi

clean:	check-platform
	rm -rf  $(BUILD_DIR_BASE)/*
	rm -rf  $(DIST_DIR_BASE)/*

clean-platform:  check-platform
	rm -rf  $(BUILD_DIR_BASE)/$(PLATFORM)/*
	rm -rf  $(DIST_DIR_BASE)/$(PLATFORM)/*


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
	$(CC) $(LINK_FLAGS) -o  $@  $(CFLAGS) $(CLT_LIB_OBJ)


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
	$(CC) -o $@ $(BUILD_DIR)/$(basename $(notdir $@))$(OBJ_EXT) $(CLT_LIB)

