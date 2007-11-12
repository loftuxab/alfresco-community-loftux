# Makefile for zlib, derived from Makefile.dj2.
# Modified for mingw32 by C. Spieler, 6/16/98.
# Updated for zlib 1.2.x by Christian Spieler and Cosmin Truta, Mar-2003.
# Updated for cross compilation (linux -> win32) via mingw 
# by Jon Cox <jcox@alfresco.org>,  Nov. 9, 2007 to work with gcc 3.4.5
#
# Copyright (C) 1995-2003 Jean-loup Gailly.
# For conditions of distribution and use, see copyright notice in zlib.h

# To use the asm code, type:
#   cp contrib/asm?86/match.S ./match.S
#   make LOC=-DASMV OBJA=match.o -fmakefile.gcc
#
# To install libz.a, zconf.h and zlib.h in the system directories, type:
#
#   make install -fmakefile.gcc

# Note:
# If the platform is *not* MinGW (e.g. it is Cygwin or UWIN),
# the DLL name should be changed from "zlib1.dll".

STATICLIB = libz.a
SHAREDLIB = zlib1.dll
IMPLIB    = libz.dll.a

#LOC = -DASMV
#LOC = -DDEBUG -g

CC = gcc
CFLAGS = $(LOC) -O3 -Wall -mms-bitfields

AS = $(CC)
ASFLAGS = $(LOC) -Wall

LD = $(CC)
LDFLAGS = $(LOC) -s 

AR = ar
ARFLAGS = rcs

RC = windres
RCFLAGS = --define GCC_WINDRES

CP = cp -fp
# If GNU install is available, replace $(CP) with install.
INSTALL = $(CP)
RM = rm -f

PREFIX = /usr/local
exec_prefix = $(PREFIX)

INCLUDE_PATH = $(PREFIX)/include
LIBRARY_PATH = $(PREFIX)/lib
BINARY_PATH  = $(PREFIX)/bin

OBJS = adler32.o compress.o crc32.o deflate.o gzio.o infback.o \
       inffast.o inflate.o inftrees.o trees.o uncompr.o zutil.o
OBJA =

all: $(STATICLIB) $(SHAREDLIB) $(IMPLIB) example_d.exe example.exe minigzip.exe minigzip_d.exe

# Test targets such as this don't make sense when cross compiling.
#
# test: example.exe minigzip.exe
# 	./example.exe
# 	echo hello world | ./minigzip.exe | ./minigzip.exe -d
# 
# testdll: example_d.exe minigzip_d.exe
# 	./example_d.exe
# 	echo hello world | ./minigzip_d.exe | ./minigzip_d.exe -d

.c.o:
	$(CC) $(CFLAGS) -c -o $@ $<

.S.o:
	$(AS) $(ASFLAGS) -c -o $@ $<

$(STATICLIB): $(OBJS) $(OBJA)
	$(AR) $(ARFLAGS) $@ $(OBJS) $(OBJA)

$(IMPLIB): $(SHAREDLIB)

$(SHAREDLIB): win32/zlib.def $(OBJS) $(OBJA) zlibrc.o
	$(CC)      -shared -o $@ $(OBJS) $(OBJA) zlibrc.o win32/zlib.def
	$(DLLTOOL) --input-def win32/zlib.def --dllname $@  --output-lib $(IMPLIB)

example.exe: example.o $(STATICLIB)
	$(CC) $(LDFLAGS) -o $@ example.o $(STATICLIB)

minigzip.exe: minigzip.o $(STATICLIB)
	$(CC) $(LDFLAGS) -o $@ minigzip.o $(STATICLIB)

example_d.exe: example.o $(IMPLIB)
	$(CC) $(LDFLAGS) -o $@ example.o  $(IMPLIB)
	$(CC) $(LDFLAGS) -o example_d_direct.exe example.o  $(IMPLIB)

minigzip_d.exe: minigzip.o $(IMPLIB)
	$(CC) $(LDFLAGS) -o $@ minigzip.o  $(IMPLIB)

zlibrc.o: win32/zlib1.rc
	$(RC) $(RCFLAGS) -o $@ win32/zlib1.rc


.PHONY: install uninstall clean

install: zlib.h zconf.h $(STATICLIB) $(SHAREDLIB) $(IMPLIB)
	@if [ ! -d $(BINARY_PATH) ]  ; then mkdir -p $(BINARY_PATH)  ; fi
	@if [ ! -d $(INCLUDE_PATH) ] ; then mkdir -p $(INCLUDE_PATH) ; fi
	@if [ ! -d $(LIBRARY_PATH) ] ; then mkdir -p $(LIBRARY_PATH) ; fi
	-$(INSTALL) $(SHAREDLIB) $(BINARY_PATH)
	-$(INSTALL) zlib.h       $(INCLUDE_PATH)
	-$(INSTALL) zconf.h      $(INCLUDE_PATH)
	-$(INSTALL) $(STATICLIB) $(LIBRARY_PATH)
	-$(INSTALL) $(IMPLIB)    $(LIBRARY_PATH)

uninstall:
	-$(RM) $(BINARY_PATH)/$(SHAREDLIB)
	-$(RM) $(INCLUDE_PATH)/zlib.h
	-$(RM) $(INCLUDE_PATH)/zconf.h
	-$(RM) $(LIBRARY_PATH)/$(STATICLIB)
	-$(RM) $(LIBRARY_PATH)/$(IMPLIB)

clean:
	-$(RM) $(SHAREDLIB)
	-$(RM) $(STATICLIB)
	-$(RM) $(IMPLIB)
	-$(RM) *.o
	-$(RM) *.exe
	-$(RM) foo.gz

adler32.o: zlib.h zconf.h
compress.o: zlib.h zconf.h
crc32.o: crc32.h zlib.h zconf.h
deflate.o: deflate.h zutil.h zlib.h zconf.h
example.o: zlib.h zconf.h
gzio.o: zutil.h zlib.h zconf.h
inffast.o: zutil.h zlib.h zconf.h inftrees.h inflate.h inffast.h
inflate.o: zutil.h zlib.h zconf.h inftrees.h inflate.h inffast.h
infback.o: zutil.h zlib.h zconf.h inftrees.h inflate.h inffast.h
inftrees.o: zutil.h zlib.h zconf.h inftrees.h
minigzip.o: zlib.h zconf.h
trees.o: deflate.h zutil.h zlib.h zconf.h trees.h
uncompr.o: zlib.h zconf.h
zutil.o: zutil.h zlib.h zconf.h
