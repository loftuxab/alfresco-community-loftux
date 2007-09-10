proc CreateWindow.6C5D6843-62DA-44BB-AC96-04FCEA01778A {wizard id} {
    set base  [$wizard widget get $id]
    set frame $base.titleframe

    grid rowconfigure    $base 3 -weight 1
    grid columnconfigure $base 0 -weight 1

    frame $frame -bd 0 -relief flat -background white
    grid  $frame -row 0 -column 0 -sticky nsew

    grid rowconfigure    $frame 1 -weight 1
    grid columnconfigure $frame 0 -weight 1

    Label $frame.title -background white -anchor nw -justify left -autowrap 1  -font TkCaptionFont -textvariable [$wizard variable $id -text1]
    grid $frame.title -row 0 -column 0 -sticky new -padx 5 -pady 5
    $id widget set Title -widget $frame.title

    Label $frame.subtitle -background white -anchor nw -autowrap 1  -justify left -textvariable [$wizard variable $id -text2]
    grid $frame.subtitle -row 1 -column 0 -sticky new -padx [list 20 5]
    $id widget set Subtitle -widget $frame.subtitle

    label $frame.icon -borderwidth 0 -background white -anchor c
    grid  $frame.icon -row 0 -column 1 -rowspan 2
    $id widget set Icon -widget $frame.icon -type image

    Separator $base.separator -relief groove -orient horizontal
    grid $base.separator -row 1 -column 0 -sticky ew 

    Label $base.caption -anchor nw -justify left -autowrap 1  -textvariable [$wizard variable $id -text3]
    grid $base.caption -row 2 -sticky nsew -padx 8 -pady [list 8 4]
    $id widget set Caption -widget $base.caption

    frame $base.clientarea
    grid  $base.clientarea -row 3 -sticky nsew -padx 8 -pady 4
    $id widget set ClientArea -widget $base.clientarea -type frame

    grid columnconfigure $base.clientarea 0 -weight 1

    ttk::radiobutton $base.clientarea.hsql -text "HSQLDB (Evaluation purposes only)" -value "hsql"  -variable ::info(ALF_DB)
    grid $base.clientarea.hsql -row 0 -column 0 -sticky nw -padx 20 -pady 2

    ttk::radiobutton $base.clientarea.mysql -text "MySQL" -value "mysql"  -variable ::info(ALF_DB)
    grid $base.clientarea.mysql -row 1 -column 0 -sticky nw -padx 20 -pady 2

    ttk::radiobutton $base.clientarea.oracle -text "Oracle" -value "oracle"  -variable ::info(ALF_DB)
    grid $base.clientarea.oracle -row 2 -column 0 -sticky nw -padx 20 -pady 2

    ttk::radiobutton $base.clientarea.postgres -text "PostgreSql" -value "postgres"  -variable ::info(ALF_DB)
    grid $base.clientarea.postgres -row 3 -column 0 -sticky nw -padx 20 -pady 2

    ttk::radiobutton $base.clientarea.sqlserver -text "SQL Server" -value "sqlserver"  -variable ::info(ALF_DB)
    grid $base.clientarea.sqlserver -row 4 -column 0 -sticky nw -padx 20 -pady 2

    ttk::radiobutton $base.clientarea.sybase -text "Sybase" -value "sybase"  -variable ::info(ALF_DB)
    grid $base.clientarea.sybase -row 5 -column 0 -sticky nw -padx 20 -pady 2

    Label $base.message -anchor nw -justify left -autowrap 1  -textvariable [$wizard variable $id -text4]
    grid $base.message -row 4 -sticky nsew -padx 8 -pady [list 4 8]
    $id widget set Message -widget $base.message
}

