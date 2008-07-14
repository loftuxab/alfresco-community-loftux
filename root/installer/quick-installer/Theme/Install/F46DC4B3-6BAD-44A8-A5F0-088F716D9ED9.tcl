proc CreateWindow.F46DC4B3-6BAD-44A8-A5F0-088F716D9ED9 {wizard id} {
    set base [$wizard widget get $id]

    grid rowconfigure    $base 1 -weight 1
    grid columnconfigure $base 1 -weight 1

    label $base.image -borderwidth 0 -background white
    grid  $base.image -row 0 -column 0 -rowspan 2 -sticky nw
    $id widget set Image -type image -widget $base.image

    Label $base.title -height 3 -bg white -font TkCaptionFont  -autowrap 1 -anchor nw -justify left
    grid $base.title -row 0 -column 1 -sticky ew -padx 20 -pady [list 20 10]
    $id widget set Caption -type text -widget $base.title

    Label $base.message -bg white -autowrap 1 -anchor nw -justify left
    grid  $base.message -row 1 -column 1 -sticky news -padx 20
    $id widget set Message -type text -widget $base.message

    set url "http://java.sun.com/"
    Label $base.url -foreground blue -cursor hand2  -text "Click here to download Java (JDK)" -justify center
    grid $base.url -row 2 -column 1 -sticky news
    bind $base.url <1> [list LaunchURL $url]
    label $base.gap -borderwidth 0 -background white
    grid $base.gap -row 3 -column 0 -columnspan 2 -sticky news
    Separator $base.sep -orient horizontal
    grid $base.sep -row 4 -column 0 -columnspan 2 -sticky ew
}

