# pgin.tcl - PostgreSQL Tcl Interface direct to protocol v3 backend
# $Id: pgin.tcl,v 3.34 2006-08-14 01:52:04+00 lbayuk Exp $
# This version encodes/decodes UNICODE data to/from PostgreSQL.
#
# Copyright 1998-2006 by ljb (lbayuk@mindspring.com)
# May be freely distributed with or without modification; must retain this
# notice; provided with no warranties.
# See the file COPYING for complete information on usage and redistribution
# of this file, and for a disclaimer of all warranties.
#
# See the file INTERNALS in the source distribution for more information
# about how this thing works, including namespace variables.
#
# Also includes:
#    md5.tcl - Compute MD5 Checksum

package require Tcl 8.3

# === Definition of the pgtcl namespace ===

namespace eval pgtcl {
  # Debug flag:
  variable debug 0

  # Version number, also used in package provide at the bottom of this file:
  variable version 3.0.1

  # Counter for making uniquely named result structures:
  variable rn 0

  # Array mapping error field names to protocol codes:
  # Secondary values (without prefix before '_') have been added for
  # compatibility with Gborg pgtcl (sigh...)
  variable errnames
  array set errnames {
    SEVERITY S
    SQLSTATE C
    MESSAGE_PRIMARY M
    MESSAGE_DETAIL D
    MESSAGE_HINT H
    STATEMENT_POSITION P
    CONTEXT W
    SOURCE_FILE F
    SOURCE_LINE L
    SOURCE_FUNCTION R
    PRIMARY M
    DETAIL D
    HINT H
    POSITION P
    FILE F
    LINE L
    FUNCTION R
  }
}

# === Internal Low-level I/O procedures for v3 protocol ===

# Internal procedure to send a packet to the backend with type and length.
# Type can be empty - this is used for the startup packet.
# The default is to flush the channel, since almost all messages generated
# by pgin.tcl need to wait for a response. The exception is prepared queries.
proc pgtcl::sendmsg {sock type data {noflush ""}} {
  puts -nonewline $sock \
      $type[binary format I [expr {[string length $data]+4}]]$data
  if {$noflush == ""} {
    flush $sock
  }
}

# Read a message and return the message type byte:
# This initializes the per-connection buffer too.
# This has a special check for a v2 error message, which is needed at
# startup in case of talking to v2 server. It assumes we will not
# get a V3 error message longer than 0x20000000 bytes, which is pretty safe.
# It fakes up a V3 error with severity ERROR, code (5 spaces), and the message.
proc pgtcl::readmsg {sock} {
  upvar #0 pgtcl::buf_$sock buf pgtcl::bufi_$sock bufi pgtcl::bufn_$sock bufn
  set bufi 0
  if {[binary scan [read $sock 5] aI type len] != 2} {
    set err "pgtcl: Unable to read message from database"
    if {[eof $sock]} {
      append err " - server closed connection"
    }
    error $err
  }
  if {$type == "E" && $len >= 0x20000000} {
    if {$pgtcl::debug} { puts "Warning: V2 error message received!" }
    # Build the start of the V3 error, including the 4 misread bytes in $len:
    set buf [binary format {a a*x a a*x a I} S ERROR C "     " M $len]
    while {[set c [read $sock 1]] != ""} {
      append buf $c
      if {$c == "\000"} break
    }
    # This is 'code=0' to mark no more error options.
    append buf "\000"
    set bufn [string length $buf]
  } else {
    set bufn [expr {$len - 4}]
    set buf [read $sock $bufn]
  }
  return $type
}

# Return the next byte from the buffer:
proc pgtcl::get_byte {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  set result [string index $buf $bufi]
  incr bufi
  return $result
}

# Return the next $n bytes from the buffer:
proc pgtcl::get_bytes {db n} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  set obufi $bufi
  incr bufi $n
  return [string range $buf $obufi [expr {$obufi + $n - 1}]]
}

# Return the rest of the buffer.
proc pgtcl::get_rest {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi pgtcl::bufn_$db bufn
  set obufi $bufi
  set bufi $bufn
  return [string range $buf $obufi end]
}

# Skip next $n bytes in the buffer.
proc pgtcl::skip {db n} {
  upvar #0 pgtcl::bufi_$db bufi
  incr bufi $n
}

# Return next int32 from the buffer:
proc pgtcl::get_int32 {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  if {[binary scan $buf "x$bufi I" i] != 1} {
    set i 0
  }
  incr bufi 4
  return $i
}

# Return next signed int16 from the buffer:
proc pgtcl::get_int16 {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  if {[binary scan $buf "x$bufi S" i] != 1} {
    set i 0
  }
  incr bufi 2
  return $i
}

# Return next unsigned int16 from the buffer:
proc pgtcl::get_uint16 {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  if {[binary scan $buf "x$bufi S" i] != 1} {
    set i 0
  }
  incr bufi 2
  return [expr {$i & 0xffff}]
}

# Return next signed int8 from the buffer:
# (This is only used in 1 place in the protocol...)
proc pgtcl::get_int8 {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  if {[binary scan $buf "x$bufi c" i] != 1} {
    set i 0
  }
  incr bufi
  return $i
}

# Return the next null-terminated string from the buffer:
# This decodes the UNICODE data. It is used for people-readable text like
# messages, not query result data.
proc pgtcl::get_string {db} {
  upvar #0 pgtcl::buf_$db buf pgtcl::bufi_$db bufi
  set end [string first "\000" $buf $bufi]
  if {$end < 0} {
    return ""
  }
  set obufi $bufi
  set bufi [expr {$end + 1}]
  return [encoding convertfrom identity \
      [string range $buf $obufi [expr {$end - 1}]]]
}

# === Internal Mid-level I/O procedures for v3 protocol ===

# Parse a backend ErrorResponse or NoticeResponse message. The Severity
# and Message parts are returned together with a trailing newline, like v2
# protocol did. If optional result_name is supplied, it is the name of
# a result structure to store all error parts in, indexed as (error,$code).
proc pgtcl::get_response {db {result_name ""}} {
  if {$result_name != ""} {
    upvar $result_name result
  }
  array set result {error,S ERROR error,M {}}
  while {[set c [pgtcl::get_byte $db]] != "\000" && $c != ""} {
    set result(error,$c) [pgtcl::get_string $db]
  }
  return "$result(error,S):  $result(error,M)\n"
}

# Handle ParameterStatus and remember the name and value:
proc pgtcl::get_parameter_status {db} {
  upvar #0 pgtcl::param_$db param
  set name [pgtcl::get_string $db]
  set param($name) [pgtcl::get_string $db]
  if {$pgtcl::debug} { puts "+server param $name=$param($name)" }
}

# Handle a notification ('A') message.
# The notifying backend pid and more_info are read but ignored.
proc pgtcl::get_notification_response {db} {
  set notify_pid [pgtcl::get_int32 $db]
  set notify_rel [pgtcl::get_string $db]
  set more_info [pgtcl::get_string $db]
  if {$pgtcl::debug} { puts "+pgtcl got notify from $notify_pid: $notify_rel" }
  if {[info exists pgtcl::notify($db,$notify_rel)]} {
    after idle $pgtcl::notify($db,$notify_rel)
  }
}

# Handle a notice ('N') message. If no handler is defined, or the handler is
# empty, do nothing, otherwise, call the handler with the message argument
# appended. For backward compatibility with v2 protocol, the message is
# assumed to end in a newline.
proc pgtcl::get_notice {db} {
  set msg [pgtcl::get_response $db]
  if {[info exists pgtcl::notice($db)] && [set cmd $pgtcl::notice($db)] != ""} {
    eval $cmd [list $msg]
  }
}

