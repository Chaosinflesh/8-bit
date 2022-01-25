; This list checks basic functionality.  Once complete, it sits in an
; infinite loop until stopped by the Timer interrupt.

; All data is in hex.
.int     a      7
.int     b      65
.int     $fw     FC8
.text    s      The remainder of this line is a string.
.raw     r      68656C6C6F

:program
    J   start   ; Goto the start of the program.
_0x05:
    HLT         ; This will be triggered by the interrupt.
:start
    SEL 7
    LDN %fw
    LDR 7
    SEL 1
    LDN b
    CP  3
    SEL 2
    LDN a
    SEL 4
    LDN r
    SEL 5
    LDN s
    SEL 0
    CLR
    ADD 1       ; 101
    MUL 2       ; 707
    STR 7       ; Write out the result.
    SEL 3
    NEG
    SEL 0
    ADD 3       ; 606
    STR 7
    DIV 1       ; 6
    STR 7
    SHL 1       ; 12
    STR 7
    COM         ; -13
    STR 7
    SHR 1       ; -7
    STR 7
    NEG         ; 7
    STR 7
:loop
    J   loop
