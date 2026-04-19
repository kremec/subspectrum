package com.subbyte.subspectrum.base.annotations

object SystemVariableRegistry {
    val variables: List<MemoryPointAnnotation> = listOf(
        MemoryPointAnnotation(
            address = 0x5C00u,
            label = "KSTATE",
            description = "Used in reading the keyboard.",
        ),
        MemoryPointAnnotation(
            address = 0x5C08u,
            label = "LAST-K",
            description = "Stores newly pressed key.",
        ),
        MemoryPointAnnotation(
            address = 0x5C09u,
            label = "REPDEL",
            description = "Time (in 50ths of a second in 60ths of a second in N. America) that a key must be held down before it repeats. This starts off at 35, but you can POKE in other values.",
        ),
        MemoryPointAnnotation(
            address = 0x5C0Au,
            label = "REPPER",
            description = "Delay (in 50ths of a second in 60ths of a second in N. America) between successive repeats of a key held down: initially 5.",
        ),
        MemoryPointAnnotation(
            address = 0x5C0Bu,
            label = "DEFADD",
            description = "Address of arguments of user defined function if one is being evaluated; otherwise 0.",
        ),
        MemoryPointAnnotation(
            address = 0x5C0Du,
            label = "K-DATA",
            description = "Stores 2nd byte of colour controls entered from keyboard .",
        ),
        MemoryPointAnnotation(
            address = 0x5C0Eu,
            label = "TVDATA",
            description = "Stores bytes of coiour, AT and TAB controls going to television.",
        ),
        MemoryPointAnnotation(
            address = 0x5C10u,
            label = "STRMS",
            description = "Addresses of channels attached to streams.",
        ),
        MemoryPointAnnotation(
            address = 0x5C36u,
            label = "CHARS",
            description = "256 less than address of character set (which starts with space and carries on to the copyright symbol). Normally in ROM, but you can set up your own in RAM and make CHARS point to it.",
        ),
        MemoryPointAnnotation(
            address = 0x5C38u,
            label = "RASP",
            description = "Length of warning buzz.",
        ),
        MemoryPointAnnotation(
            address = 0x5C39u,
            label = "PIP",
            description = "Length of keyboard click.",
        ),
        MemoryPointAnnotation(
            address = 0x5C3Au,
            label = "ERR-NR",
            description = "1 less than the report code. Starts off at 255 (for 1) so PEEK 23610 gives 255.",
        ),
        MemoryPointAnnotation(
            address = 0x5C3Bu,
            label = "FLAGS",
            description = "Various flags to control the BASIC system.",
        ),
        MemoryPointAnnotation(
            address = 0x5C3Cu,
            label = "TV-FLAG",
            description = "Flags associated with the television.",
        ),
        MemoryPointAnnotation(
            address = 0x5C3Du,
            label = "ERR-SP",
            description = "Address of item on machine stack to be used as error return.",
        ),
        MemoryPointAnnotation(
            address = 0x5C3Fu,
            label = "LIST-SP",
            description = "Address of return address from automatic listing.",
        ),
        MemoryPointAnnotation(
            address = 0x5C41u,
            label = "MODE",
            description = "Specifies K, L, C. E or G cursor.",
        ),
        MemoryPointAnnotation(
            address = 0x5C42u,
            label = "NEWPPC",
            description = "Line to be jumped to.",
        ),
        MemoryPointAnnotation(
            address = 0x5C44u,
            label = "NSPPC",
            description = "Statement number in line to be jumped to. Poking first NEWPPC and then NSPPC forces a jump to a specified statement in a line.",
        ),
        MemoryPointAnnotation(
            address = 0x5C45u,
            label = "PPC",
            description = "Line number of statement currently being executed.",
        ),
        MemoryPointAnnotation(
            address = 0x5C47u,
            label = "SUBPPC",
            description = "Number within line of statement being executed.",
        ),
        MemoryPointAnnotation(
            address = 0x5C48u,
            label = "BORDCR",
            description = "Border colour * 8; also contains the attributes normally used for the lower half of the screen.",
        ),
        MemoryPointAnnotation(
            address = 0x5C49u,
            label = "E-PPC",
            description = "Number of current line (with program cursor).",
        ),
        MemoryPointAnnotation(
            address = 0x5C4Bu,
            label = "VARS",
            description = "Address of variables.",
        ),
        MemoryPointAnnotation(
            address = 0x5C4Du,
            label = "DEST",
            description = "Address of variable in assignment.",
        ),
        MemoryPointAnnotation(
            address = 0x5C4Fu,
            label = "CHANS",
            description = "Address of channel data.",
        ),
        MemoryPointAnnotation(
            address = 0x5C51u,
            label = "CURCHL",
            description = "Address of information currently being used for input and output.",
        ),
        MemoryPointAnnotation(
            address = 0x5C53u,
            label = "PROG",
            description = "Address of BASIC program.",
        ),
        MemoryPointAnnotation(
            address = 0x5C55u,
            label = "NXTLIN",
            description = "Address of next line in program.",
        ),
        MemoryPointAnnotation(
            address = 0x5C57u,
            label = "DATADD",
            description = "Address of terminator of last DATA item.",
        ),
        MemoryPointAnnotation(
            address = 0x5C59u,
            label = "E-LINE",
            description = "Address of command being typed in.",
        ),
        MemoryPointAnnotation(
            address = 0x5C5Bu,
            label = "K-CUR",
            description = "Address of cursor.",
        ),
        MemoryPointAnnotation(
            address = 0x5C5Du,
            label = "CH-ADD",
            description = "Address of the next character to be interpreted: the character after the argument of PEEK, or the NEWLINE at the end of a POKE statement.",
        ),
        MemoryPointAnnotation(
            address = 0x5C5Fu,
            label = "X-PTR",
            description = "Address of the character after the ? marker.",
        ),
        MemoryPointAnnotation(
            address = 0x5C61u,
            label = "WORKSP",
            description = "Address of temporary work space.",
        ),
        MemoryPointAnnotation(
            address = 0x5C63u,
            label = "STKBOT",
            description = "Address of bottom of calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x5C65u,
            label = "STKEND",
            description = "Address of start of spare space.",
        ),
        MemoryPointAnnotation(
            address = 0x5C67u,
            label = "BREG",
            description = "Calculator's b register.",
        ),
        MemoryPointAnnotation(
            address = 0x5C68u,
            label = "MEM",
            description = "Address of area used for calculator's memory. (Usually MEMBOT, but not always.)",
        ),
        MemoryPointAnnotation(
            address = 0x5C6Au,
            label = "FLAGS2",
            description = "More flags.",
        ),
        MemoryPointAnnotation(
            address = 0x5C6Bu,
            label = "DF-SZ",
            description = "The number of lines (including one blank line) in the lower part of the screen.",
        ),
        MemoryPointAnnotation(
            address = 0x5C6Cu,
            label = "S-TOP",
            description = "The number of the top program line in automatic listings.",
        ),
        MemoryPointAnnotation(
            address = 0x5C6Eu,
            label = "OLDPPC",
            description = "Line number to which CONTINUE jumps.",
        ),
        MemoryPointAnnotation(
            address = 0x5C70u,
            label = "OSPCC",
            description = "Number within line of statement to which CONTINUE jumps.",
        ),
        MemoryPointAnnotation(
            address = 0x5C71u,
            label = "FLAGX",
            description = "Various flags.",
        ),
        MemoryPointAnnotation(
            address = 0x5C72u,
            label = "STRLEN",
            description = "Length of string type destination in assignment.",
        ),
        MemoryPointAnnotation(
            address = 0x5C74u,
            label = "T-ADDR",
            description = "Address of next item in syntax table (very unlikely to be useful).",
        ),
        MemoryPointAnnotation(
            address = 0x5C76u,
            label = "SEED",
            description = "The seed for RND. This is the variable that is set by RANDOMIZE.",
        ),
        MemoryPointAnnotation(
            address = 0x5C78u,
            label = "FRAMES",
            description = "3 byte (least significant first), frame counter. Incremented every 20ms. See Chapter 18.",
        ),
        MemoryPointAnnotation(
            address = 0x5C7Bu,
            label = "UDG",
            description = "Address of 1st user defined graphic You can change this for instance to save space by having fewer user defined graphics.",
        ),
        MemoryPointAnnotation(
            address = 0x5C7Du,
            label = "COORDS",
            description = "x-coordinate of last point plotted. y-coordinate of last point plotted.",
        ),
        MemoryPointAnnotation(
            address = 0x5C7Fu,
            label = "P-POSN",
            description = "33 column number of printer position",
        ),
        MemoryPointAnnotation(
            address = 0x5C80u,
            label = "PR-CC",
            description = "Full address of next position for LPRINT to print at (in ZX printer buffer). Legal values 5B00 - 5B1F. [Not used in 128K mode or when certain peripherals are attached]",
        ),
        MemoryPointAnnotation(
            address = 0x5C82u,
            label = "ECHO-E",
            description = "33 column number and 24 line number (in lower half) of end of input buffer.",
        ),
        MemoryPointAnnotation(
            address = 0x5C84u,
            label = "DF-CC",
            description = "Address in display file of PRINT position.",
        ),
        MemoryPointAnnotation(
            address = 0x5C86u,
            label = "DF-CCL",
            description = "Like DF CC for lower part of screen.",
        ),
        MemoryPointAnnotation(
            address = 0x5C88u,
            label = "S-POSN",
            description = "33 column number for PRINT position 24 line number for PRINT position.",
        ),
        MemoryPointAnnotation(
            address = 0x5C8Au,
            label = "S-POSNL",
            description = "Like S POSN for lower part",
        ),
        MemoryPointAnnotation(
            address = 0x5C8Cu,
            label = "SCR-CT",
            description = "Counts scrolls: it is always 1 more than the number of scrolls that will be done before stopping with scroll? If you keep poking this with a number bigger than 1 (say 255), the screen will scroll on and on without asking you.",
        ),
        MemoryPointAnnotation(
            address = 0x5C8Du,
            label = "ATTR-P",
            description = "Permanent current colours, etc (as set up by colour statements).",
        ),
        MemoryPointAnnotation(
            address = 0x5C8Eu,
            label = "MASK-P",
            description = "Used for transparent colours, etc. Any bit that is 1 shows that the corresponding attribute bit is taken not from ATTR P, but from what is already on the screen.",
        ),
        MemoryPointAnnotation(
            address = 0x5C8Fu,
            label = "ATTR-T",
            description = "Temporary current colours, etc (as set up by colour items).",
        ),
        MemoryPointAnnotation(
            address = 0x5C90u,
            label = "MASK-T",
            description = "Like MASK P, but temporary.",
        ),
        MemoryPointAnnotation(
            address = 0x5C91u,
            label = "P-FLAG",
            description = "More flags.",
        ),
        MemoryPointAnnotation(
            address = 0x5C92u,
            label = "MEMBOT",
            description = "Calculator's memory area; used to store numbers that cannot conveniently be put on the calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x5CB0u,
            label = "NMIADD",
            description = "This is the address of a user supplied NMI address which is read by the standard ROM when a peripheral activates the NMI. Probably intentionally disabled so that the effect is to perform a reset if both locations hold zero, but do nothing if the locations hold a non-zero value. Interface 1's with serial number greater than 87315 will initialize these locations to 0 and 80 to allow the RS232 \"T\" channel to use a variable line width. 23728 is the current print position and 23729 the width - default 80.",
        ),
        MemoryPointAnnotation(
            address = 0x5CB2u,
            label = "RAMTOP",
            description = "Address of last byte of BASIC system area.",
        ),
        MemoryPointAnnotation(
            address = 0x5CB4u,
            label = "P-RAMT",
            description = "Address of last byte of physical RAM.",
        ),
        MemoryPointAnnotation(
            address = 0x5CB6u,
            label = "Channel information",
            description = "For each channel the output routine address comes before the input routine address and the channel's code.",
        )
    )
}