# Internal procedure to read a tuple (row) from the backend.
# Column count is redundant, but check it anyway.
# Format code (text/binary) is used to do Unicode decoding on Text only.
proc pgtcl::gettuple {db result_name} {
  upvar $result_name result
  if {$result(nattr) == 0} {
    unset result
    error "Protocol error, data before descriptor"
  }
  set irow $result(ntuple)
  set nattr [pgtcl::get_uint16 $db]
  if {$nattr != $result(nattr)} {
    unset result
    error "Expecting $result(nattr) columns, but data row has $nattr"
  }
  set icol 0
  foreach format $result(formats) {
    set col_len [pgtcl::get_int32 $db]
    if {$col_len > 0} {
      if ($format) {
        set result($irow,$icol) [pgtcl::get_bytes $db $col_len]
      } else {
        set result($irow,$icol) [encoding convertfrom identity \
           [pgtcl::get_bytes $db $col_len]]
      }
    } else {
      set result($irow,$icol) ""
      if {$col_len < 0} {
        set result(null,$irow,$icol) ""
      }
    }
    incr icol
  }
  incr result(ntuple)
}

# Internal procedure to handle common backend utility message types:
#    C : Completion status        E : Error
#    N : Notice message           A : Notification
#    S : ParameterStatus
# This can be given any message type. If it handles the message,
# it returns 1. If it doesn't handle the message, it returns 0.
#
proc pgtcl::common_message {msgchar db result_name} {
  upvar $result_name result
  switch -- $msgchar {
    A { pgtcl::get_notification_response $db }
    C { set result(complete) [pgtcl::get_string $db] }
    N { pgtcl::get_notice $db }
    S { pgtcl::get_parameter_status $db }
    E {
      set result(status) PGRES_FATAL_ERROR
      set result(error) [pgtcl::get_response $db result]
    }
    default { return 0 }
  }
  return 1
}

# === Other internal support procedures ===

# Internal procedure to set a default value from the environment:
proc pgtcl::default {default args} {
  global env
  foreach a $args {
    if {[info exists env($a)]} {
      return $env($a)
    }
  }
  return $default
}

