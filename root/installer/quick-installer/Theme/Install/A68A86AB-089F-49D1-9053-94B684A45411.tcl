proc CreateWindow.A68A86AB-089F-49D1-9053-94B684A45411 {wizard id} {
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

    ttk::label $base.clientarea.label3 -text "Server:" -justify right
    grid $base.clientarea.label3 -row 2 -column 0 -sticky e

    ttk::entry $base.clientarea.entry3 -textvariable ::info(DB_HOST)
    grid $base.clientarea.entry3 -row 2 -column 1 -sticky e

    ttk::label $base.clientarea.label4 -text "Port:" -justify right
    grid $base.clientarea.label4 -row 3 -column 0 -sticky e
    
    ttk::entry $base.clientarea.entry4 -textvariable ::info(DB_PORT)
    grid $base.clientarea.entry4 -row 3 -column 1  -sticky e
    
    ttk::label $base.clientarea.label6 -text "Database name:" -justify right
    grid $base.clientarea.label6 -row 4 -column 0 -sticky e

    ttk::entry $base.clientarea.entry5 -textvariable ::info(DB_NAME)
    grid $base.clientarea.entry5 -row 4 -column 1 -sticky e
    
    ttk::label $base.clientarea.label7 -text "Database user:" -justify right
    grid $base.clientarea.label7 -row 5 -column 0 -sticky e

    ttk::entry $base.clientarea.entry6 -textvariable ::info(DB_USER)
    grid $base.clientarea.entry6 -row 5 -column 1 -sticky e
    
    ttk::label $base.clientarea.label8 -text "Database password:" -justify right
    grid $base.clientarea.label8 -row 6 -column 0 -sticky e

    ttk::entry $base.clientarea.entry7 -textvariable ::info(DB_USERPWD)
    grid $base.clientarea.entry7 -row 6 -column 1 -sticky e

    Label $base.message -anchor nw -justify left -autowrap 1  -textvariable [$wizard variable $id -text4]
    grid $base.message -row 4 -sticky nsew -padx 8 -pady [list 4 8]
    $id widget set Message -widget $base.message
}