# Internal procedure to parse a connection info string.
# This has to handle quoting and escaping. See the PostgreSQL Programmer's
# Guide, Client Interfaces, Libpq, Database Connection Functions.
# The definitive reference is the PostgreSQL source code in:
#          interface/libpq/fe-connect.c:conninfo_parse()
# One quirk to note: backslash escapes work in quoted values, and also in
# unquoted values, but you cannot use backslash-space in an unquoted value,
# because the space ends the value regardless of the backslash.
#
# Stores the results in an array $result(paramname)=value. It will not
# create a new index in the array; if paramname does not already exist,
# it means a bad parameter was given (one not defined by pg_conndefaults).
# Returns an error message on error, else an empty string if OK.
proc pgtcl::parse_conninfo {conninfo result_name} {
  upvar $result_name result
  while {[regexp {^ *([^=]*)= *(.+)} $conninfo unused name conninfo]} {
    set name [string trim $name]
    if {[regexp {^'(.*)} $conninfo unused conninfo]} {
      set value ""
      set n [string length $conninfo]
      for {set i 0} {$i < $n} {incr i} {
        if {[set c [string index $conninfo $i]] == "\\"} {
          set c [string index $conninfo [incr i]]
        } elseif {$c == "'"} break
        append value $c
      }
      if {$i >= $n} {
        return "unterminated quoted string in connection info string"
      }
      set conninfo [string range $conninfo [incr i] end]
    } else {
      regexp {^([^ ]*)(.*)} $conninfo unused value conninfo
      regsub -all {\\(.)} $value {\1} value
    }
    if {$pgtcl::debug} { puts "+parse_conninfo name=$name value=$value" }
    if {![info exists result($name)]} {
      return "invalid connection option \"$name\""
    }
    set result($name) $value
  }
  if {[string trim $conninfo] != ""} {
    return "syntax error in connection info string '...$conninfo'"
  }
  return ""
}

# Internal procedure to check for valid result handle. This returns
# the fully qualified name of the result array.
# Usage:  upvar #0 [pgtcl::checkres $res] result
proc pgtcl::checkres {res} {
  if {![info exists pgtcl::result$res]} {
    error "Invalid result handle\n$res is not a valid query result"
  }
  return "pgtcl::result$res"
}

# === Public procedures : Connecting and Disconnecting ===

# Return connection defaults as {optname label dispchar dispsize value}...
proc pg_conndefaults {} {
  set user [pgtcl::default user PGUSER USER LOGNAME USERNAME]
  set result [list \
    [list user     Database-User    {} 20 $user] \
    [list password Database-Password *  20 [pgtcl::default {} PGPASSWORD]] \
    [list host     Database-Host    {} 40 [pgtcl::default localhost PGHOST]] \
         {hostaddr Database-Host-IP-Address {} 45 {}} \
    [list port     Database-Port    {}  6 [pgtcl::default 5432 PGPORT]] \
    [list dbname   Database-Name    {} 20 [pgtcl::default $user PGDATABASE]] \
    [list tty      Backend-Debug-TTY  D 40 [pgtcl::default {} PGTTY]] \
    [list options  Backend-Debug-Options D 40 [pgtcl::default {} PGOPTIONS]] \
  ]
  if {$pgtcl::debug} { puts "+pg_conndefaults: $result" }
  return $result
}

# Connect to database. Only the new form, with -conninfo, is recognized.
# We speak backend protocol v3, and only handle clear-text password and
# MD5 authentication (messages R 3, and R 5).
# A parameter is added to set client_encoding to UNICODE. This is due to
# Tcl's way of representing strings.
proc pg_connect {args} {

  if {[llength $args] != 2 || [lindex $args 0] != "-conninfo"} {
    error "Connection to database failed\nMust use pg_connect -conninfo form"
  }

  # Get connection defaults into an array opt(), then merge caller params:
  foreach o [pg_conndefaults] {
    set opt([lindex $o 0]) [lindex $o 4]
  }
  if {[set msg [pgtcl::parse_conninfo [lindex $args 1] opt]] != ""} {
    error "Connection to database failed\n$msg"
  }

  # Hostaddr overrides host, per documentation, and we need host below.
  if {$opt(hostaddr) != ""} {
    set opt(host) $opt(hostaddr)
  }

  if {$pgtcl::debug} {
    puts "+pg_connect to $opt(dbname)@$opt(host):$opt(port) as $opt(user)"
  }

  if {[catch {socket $opt(host) $opt(port)} sock]} {
    error "Connection to database failed\n$sock"
  }
  # Note: full buffering, socket must be flushed after write!
  fconfigure $sock -buffering full -translation binary

  # Startup packet:
  pgtcl::sendmsg $sock {} [binary format "I a*x a*x a*x a*x a*x a*x a*x a*x x" \
        0x00030000 \
        user $opt(user) database $opt(dbname) \
        client_encoding UNICODE options $opt(options)]

  set msg {}
  while {[set c [pgtcl::readmsg $sock]] != "Z"} {
    switch $c {
      E {
        set msg [pgtcl::get_response $sock]
        break
      }
      R {
        set n [pgtcl::get_int32 $sock]
        if {$n == 3} {
          pgtcl::sendmsg $sock p "$opt(password)\000"
        } elseif {$n == 5} {
          set salt [pgtcl::get_bytes $sock 4]
          # This is from PostgreSQL source backend/libpq/crypt.c:
          set md5_response \
            "md5[md5::digest [md5::digest $opt(password)$opt(user)]$salt]"
          if {$pgtcl::debug} { puts "+pg_connect MD5 sending: $md5_response" }
          pgtcl::sendmsg $sock p "$md5_response\000"
        } elseif {$n != 0} {
          set msg "Unknown database authentication request($n)"
          break
        }
      }
      K {
        set pid [pgtcl::get_int32 $sock]
        set key [pgtcl::get_int32 $sock]
        if {$pgtcl::debug} { puts "+server pid=$pid key=$key" }
      }
      S {
        pgtcl::get_parameter_status $sock
      }
      default {
        set msg "Unexpected reply from database: $c"
        break
      }
    }
  }
  if {$msg != ""} {
    close $sock
    error "Connection to database failed\n$msg"
  }
  # Initialize transaction status; should be get_byte but it better be I:
  set pgtcl::xstate($sock) I
  # Initialize action for NOTICE messages (see get_notice):
  set pgtcl::notice($sock) {puts -nonewline stderr}

  return $sock
}

# Disconnect from the database. Free all result structures which are
# associated with this connection, and other data for this connection,
# including the buffer.
# Note: This does not use {array unset} (Tcl 8.3) nor {unset -nocomplain}
# (Tcl 8.4), but is coded to be compatible with earlier versions.
proc pg_disconnect {db} {
  if {$pgtcl::debug} { puts "+Disconnecting $db from database" }
  pgtcl::sendmsg $db X {}
  catch {close $db}
  foreach v [info vars pgtcl::result*] {
    upvar #0 $v result
    if {$result(conn) == $db} {
      if {$pgtcl::debug} { puts "+Freeing left-over result structure $v" }
      unset result
    }
  }
  if {[array exists pgtcl::notify]} {
    foreach v [array names pgtcl::notify $db,*] {
      unset pgtcl::notify($v)
    }
  }
  catch { unset pgtcl::param_$db }
  catch { unset pgtcl::xstate($db) pgtcl::notice($db) }
  catch { unset pgtcl::buf_$db pgtcl::bufi_$db pgtcl::bufn_$db }
}

# === Internal procedures: Query Result and supporting functions ===

# Read the backend reply to a query (simple or extended) and build a
# result structure. For extended query mode, the client already sent
# the Bind, DescribePortal, Execute, and Sync.
# This implements most of the backend query response protocol. The important
# reply codes are:
#  T : RowDescription describes the attributes (columns) of each data row.
#  D : DataRow has data for 1 tuple.
#  Z : ReadyForQuery, update transaction status.
#  H : Ready for Copy Out
#  G : Ready for Copy In
# Plus the messages handled by pgtcl::common_message.
# If the optional parameter $extq == 1, the result handle is from an extended
# mode query (see pg_exec_prepared) and these messages are allowed and ignored:
#  2 : BindComplete
#  1 : ParseComplete (used only for exec_params)
#  n : NoData
#
# Returns a result handle (the number pgtcl::rn), or throws an error.

proc pgtcl::getresult {db {extq 0}} {
  upvar #0 pgtcl::result[incr pgtcl::rn] result
  set result(conn) $db
  array set result {
    nattr 0     ntuple 0
    attrs {}    types {}    sizes {}    modifs {}   formats {}
    error {}    tbloids {}  tblcols {}
    complete {}
    status PGRES_COMMAND_OK
  }

  while {1} {
    set c [pgtcl::readmsg $db]
    switch $c {
      D {
        pgtcl::gettuple $db result
      }
      T {
        if {$result(nattr) != 0} {
          unset result
          error "Protocol failure, multiple descriptors"
        }
        set result(status) PGRES_TUPLES_OK
        set nattr [pgtcl::get_uint16 $db]
        set result(nattr) $nattr
        for {set icol 0} {$icol < $nattr} {incr icol} {
          lappend result(attrs) [pgtcl::get_string $db]
          lappend result(tbloids) [pgtcl::get_int32 $db]
          lappend result(tblcols) [pgtcl::get_uint16 $db]
          lappend result(types) [pgtcl::get_int32 $db]
          lappend result(sizes) [pgtcl::get_int16 $db]
          lappend result(modifs) [pgtcl::get_int32 $db]
          lappend result(formats) [pgtcl::get_int16 $db]
        }
      }
      I {
        set result(status) PGRES_EMPTY_QUERY
      }
      H {
        pgtcl::begincopy result OUT
        break
      }
      G {
        pgtcl::begincopy result IN
        break
      }
      Z {
        set pgtcl::xstate($db) [pgtcl::get_byte $db]
        break
      }
      default {
        if {(!$extq || ($c != "2" && $c != "n" && $c != "1")) && \
              ![pgtcl::common_message $c $db result]} {
          unset result
          error "Unexpected reply from database: $c"
        }
      }
    }
  }
  if {$pgtcl::debug > 1} {
    puts "+pgtcl::getresult $pgtcl::rn = "
    parray result
  }
  return $pgtcl::rn
}

# Process format code information for pg_exec_prepared.
#   fclist       A list of BINARY (or B*) or TEXT (or T*) format code words.
#   ncodes_name  The name of a variable to get the number of format codes.
#   codes_name   The name of a variable to get a list of format codes in
#                the PostgreSQL syntax: 0=text 1=binary.
proc pgtcl::crunch_fcodes {fclist ncodes_name codes_name} {
  upvar $ncodes_name ncodes $codes_name codes
  set ncodes [llength $fclist]
  set codes {}
  foreach k $fclist {
    if {[string match B* $k]} {
      lappend codes 1
    } else {
      lappend codes 0
    }
  }
}

# Return an error code field value for pg_result -error?Field? code.
# For field names, it accepts either the libpq name (without PG_DIAG_) or the
# single-letter protocol code.
# For compatibility with changes made to Gborg pgtcl after this feature was
# added here, it also accepts names without prefixes, and $code is case
# insensitive.
# If an unknown field name is used, or the field isn't part of the error
# message, an empty string is substituted.

proc pgtcl::error_fields {result_name code} {
  upvar $result_name result
  variable errnames
  set code [string toupper $code]
  if {[info exists errnames($code)]} {
    set code $errnames($code)
  }
  if {[info exists result(error,$code)]} {
    return $result(error,$code)
  }
  return ""
}

# === Public procedures : Query and Result ===

# Execute SQL and return a result handle.
# If parameters are supplied, use pg_exec_params in all-text arg mode.
# (Let pg_exec_params encode the query in that case.)

proc pg_exec {db query args} {
  if {$pgtcl::debug} { puts "+pg_exec $query {$args}" }
  if {[llength $args] == 0} {
    pgtcl::sendmsg $db Q "[encoding convertto identity $query]\000"
    return [pgtcl::getresult $db]
  }
  return [eval pg_exec_params {$db} {$query} {{}} {{}} {{}} $args]
}

# Extract data from a pg_exec result structure.
# -cmdTuples, -list, and -llist are extensions to the baseline libpgtcl which
# have appeared or will appear in beta or future versions.
# -errorField, -lxAttributes and -getNull are proposed new for 7.4.
# -cmdStatus is new with pgintcl-2.0.1

proc pg_result {res option args} {
  upvar #0 [pgtcl::checkres $res] result
  set argc [llength $args]
  set ntuple $result(ntuple)
  set nattr $result(nattr)
  switch -- $option {
    -status { return $result(status) }
    -conn   { return $result(conn) }
    -oid {
      if {[regexp {^INSERT +([0-9]*)} $result(complete) unused oid]} {
        return $oid
      }
      return 0
    }
    -cmdTuples {
      if {[regexp {^INSERT +[0-9]* +([0-9]*)} $result(complete) x num] \
       || [regexp {^(UPDATE|DELETE) +([0-9]*)} $result(complete) x y num]} {
        return $num
      }
      return ""
    }
    -cmdStatus { return $result(complete) }
    -numTuples { return $ntuple }
    -numAttrs  { return $nattr }
    -assign {
      if {$argc != 1} {
        error "-assign option must be followed by a variable name"
      }
      upvar $args a
      set icol 0
      foreach attr $result(attrs) {
        for {set irow 0} {$irow < $ntuple} {incr irow} {
          set a($irow,$attr) $result($irow,$icol)
        }
        incr icol
      }
    }
    -assignbyidx {
      if {$argc != 1 && $argc != 2} {
        error "-assignbyidxoption requires an array name and optionally an\
          append string"
      }
      upvar [lindex $args 0] a
      if {$argc == 2} {
        set suffix [lindex $args 1]
      } else {
        set suffix {}
      }
      set attr_first [lindex $result(attrs) 0]
      set attr_rest [lrange $result(attrs) 1 end]
      for {set irow 0} {$irow < $ntuple} {incr irow} {
        set val_first $result($irow,0)
        set icol 1
        foreach attr $attr_rest {
          set a($val_first,$attr$suffix) $result($irow,$icol)
          incr icol
        }
      }
    }
    -getTuple {
      if {$argc != 1} {
        error "-getTuple option must be followed by a tuple number"
      }
      set irow $args
      if {$irow < 0 || $irow >= $ntuple} {
        error "argument to getTuple cannot exceed number of tuples - 1"
      }
      set list {}
      for {set icol 0} {$icol < $nattr} {incr icol} {
        lappend list $result($irow,$icol)
      }
      return $list
    }
    -getNull {
      if {$argc != 1} {
        error "-getNull option must be followed by a tuple number"
      }
      set irow $args
      if {$irow < 0 || $irow >= $ntuple} {
        error "argument to getNull cannot exceed number of tuples - 1"
      }
      set list {}
      for {set icol 0} {$icol < $nattr} {incr icol} {
        lappend list [info exists result(null,$irow,$icol)]
      }
      return $list
    }
    -tupleArray {
      if {$argc != 2} {
        error "-tupleArray option must be followed by a tuple number and\
           array name"
      }
      set irow [lindex $args 0]
      if {$irow < 0 || $irow >= $ntuple} {
        error "argument to tupleArray cannot exceed number of tuples - 1"
      }
      upvar [lindex $args 1] a
      set icol 0
      foreach attr $result(attrs) {
        set a($attr) $result($irow,$icol)
        incr icol
      }
    }
    -list {
      set list {}
      for {set irow 0} {$irow < $ntuple} {incr irow} {
        for {set icol 0} {$icol < $nattr} {incr icol} {
          lappend list $result($irow,$icol)
        }
      }
      return $list
    }
    -llist {
      set list {}
      for {set irow 0} {$irow < $ntuple} {incr irow} {
        set sublist {}
        for {set icol 0} {$icol < $nattr} {incr icol} {
          lappend sublist $result($irow,$icol)
        }
        lappend list $sublist
      }
      return $list
    }
    -attributes {
      return $result(attrs)
    }
    -lAttributes {
      set list {}
      foreach attr $result(attrs) type $result(types) size $result(sizes) {
        lappend list [list $attr $type $size]
      }
      return $list
    }
    -lxAttributes {
      set list {}
      foreach attr $result(attrs) type $result(types) size $result(sizes) \
              modif $result(modifs) format $result(formats) \
              tbloid $result(tbloids) tblcol $result(tblcols) {
        lappend list [list $attr $type $size $modif $format $tbloid $tblcol]
      }
      return $list
    }
    -clear {
      unset result
    }
    -error -
    -errorField {
      if {$argc == 0} {
        return $result(error)
      }
      return [pgtcl::error_fields result $args]
    }
    default { error "Invalid option to pg_result: $option" }
  }
}

# Run a select query and iterate over the results. Uses pg_exec to run the
# query and build the result structure, but we cheat and directly use the
# result array rather than calling pg_result.
# Each returned tuple is stored into the caller's array, then the caller's
# proc is called. 
# If the caller's proc does "break", "return", or gets an error, get out
# of the processing loop. Tcl codes: 0=OK 1=error 2=return 3=break 4=continue
proc pg_select {db query var_name proc} {
  upvar $var_name var
  global errorCode errorInfo
  set res [pg_exec $db $query]
  upvar #0 pgtcl::result$res result
  if {$result(status) != "PGRES_TUPLES_OK"} {
    set msg $result(error)
    unset result
    error $msg
  }
  set code 0
  set var(.headers) $result(attrs)
  set var(.numcols) $result(nattr)
  set ntuple $result(ntuple)
  for {set irow 0} {$irow < $ntuple} {incr irow} {
    set var(.tupno) $irow
    set icol 0
    foreach attr $result(attrs) {
      set var($attr) $result($irow,$icol)
      incr icol
    }
    set code [catch {uplevel 1 $proc} s]
    if {$code != 0 && $code != 4} break
  }
  unset result var
  if {$code == 1} {
    return -code error -errorinfo $errorInfo -errorcode $errorCode $s
  } elseif {$code == 2 || $code > 4} {
    return -code $code $s
  }
  return
}

# Register a listener for backend notification, or cancel a listener.
proc pg_listen {db name {proc ""}} {
  if {$proc != ""} {
    set pgtcl::notify($db,$name) $proc
    set r [pg_exec $db "listen $name"]
    pg_result $r -clear
  } elseif {[info exists pgtcl::notify($db,$name)]} {
    unset pgtcl::notify($db,$name)
    set r [pg_exec $db "unlisten $name"]
    pg_result $r -clear
  }
}

# pg_execute: Execute a query, optionally iterating over the results.
#
# Returns the number of tuples selected or affected by the query.
# Usage: pg_execute ?options? connection query ?proc?
#   Options:  -array ArrayVar
#             -oid OidVar
# If -array is not given with a SELECT, the data is put in variables
# named by the fields. This is generally a bad idea and could be dangerous.
#
# If there is no proc body and the query return 1 or more rows, the first
# row is stored in the array or variables and we return (as does libpgtcl).
#
# Notes: Handles proc return codes of:
#    0(OK) 1(error) 2(return) 3(break) 4(continue)
#   Uses pg_exec and pg_result, but also makes direct access to the
# structures used by them.

proc pg_execute {args} {
  global errorCode errorInfo

  set usage "pg_execute ?-array arrayname?\
     ?-oid varname? connection queryString ?loop_body?"

  # Set defaults and parse command arguments:
  set use_array 0
  set set_oid 0
  set do_proc 0
  set last_option_arg {}
  set n_nonswitch_args 0
  set conn {}
  set query {}
  set proc {}
  foreach arg $args {
    if {$last_option_arg != ""} {
      if {$last_option_arg == "-array"} {
        set use_array 1
        upvar $arg data
      } elseif {$last_option_arg == "-oid"} {
        set set_oid 1
        upvar $arg oid
      } else {
        error "Unknown option $last_option_arg\n$usage"
      }
      set last_option_arg {}
    } elseif {[regexp ^- $arg]} {
      set last_option_arg $arg
    } else {
      if {[incr n_nonswitch_args] == 1} {
        set conn $arg
      } elseif {$n_nonswitch_args == 2} {
        set query $arg
      } elseif {$n_nonswitch_args == 3} {
        set do_proc 1
        set proc $arg
      } else {
        error "Wrong # of arguments\n$usage"
      }
    }
  }
  if {$last_option_arg != "" || $n_nonswitch_args < 2} {
    error "Bad arguments\n$usage"
  }

  set res [pg_exec $conn $query]
  upvar #0 pgtcl::result$res result

  # For non-SELECT query, just process oid and return value.
  # Let pg_result do the decoding.
  if {[regexp {^PGRES_(COMMAND_OK|COPY|EMPTY_QUERY)} $result(status)]} {
    if {$set_oid} {
      set oid [pg_result $res -oid]
    }
    set ntuple [pg_result $res -cmdTuples]
    pg_result $res -clear
    return $ntuple
  }

  if {$result(status) != "PGRES_TUPLES_OK"} {
    set status [list $result(status) $result(error)]
    pg_result $res -clear
    error $status
  }

  # Handle a SELECT query. This is like pg_select, except the proc is optional,
  # and the fields can go in an array or variables.
  # With no proc, store the first row only.
  set code 0
  if {!$use_array} {
    foreach attr $result(attrs) {
      upvar $attr data_$attr
    }
  }
  set ntuple $result(ntuple)
  for {set irow 0} {$irow < $ntuple} {incr irow} {
    set icol 0
    if {$use_array} {
      foreach attr $result(attrs) {
        set data($attr) $result($irow,$icol)
        incr icol
      }
    } else {
      foreach attr $result(attrs) {
        set data_$attr $result($irow,$icol)
        incr icol
      }
    }
    if {!$do_proc} break
    set code [catch {uplevel 1 $proc} s]
    if {$code != 0 && $code != 4} break
  }
  pg_result $res -clear
  if {$code == 1} {
    return -code error -errorinfo $errorInfo -errorcode $errorCode $s
  } elseif {$code == 2 || $code > 4} {
    return -code $code $s
  }
  return $ntuple
}

# Extended query protocol: Bind parameters and execute prepared statement.
# This is modelled on libpq PQexecPrepared. Use pg_exec to send a PREPARE
# first; when called externally it does not handle unnamed statements.
# This is also used internally by pg_exec_params, with an unnamed statement.
# Parameters:
#  db          Connection handle
#  stmt        Name of the prepared SQL statement to execute
#  res_formats A list describing results: B* => Binary, else Text.
#  arg_formats A list describing args: B* => Binary, else Text.
#  args        Variable number of arguments to bind to the query params.
proc pg_exec_prepared {db stmt res_formats arg_formats args} {
  set nargs [llength $args]

  if {$pgtcl::debug} { puts "+pg_exec_prepared stmt=$stmt nargs=$nargs" }
  # Calculate argument format information:
  pgtcl::crunch_fcodes $arg_formats nfcodes fcodes

  # Build the first part of the Bind message:
  set out [binary format {x a*x S S* S} \
      [encoding convertto identity $stmt] $nfcodes $fcodes $nargs]

  # Expand fcodes so there is a text/binary flag for each argument:
  if {$nfcodes == 0} {
    set all_fcodes [string repeat "0 " $nargs]
  } elseif {$nfcodes == 1} {
    set all_fcodes [string repeat "$fcodes " $nargs]
  } else {
    set all_fcodes $fcodes
  }

  # Append parameter values as { int32 length or 0 or -1 for NULL; data}
  # Note: There is no support for NULLs as parameters.
  # Encode all text parameters, leave binary parameters alone.
  foreach arg $args fcode $all_fcodes {
    if {$fcode} {
      append out [binary format I [string length $arg]] $arg
    } else {
      append out [binary format I [string length $arg]] \
          [encoding convertto identity $arg]
    }
  }

  # Append result parameter format information:
  pgtcl::crunch_fcodes $res_formats nrfcodes rfcodes
  append out [binary format {S S*} $nrfcodes $rfcodes]

  # Send it off. Don't wait for BindComplete or Error, because the protocol
  # says the BE will discard until Sync anyway.
  pgtcl::sendmsg $db B $out -noflush
  unset out
  # Send DescribePortal for the unnamed portal:
  pgtcl::sendmsg $db D "P\0" -noflush
  # Send Execute, unnamed portal, unlimited rows:
  pgtcl::sendmsg $db E "\0\0\0\0\0" -noflush
  # Send Sync
  pgtcl::sendmsg $db S {}

  # Fetch query result and return result handle:
  return [pgtcl::getresult $db 1]
}

# Extended query protocol: Parse, Bind and execute statement. This is similar
# to pg_exec_prepared, but doesn't use a pre-prepared statement, and if you
# want to pass binary parameters you must also provide the type OIDs.
# This is modelled on libpq PQexecParams.
# Parameters:
#  db          Connection handle
#  query       Query to execute, may contain parameters $1, $2, ...
#  res_formats A list describing results: B* => binary, else text
#  arg_formats A list describing args: B* => Binary, else Text.
#  arg_types   A list of type OIDs for each argument (if Binary).
#  args        Variable number of arguments to bind to the query params.

# Protocol note: Perhaps the right way to do this is to send Parse,
# then Flush, and check for ParseComplete or ErrorResponse. But then
# if there is an error, you need to send Sync and build a result structure.
# Since the backend will ignore everything after error until Sync, this
# is coded the easier way: Just send everything and let the lower-level code
# report the errors, whether on Parse or Bind or Execute.

proc pg_exec_params {db query res_formats arg_formats arg_types args} {
  if {$pgtcl::debug} { puts "+pg_exec_params query=$query" }
  # Build and send Parse message with the SQL command and list of arg types:
  set out [binary format {x a*x S} [encoding convertto identity $query] \
      [llength $arg_types]]
  foreach type $arg_types {
    append out [binary format I $type]
  }
  pgtcl::sendmsg $db P $out -noflush
  # See note above regarding not checking for ParseComplete here.
  # Proceed as with pg_exec_prepared, but with an unnamed statement:
  return [eval pg_exec_prepared {$db} {""} {$res_formats} {$arg_formats} $args]
}

# === Public procedures : Miscellaneous ===

# pg_notice_handler: Set/get handler command for Notice/Warning
# Usage: pg_notice_handler connection ?command?
# Parameters:
#   command      If supplied, the new handler command. The notice text
#                will be appended as a list element.
#                If supplied but empty, ignore notice/warnings.
#                If not supplied, just return the current value.
# Returns the previous handler command.
proc pg_notice_handler {db args} {
  set return_value $pgtcl::notice($db)
  if {[set nargs [llength $args]] == 1} {
    set pgtcl::notice($db) [lindex $args 0]
  } elseif {$nargs != 0} {
    error "Wrong # args: should be \"pg_notice_handler connection ?command?\""
  }
  return $return_value
}

# pg_configure: Configure options for PostgreSQL connections
# This is provided only for backward compatibility with earlier versions.
# Do not use.
proc pg_configure {db option args} {
  if {[set nargs [llength $args]] > 1} {
    error "Wrong # args: should be \"pg_configure connection option ?value?\""
  }
  switch -- $option {
    debug { upvar pgtcl::debug var }
    notice { upvar pgtcl::notice($db) var }
    default {
      error "Bad option \"$option\": must be one of notice, debug"
    }
  }
  set return_value $var
  if {$nargs} {
    set var [lindex $args 0]
  }
  return $return_value
}

# pg_escape_string: Escape a string for use as a quoted SQL string
# Returns the escaped string. This was added to PostgreSQL after 7.3.2
# and to libpgtcl after 1.4b3.
# Note: string map requires Tcl >= 8.1 but is faster than regsub here.
proc pg_escape_string {s} {
  return [string map {' '' \\ \\\\} $s]
}

# pg_quote: Same as pg_escape_string but returns the quotes around the
# argument too. Found this in gborg pgtcl cvs logs, not sure why.
# pg_quote instead.
proc pg_quote {s} {
  return "'[string map {' '' \\ \\\\} $s]'"
}

# pg_escape_bytea: Escape a binary string for use as a quoted SQL string.
# Returns the escaped string, which is safe for use inside single quotes
# in an SQL statement. Note back-slashes are doubled due to double parsing
# in the backend. Emulates libpq PQescapeBytea()
# See also pg_unescape_bytea, but note that these functions are not inverses.
# (I tried many versions to improve speed and this was fastest, although still
# slow. The numeric constants 92=\ and 39=` were part of that optimization.)
proc pg_escape_bytea {binstr} {
  set result {}
  binary scan $binstr c* val_list
  foreach c [split $binstr {}] val $val_list {
    if {$val == 92} {
      append result {\\\\}
    } elseif {$val == 39} {
      append result {''}
    } elseif {$val < 32 || 126 < $val} {
      append result [format {\\%03o} [expr {$val & 255}]]
    } else {
      append result $c
    }
  }
  return $result
}

# pg_unescape_bytea: Unescape a string returned from PostgreSQL as an
# escaped bytea object and return a binary string.
# Emulates libpq PQunescapeBytea().
# See also pg_escape_bytea, but note that these functions are not inverses.
# Implementation note: Iterative implementations perform very poorly.
# This method is from Benny Riefenstahl via Jerry Levan. It works much
# faster, and returns the correct data on any value produced by the
# PostgreSQL backend from converting a bytea data type to text (byteaout).
# But it does NOT work the same as PQunescapeBytea() for all values.
# For example, passing \a here returns 0x07, but PQunescapeBytea returns 'a'.
proc pg_unescape_bytea {str} {
  return [subst -nocommands -novariables $str]
}

# pg_parameter_status: Return the value of a backend parameter value.
# These are generally supplied by the backend during startup.
proc pg_parameter_status {db name} {
  upvar #0 pgtcl::param_$db param
  if {[info exists param($name)]} {
    return $param($name)
  }
  return ""
}

# pg_transaction_status: Return the current transaction status.
# Returns a string: IDLE INTRANS INERROR or UNKNOWN.
proc pg_transaction_status {db} {
  if {[info exists pgtcl::xstate($db)]} {
    switch -- $pgtcl::xstate($db) {
      I { return IDLE }
      T { return INTRANS }
      E { return INERROR }
    }
  }
  return UNKNOWN
}

# === Internal Procedure to support COPY ===

# Handle a CopyInResponse or CopyOutResponse message:
proc pgtcl::begincopy {result_name direction} {
  upvar $result_name result
  set db $result(conn)
  if {[pgtcl::get_int8 $db]} {
    error "pg_exec: COPY BINARY is not supported"
  }
  set result(status) PGRES_COPY_$direction
  # Column count and per-column formats are ignored.
  set ncol [pgtcl::get_int16 $db]
  pgtcl::skip $db [expr {2*$ncol}]
  if {$pgtcl::debug} { puts "+pg_exec begin copy $direction" }
}

# === Public procedures: COPY ===

# I/O procedures to support COPY. No longer able to just read/write the
# channel, due to the message procotol.

# Read line from COPY TO. Returns the copy line if OK, else "" on end.
# Note: The returned line does not end in a newline, so you can split it
# on tab and get a list of column values.
# At end of COPY, it takes the CopyDone only. pg_endcopy must be called to
# get the CommandComplete and ReadyForQuery messages.
proc pg_copy_read {res} {
  upvar #0 [pgtcl::checkres $res] result
  set db $result(conn)
  if {$result(status) != "PGRES_COPY_OUT"} {
    error "pg_copy_read called but connection is not doing a COPY OUT"
  }
  # Notice/Notify etc are not allowed during copy, so no loop needed.
  set c [pgtcl::readmsg $db]
  if {$pgtcl::debug} { puts "+pg_copy_read msg $c" }
  if {$c == "d"} {
    return [string trimright \
        [encoding convertfrom identity [pgtcl::get_rest $db]] "\n\r"]
  }
  if {$c == "c"} {
    return ""
  }
  # Error or invalid response.
  if {$c == "E"} {
    set result(status) PGRES_FATAL_ERROR
    set result(error) [pgtcl::get_response $db result]
    return ""
  }
  error "pg_copy_read: procotol violation, unexpected $c in copy out"
}

# Write line for COPY FROM. This must represent a single record (tuple) with
# values separated by tabs. Do not add a newline; pg_copy_write does this.
proc pg_copy_write {res line} {
  upvar #0 [pgtcl::checkres $res] result
  pgtcl::sendmsg $result(conn) d "[encoding convertto identity $line]\n"
}

# End a COPY TO/FROM. This is needed to finish up the protocol after
# reading or writing. On COPY TO, this needs to be called after
# pg_copy_read returns an empty string. On COPY FROM, this needs to
# be called after writing the last record with pg_copy_write.
# Note: Do not write or expect to read "\." anymore.
# When it returns, the result structure (res) will be updated.
proc pg_endcopy {res} {
  upvar #0 [pgtcl::checkres $res] result
  set db $result(conn)
  if {$pgtcl::debug} { puts "+pg_endcopy end $result(status)" }

  # An error might have been sent during a COPY TO, so the result
  # status will already be FATAL and should not be disturbed.
  if {$result(status) != "PGRES_FATAL_ERROR"} {
    if {$result(status) == "PGRES_COPY_IN"} {
      # Send CopyDone
      pgtcl::sendmsg $db c {}
    } elseif {$result(status) != "PGRES_COPY_OUT"} {
      error "pg_endcopy called but connection is not doing a COPY"
    }
    set result(status) PGRES_COMMAND_OK
  }

  # We're looking for CommandComplete and ReadyForQuery here, but other
  # things can happen too.
  while {[set c [pgtcl::readmsg $db]] != "Z"} {
    if {![pgtcl::common_message $c $db result]} {
      error "Unexpected reply from database: $c"
    }
  }
  set pgtcl::xstate($db) [pgtcl::get_byte $db]
  if {$pgtcl::debug} { puts "+pg_endcopy returns, st=$result(status)" }
}

# === Internal producedures for Function Call (used by Large Object) ===

# Internal procedure to lookup, cache, and return a PostgreSQL function OID.
# This assumes all connections have the same function OIDs, which might not be
# true if you connect to servers running different versions of PostgreSQL.
# Throws an error if the OID is not found by PostgreSQL.
# To call overloaded functions, argument types must be specified in parentheses
# after the function name, in the the exact same format as psql "\df".
# This is a list of types separated by a comma and one space.
# For example: fname="like(text, text)".
# The return type cannot be specified. I don't think there are any functions
# distinguished only by return type.
proc pgtcl::getfnoid {db fname} {
  variable fnoids

  if {![info exists fnoids($fname)]} {

    # Separate the function name from the (arg type list):
    if {[regexp {^([^(]*)\(([^)]*)\)$} $fname unused fcn arglist]} {
      set amatch " and oidvectortypes(proargtypes)='$arglist'"
    } else {
      set fcn $fname
      set amatch ""
    }
    pg_select $db "select oid from pg_proc where proname='$fcn' $amatch" d {
      set fnoids($fname) $d(oid)
    }
    if {![info exists fnoids($fname)]} {
      error "Unable to get OID of database function $fname"
    }
  }
  return $fnoids($fname)
}

# Internal procedure to implement PostgreSQL "fast-path" function calls.
# $fn_oid is the OID of the PostgreSQL function. See pgtcl::getfnoid.
# $result_name is the name of the variable to store the backend function
#   result into.
# $arginfo is a list of argument descriptors, each is I or S or a number.
#   I means the argument is an integer32.
#   S means the argument is a string, and its actual length is used.
#   A number means send exactly that many bytes (null-pad if needed) from
# the argument.
#   (Argument type S is passed in Ascii format code, others as Binary.)
# $arglist  is a list of arguments to the PostgreSQL function. (This
#    is actually a pass-through argument 'args' from the wrappers.)
# Throws Tcl error on error, otherwise returns size of the result
# stored into the $result_name variable.

proc pgtcl::callfn {db fn_oid result_name arginfo arglist} {
  upvar $result_name result

  set nargs [llength $arginfo]
  if {$pgtcl::debug} {
    puts "+callfn oid=$fn_oid nargs=$nargs info=$arginfo args=$arglist"
  }

  # Function call: oid nfcodes fcodes... nargs {arglen arg}... resultfcode
  set fcodes {}
  foreach k $arginfo {
    if {$k == "S"} {
      lappend fcodes 0
    } else {
      lappend fcodes 1
    }
  }
  set out [binary format {I S S* S} $fn_oid $nargs $fcodes $nargs]
  # Append each argument and its length:
  foreach k $arginfo arg $arglist {
    if {$k == "I"} {
      append out [binary format II 4 $arg]
    } elseif {$k == "S"} {
      append out [binary format I [string length $arg]] $arg
    } else {
      append out [binary format Ia$k $k $arg]
    }
  }
  # Append format code for binary result:
  append out [binary format S 1]
  pgtcl::sendmsg $db F $out

  set result {}
  set result_size 0
  # Fake up a partial result structure for pgtcl::common_message :
  set res(error) ""

  # FunctionCall response. Also handles common messages (notify, notice).
  while {[set c [pgtcl::readmsg $db]] != "Z"} {
    if {$c == "V"} {
      set result_size [pgtcl::get_int32 $db]
      if {$result_size > 0} {
        set result [pgtcl::get_bytes $db $result_size]
      } else {
        set result ""
      }
    } elseif {![pgtcl::common_message $c $db res]} {
      error "Unexpected reply from database: $c"
    }
  }
  set pgtcl::xstate($db) [pgtcl::get_byte $db]
  if {$res(error) != ""} {
    error $res(error)
  }
  return $result_size
}

# === Public prodedures: Function Call ===

# Public interface to pgtcl::callfn.
proc pg_callfn {db fname result_name arginfo args} {
  upvar $result_name result
  return [pgtcl::callfn $db [pgtcl::getfnoid $db $fname] result $arginfo $args]
}

# Public, simplified interface to pgtcl::callfn when an int32 return value is
# expected. Returns the backend function return value.
proc pg_callfn_int {db fname arginfo args} {
  set n [pgtcl::callfn $db [pgtcl::getfnoid $db $fname] result $arginfo $args]
  if {$n != 4} { 
    error "Unexpected response size ($result_size) to pg function call $fname"
  }
  binary scan $result I val
  return $val
}

# === Internal procedure to support Large Object ===

# Convert a LO mode string into the value of the constants used by libpq.
# Note: libpgtcl uses a mode like INV_READ|INV_WRITE for lo_creat, but
# r, w, or rw for lo_open (which it translates to INV_READ|INV_WRITE).
# This seems like a mistake. The code here accepts either form for either.
proc pgtcl::lomode {mode} {
  set imode 0
  if {[string match -nocase *INV_* $mode]} {
    if {[string match -nocase *INV_READ* $mode]} {
      set imode 0x40000
    }
    if {[string match -nocase *INV_WRITE* $mode]} {
      set imode [expr {$imode + 0x20000}]
    }
  } else {
    if {[string match -nocase *r* $mode]} {
      set imode 0x40000
    }
    if {[string match -nocase *w* $mode]} {
      set imode [expr {$imode + 0x20000}]
    }
  }
  if {$imode == 0} {
    error "Invalid large object mode $mode"
  }
  return $imode
}

# === Public prodedures: Large Object ===

# Create large object and return OID.
# See note regarding mode above at pgtcl::lomode.
proc pg_lo_creat {db mode} {
  if {[catch {pg_callfn_int $db lo_creat I [pgtcl::lomode $mode]} result]} {
    error "Large Object create failed\n$result"
  }
  if {$result == -1} {
    error "Large Object create failed"
  }
  return $result
}

# Open large object and return large object file descriptor.
# See note regarding mode above at pgtcl::lomode.
proc pg_lo_open {db loid mode} {
  if {[catch {pg_callfn_int $db lo_open "I I" $loid [pgtcl::lomode $mode]} \
      result]} {
    error "Large Object open failed\n$result"
  }
  if {$result == -1} {
    error "Large Object open failed"
  }
  return $result
}

# Close large object file descriptor.
proc pg_lo_close {db lofd} {
  if {[catch {pg_callfn_int $db lo_close I $lofd} result]} {
    error "Large Object close failed\n$result"
  }
  return $result
}

# Delete large object:
proc pg_lo_unlink {db loid} {
  if {[catch {pg_callfn_int $db lo_unlink I $loid} result]} {
    error "Large Object unlink failed\n$result"
  }
  return $result
}

# Read from large object.
# Note: The original PostgreSQL documentation says it returns -1 on error,
# which is a bad idea since you can't get to the error message. But it's
# probably too late to change it, so we remain bug compatible.
proc pg_lo_read {db lofd buf_name maxlen} {
  upvar $buf_name buf
  if {[catch {pg_callfn $db loread buf "I I" $lofd $maxlen} result]} {
    return -1
  }
  return $result
}

# Write to large object. At most $len bytes are written.
# See note above on pg_lo_read error return.
proc pg_lo_write {db lofd buf len} {
  if {[set buflen [string length $buf]] < $len} {
    set len $buflen
  }
  if {[catch {pg_callfn_int $db lowrite "I $len" $lofd $buf} result]} {
    return -1
  }
  return $result
}

# Seek to offset inside large object:
proc pg_lo_lseek {db lofd offset whence} {
  switch $whence {
    SEEK_SET { set iwhence 0 }
    SEEK_CUR { set iwhence 1 }
    SEEK_END { set iwhence 2 }
    default { error "'whence' must be SEEK_SET, SEEK_CUR, or SEEK_END" }
  }
  if {[catch {pg_callfn_int $db lo_lseek "I I I" $lofd $offset $iwhence} \
      result]} {
    error "Large Object seek failed\n$result"
  }
  return $result
}

# Return location of file offset in large object:
proc pg_lo_tell {db lofd} {
  if {[catch {pg_callfn_int $db lo_tell I $lofd} result]} {
    error "Large Object tell offset failed\n$result"
  }
  return $result
}

# Import large object. Wrapper for lo_creat, lo_open, lo_write.
# Returns Large Object OID, which should be stored in a table somewhere.
proc pg_lo_import {db filename} {
  if {[catch {open $filename} f]} {
    error "Large object import of $filename failed\n$f"
  }
  fconfigure $f -translation binary
  set loid [pg_lo_creat $db INV_READ|INV_WRITE]
  set lofd [pg_lo_open $db $loid w]
  while {1} {
    set buf [read $f 32768]
    if {[set len [string length $buf]] == 0} break
    if {[pg_lo_write $db $lofd $buf $len] != $len} {
      error "Large Object import failed to write $len bytes"
    }
  }
  pg_lo_close $db $lofd
  close $f
  return $loid
}

# Export large object. Wrapper for lo_open, lo_read.
proc pg_lo_export {db loid filename} {
  if {[catch {open $filename w} f]} {
    error "Large object export to $filename failed\n$f"
  }
  fconfigure $f -translation binary
  if {[catch {pg_lo_open $db $loid r} lofd]} {
    error "Large Object export to $filename failed\n$lofd"
  }
  while {[set len [pg_lo_read $db $lofd buf 32768]] > 0} {
    puts -nonewline $f $buf
  }
  pg_lo_close $db $lofd
  close $f
}

# === MD5 Checksum procedures for password authentication ===

# Coded in Tcl by ljb <lbayuk@mindspring.com>, using these sources:
#  RFC1321
#  PostgreSQL: src/backend/libpq/md5.c
# If you want a better/faster MD5 implementation, see tcllib.

namespace eval md5 { }

# Round 1 helper, e.g.:
#   a = b + ROT_LEFT((a + F(b, c, d) + X[0] + 0xd76aa478), 7)
#       p1            p2    p1 p3 p4   p5        p6        p7
# Where F(x,y,z) = (x & y) | (~x & z)
#
proc md5::round1 {p1 p2 p3 p4 p5 p6 p7} {
  set r [expr {$p2 + ($p1 & $p3 | ~$p1 & $p4) + $p5 + $p6}]
  return [expr {$p1 + ($r << $p7 | (($r >> (32 - $p7)) & ((1 << $p7) - 1)))}]
}

# Round 2 helper, e.g.:
#   a = b + ROT_LEFT((a + G(b, c, d) + X[1] + 0xf61e2562), 5)
#       p1            p2    p1 p3 p4   p5        p6        p7
# Where G(x,y,z) = (x & z) | (y & ~z)
#
proc md5::round2 {p1 p2 p3 p4 p5 p6 p7} {
  set r [expr {$p2 + ($p1 & $p4 | $p3 & ~$p4) + $p5 + $p6}]
  return [expr {$p1 + ($r << $p7 | (($r >> (32 - $p7)) & ((1 << $p7) - 1)))}]
}

# Round 3 helper, e.g.:
#   a = b + ROT_LEFT((a + H(b, c, d) + X[5] + 0xfffa3942), 4)
#       p1            p2    p1 p3 p4   p5     p6           p7
# Where H(x, y, z) = x ^ y ^ z
#
proc md5::round3 {p1 p2 p3 p4 p5 p6 p7} {
  set r [expr {$p2 + ($p1 ^ $p3 ^ $p4) + $p5 + $p6}]
  return [expr {$p1 + ($r << $p7 | (($r >> (32 - $p7)) & ((1 << $p7) - 1)))}]
}

# Round 4 helper, e.g.:
#   a = b + ROT_LEFT((a + I(b, c, d) + X[0] + 0xf4292244), 6)
#       p1            p2    p1 p3 p4   p5     p6           p7
# Where I(x, y, z) = y ^ (x | ~z)
#
proc md5::round4 {p1 p2 p3 p4 p5 p6 p7} {
  set r [expr {$p2 + ($p3 ^ ($p1 | ~$p4)) + $p5 + $p6}]
  return [expr {$p1 + ($r << $p7 | (($r >> (32 - $p7)) & ((1 << $p7) - 1)))}]
}

# Do one set of rounds. Updates $state(0:3) with results from $x(0:16).
proc md5::round {x_name state_name} {
  upvar $x_name x $state_name state
  set a $state(0)
  set b $state(1)
  set c $state(2)
  set d $state(3)

  # Round 1, steps 1-16
  set a [round1 $b $a $c $d $x(0)  0xd76aa478  7]
  set d [round1 $a $d $b $c $x(1)  0xe8c7b756 12]
  set c [round1 $d $c $a $b $x(2)  0x242070db 17]
  set b [round1 $c $b $d $a $x(3)  0xc1bdceee 22]
  set a [round1 $b $a $c $d $x(4)  0xf57c0faf  7]
  set d [round1 $a $d $b $c $x(5)  0x4787c62a 12]
  set c [round1 $d $c $a $b $x(6)  0xa8304613 17]
  set b [round1 $c $b $d $a $x(7)  0xfd469501 22]
  set a [round1 $b $a $c $d $x(8)  0x698098d8  7]
  set d [round1 $a $d $b $c $x(9)  0x8b44f7af 12]
  set c [round1 $d $c $a $b $x(10) 0xffff5bb1 17]
  set b [round1 $c $b $d $a $x(11) 0x895cd7be 22]
  set a [round1 $b $a $c $d $x(12) 0x6b901122  7]
  set d [round1 $a $d $b $c $x(13) 0xfd987193 12]
  set c [round1 $d $c $a $b $x(14) 0xa679438e 17]
  set b [round1 $c $b $d $a $x(15) 0x49b40821 22]

  # Round 2, steps 17-32
  set a [round2 $b $a $c $d $x(1)  0xf61e2562  5]
  set d [round2 $a $d $b $c $x(6)  0xc040b340  9]
  set c [round2 $d $c $a $b $x(11) 0x265e5a51 14]
  set b [round2 $c $b $d $a $x(0)  0xe9b6c7aa 20]
  set a [round2 $b $a $c $d $x(5)  0xd62f105d  5]
  set d [round2 $a $d $b $c $x(10) 0x02441453  9]
  set c [round2 $d $c $a $b $x(15) 0xd8a1e681 14]
  set b [round2 $c $b $d $a $x(4)  0xe7d3fbc8 20]
  set a [round2 $b $a $c $d $x(9)  0x21e1cde6  5]
  set d [round2 $a $d $b $c $x(14) 0xc33707d6  9]
  set c [round2 $d $c $a $b $x(3)  0xf4d50d87 14]
  set b [round2 $c $b $d $a $x(8)  0x455a14ed 20]
  set a [round2 $b $a $c $d $x(13) 0xa9e3e905  5]
  set d [round2 $a $d $b $c $x(2)  0xfcefa3f8  9]
  set c [round2 $d $c $a $b $x(7)  0x676f02d9 14]
  set b [round2 $c $b $d $a $x(12) 0x8d2a4c8a 20]

  # Round 3, steps 33-48
  set a [round3 $b $a $c $d $x(5)  0xfffa3942  4]
  set d [round3 $a $d $b $c $x(8)  0x8771f681 11]
  set c [round3 $d $c $a $b $x(11) 0x6d9d6122 16]
  set b [round3 $c $b $d $a $x(14) 0xfde5380c 23]
  set a [round3 $b $a $c $d $x(1)  0xa4beea44  4]
  set d [round3 $a $d $b $c $x(4)  0x4bdecfa9 11]
  set c [round3 $d $c $a $b $x(7)  0xf6bb4b60 16]
  set b [round3 $c $b $d $a $x(10) 0xbebfbc70 23]
  set a [round3 $b $a $c $d $x(13) 0x289b7ec6  4]
  set d [round3 $a $d $b $c $x(0)  0xeaa127fa 11]
  set c [round3 $d $c $a $b $x(3)  0xd4ef3085 16]
  set b [round3 $c $b $d $a $x(6)  0x04881d05 23]
  set a [round3 $b $a $c $d $x(9)  0xd9d4d039  4]
  set d [round3 $a $d $b $c $x(12) 0xe6db99e5 11]
  set c [round3 $d $c $a $b $x(15) 0x1fa27cf8 16]
  set b [round3 $c $b $d $a $x(2)  0xc4ac5665 23]

  # Round 4, steps 49-64
  set a [round4 $b $a $c $d $x(0)  0xf4292244  6]
  set d [round4 $a $d $b $c $x(7)  0x432aff97 10]
  set c [round4 $d $c $a $b $x(14) 0xab9423a7 15]
  set b [round4 $c $b $d $a $x(5)  0xfc93a039 21]
  set a [round4 $b $a $c $d $x(12) 0x655b59c3  6]
  set d [round4 $a $d $b $c $x(3)  0x8f0ccc92 10]
  set c [round4 $d $c $a $b $x(10) 0xffeff47d 15]
  set b [round4 $c $b $d $a $x(1)  0x85845dd1 21]
  set a [round4 $b $a $c $d $x(8)  0x6fa87e4f  6]
  set d [round4 $a $d $b $c $x(15) 0xfe2ce6e0 10]
  set c [round4 $d $c $a $b $x(6)  0xa3014314 15]
  set b [round4 $c $b $d $a $x(13) 0x4e0811a1 21]
  set a [round4 $b $a $c $d $x(4)  0xf7537e82  6]
  set d [round4 $a $d $b $c $x(11) 0xbd3af235 10]
  set c [round4 $d $c $a $b $x(2)  0x2ad7d2bb 15]
  set b [round4 $c $b $d $a $x(9)  0xeb86d391 21]

  incr state(0) $a
  incr state(1) $b
  incr state(2) $c
  incr state(3) $d
}

# Pad out buffer per MD5 spec:
proc md5::pad {buf_name} {
  upvar $buf_name buf

  # Length in bytes:
  set len [string length $buf]
  # Length in bits as 2 32 bit words:
  set len64hi [expr {$len >> 29 & 7}]
  set len64lo [expr {$len << 3}]

  # Append 1 special byte, then append 0 or more 0 bytes until
  # (length in bytes % 64) == 56
  set pad [expr {64 - ($len + 8) % 64}]
  append buf [binary format a$pad "\x80"]

  # Append the length in bits as a 64 bit value, low bytes first.
  append buf [binary format i1i1 $len64lo $len64hi]

}

# Calculate MD5 Digest over a string, return as 32 hex digit string.
proc md5::digest {buf} {
  # This is 0123456789abcdeffedcba9876543210 in byte-swapped order:
  set state(0) 0x67452301
  set state(1) 0xEFCDAB89
  set state(2) 0x98BADCFE
  set state(3) 0x10325476

  # Pad buffer per RFC to exact multiple of 64 bytes.
  pad buf

  # Calculate digest in 64 byte chunks:
  set nwords 0
  set nbytes 0
  set word 0
  binary scan $buf c* bytes
  # Unclear, but the data seems to get byte swapped here.
  foreach c $bytes {
    set word [expr {$c << 24 | ($word >> 8 & 0xffffff) }]
    if {[incr nbytes] == 4} {
      set nbytes 0
      set x($nwords) $word
      set word 0
      if {[incr nwords] == 16} {
        round x state
        set nwords 0
      }
    }
  }

  # Result is state(0:3), but each word is taken low byte first.
  set result {}
  for {set i 0} {$i <= 3} {incr i} {
    set w $state($i)
    append result [format %02x%02x%02x%02x \
             [expr {$w & 255}] \
             [expr {$w >> 8 & 255}] \
             [expr {$w >> 16 & 255}] \
             [expr {$w >> 24 & 255}]]
  }
  return $result
}
package provide pgintcl $pgtcl::version
