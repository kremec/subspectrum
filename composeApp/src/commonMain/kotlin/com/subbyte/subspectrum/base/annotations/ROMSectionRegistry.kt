package com.subbyte.subspectrum.base.annotations

object ROMSectionRegistry {
    val sections: List<MemoryPointAnnotation> = listOf(
        MemoryPointAnnotation(
            address = 0x0000u,
            label = "THE 'START'",
            description = "The maskable interrupt is disabled and the DE register pair set to hold the 'top of possible RAM'.",
        ),
        MemoryPointAnnotation(
            address = 0x0008u,
            label = "THE 'ERROR' RESTART",
            description = "The error pointer is made to point to the position of the error.",
        ),
        MemoryPointAnnotation(
            address = 0x0010u,
            label = "THE 'PRINT A CHARACTER' RESTART",
            description = "The A register holds the code of the character that is to be printed.",
        ),
        MemoryPointAnnotation(
            address = 0x0013u,
            label = "Unused",
        ),
        MemoryPointAnnotation(
            address = 0x0018u,
            label = "THE 'COLLECT CHARACTER' RESTART",
            description = "The contents of the location currently addressed by CH-ADD are fetched. A return is made if the value represents a printable character, otherwise CH-ADD is incremented and the tests repeated.",
        ),
        MemoryPointAnnotation(
            address = 0x0020u,
            label = "THE 'COLLECT NEXT CHARACTER' RESTART",
            description = "As a BASIC line is interpreted, this routine is called repeatedly to step along the line.",
        ),
        MemoryPointAnnotation(
            address = 0x0025u,
            label = "Unused",
        ),
        MemoryPointAnnotation(
            address = 0x0028u,
            label = "THE 'CALCULATOR' RESTART",
            description = "The floating point calculator is entered at 335B.",
        ),
        MemoryPointAnnotation(
            address = 0x002Bu,
            label = "Unused",
        ),
        MemoryPointAnnotation(
            address = 0x0030u,
            label = "THE 'MAKE BC SPACES' RESTART",
            description = "This routine creates free locations in the work space. The number of locations is determined by the current contents of the BC register pair.",
        ),
        MemoryPointAnnotation(
            address = 0x0038u,
            label = "THE 'MASKABLE INTERRUPT' ROUTINE",
            description = "The real time clock is incremented and the keyboard scanned whenever a maskable interrupt occurs.",
        ),
        MemoryPointAnnotation(
            address = 0x0053u,
            label = "THE 'ERROR-2' ROUTINE",
            description = "The return address to the interpreter points to the 'DEFB' that signifies which error has occurred. This 'DEFB' is fetched and transferred to ERR-NR. The machine stack is cleared before jumping forward to clear the calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x005Fu,
            label = "Unused",
        ),
        MemoryPointAnnotation(
            address = 0x0066u,
            label = "THE 'NON-MASKABLE INTERRUPT' ROUTINE",
            description = "This routine is not used in the standard Spectrum but the code allows for a system reset to occur following activation of the NMI line. The system variable at 5CB0, named here NMIADD, has to have the value zero for the reset to occur.",
        ),
        MemoryPointAnnotation(
            address = 0x0074u,
            label = "THE 'CH-ADD+1' SUBROUTINE",
            description = "The address held in CH-ADD is fetched, incremented and restored. The contents of the location now addressed by CH-ADD is fetched. The entry points of TEMP-PTR1 and TEMP-PTR2 are used to set CH-ADD for a temporary period.",
        ),
        MemoryPointAnnotation(
            address = 0x007Du,
            label = "THE 'SKIP-OVER' SUBROUTINE",
            description = "The value brought to the subroutine in the A register is tested to see if it is printable. Various special codes lead to HL being incremented once, or twice, and CH-ADD amended accordingly.",
        ),
        MemoryPointAnnotation(
            address = 0x0095u,
            label = "THE TOKEN TABLE",
            description = "All the tokens used by the Spectrum are expanded by reference to this table. The last code of each token is 'inverted' by having its bit 7 set.",
        ),
        MemoryPointAnnotation(
            address = 0x0205u,
            label = "THE KEY TABLES",
            description = "There are six separate key tables. The final character code obtained depends on the particular key pressed and the 'mode' being used. (a) The main key table L mode and CAPS SHIFT.",
        ),
        MemoryPointAnnotation(
            address = 0x028Eu,
            label = "THE 'KEYBOARD SCANNING' SUBROUTINE",
            description = "This very important subroutine is called by both the main keyboard subroutine and the INKEY$ routine (in SCANNING). In all instances the E register is returned with a value in the range of +00 to +27, the value being different for each of the forty keys of the keyboard, or the value +FF, the no-key. The D register is returned with a value that indicates which single shift key is being pressed. If both shift keys are being pressed then the D and E registers are returned with the values for the CAPS SHIFT and SYMBOL SHIFT keys respectively. If no keys is being pressed then the DE register pair is returned holding +FFFF. The zero flag is returned reset if more than two keys are being pressed, or neither key of a pair of keys is a shift key.",
        ),
        MemoryPointAnnotation(
            address = 0x02BFu,
            label = "THE 'KEYBOARD' SUBROUTINE",
            description = "This subroutine is called on every occasion that a maskable interrupt occurs. In normal operation this will happen once every 20 ms. The purpose of this subroutine is to scan the keyboard and decode the key value. The code produced will, if the 'repeat' status allows it, be passed to the system variable LAST-K. When a code is put into this system variable bit 5 of FLAGS is set to show that a 'new' key has been pressed.",
        ),
        MemoryPointAnnotation(
            address = 0x0310u,
            label = "THE 'REPEATING KEY' SUBROUTINE",
            description = "A key will 'repeat' on the first occasion after the delay period - REPDEL (normally 0.7 secs.) and on subsequent occasions after the delay period - REPPER (normally 0.1 secs.).",
        ),
        MemoryPointAnnotation(
            address = 0x031Eu,
            label = "THE 'K-TEST' SUBROUTINE",
            description = "The key value is tested and a return made if 'no-key' or 'shift-only'; otherwise the 'main code' for that key is found.",
        ),
        MemoryPointAnnotation(
            address = 0x0333u,
            label = "THE 'KEYBOARD DECODING' SUBROUTINE",
            description = "This subroutine is entered with the 'main code' in the E register, the value of FLAGS in the D register, the value of MODE in the C register and the 'shift byte' in the B register. By considering these four values and referring, as necessary, to the six key tables a 'final code' is produced. This is returned in the A register.",
        ),
        MemoryPointAnnotation(
            address = 0x03B5u,
            label = "THE 'BEEPER' SUBROUTINE",
            description = "This subroutine is entered with the DE register pair holding the value 'f*t', where a note of given frequency 'f' is to have a duration of 't' seconds, and the HL register pair holding a value equal to the number of T states in the 'timing loop' divided by '4'. i.e. For the note 'middle C' to be produced for one second DE holds +0105 (INT(261.3 * 1)) and HL holds +066A (derived from 6,689/4 30.125).",
        ),
        MemoryPointAnnotation(
            address = 0x03F8u,
            label = "THE 'BEEP' COMMAND ROUTINE",
            description = "The subroutine is entered with two numbers on the calculator stack. The topmost number represents the 'pitch' of the note and the number underneath it represents the 'duration'.",
        ),
        MemoryPointAnnotation(
            address = 0x046Eu,
            label = "THE 'SEMI-TONE' TABLE",
            description = "This table holds the frequencies of the twelve semi-tones in an octave. frequency hz. note",
        ),
        MemoryPointAnnotation(
            address = 0x04AAu,
            label = "THE 'PROGRAM NAME' SUBROUTINE (ZX81)",
            description = "The following subroutine applies to the ZX81 and was not removed when the program was rewritten for the SPECTRUM.",
        ),
        MemoryPointAnnotation(
            address = 0x04C2u,
            label = "THE 'SA-BYTES' SUBROUTINE",
            description = "This subroutine is called to SAVE the header information (from 09BA) and later the actual program/data block (from 099E).",
        ),
        MemoryPointAnnotation(
            address = 0x053Fu,
            label = "THE 'SA/LD-RET' SUBROUTINE",
            description = "This subroutine is common to both SAVEing and LOADing. The border is set to its original colour and the BREAK key tested for a last time.",
        ),
        MemoryPointAnnotation(
            address = 0x0556u,
            label = "THE 'LD-BYTES' SUBROUTINE",
            description = "This subroutine is called to LOAD the header information (from 07BE) and later LOAD, or VERIFY, an actual block of data (from 0802).",
        ),
        MemoryPointAnnotation(
            address = 0x05E3u,
            label = "THE 'LD-EDGE-2' AND 'LD-EDGE-1' SUBROUTINES",
            description = "These two subroutines form the most important part of the LOAD/VERIFY operation. The subroutines are entered with a timing constant in the B register, and the previous border colour and 'edge-type' in the C register. The subroutines return with the carry flag set if the required number of 'edges' have been found in the time allowed; and the change to the value in the B register shows just how long it took to find the 'edge(s)'. The carry flag will be reset if there is an error. The zero flag then signals 'BREAK pressed' by being reset, or 'time-up' by being set. The entry point LD-EDGE-2 is used when the length of a complete pulse is required and LD-EDGE-1 is used to find the time before the next 'edge'.",
        ),
        MemoryPointAnnotation(
            address = 0x0605u,
            label = "THE 'SAVE, LOAD, VERIFY and MERGE' COMMAND ROUTINES",
        ),
        MemoryPointAnnotation(
            address = 0x07CBu,
            label = "THE 'VERIFY' CONTROL ROUTINE",
            description = "The verification process involves the LOADing of a block of data, a byte at a time, but the bytes are not stored only checked. This routine is also used to LOAD blocks of data that have been described with 'SCREEN$ & CODE'.",
        ),
        MemoryPointAnnotation(
            address = 0x0802u,
            label = "THE 'LOAD A DATA BLOCK' SUBROUTINE",
            description = "This subroutine is common to all the 'LOADing' routines. In the case of LOAD & VERIFY it acts as a full return from the cassette handling routines but in the case of MERGE the data block has yet to be 'MERGEd'.",
        ),
        MemoryPointAnnotation(
            address = 0x0808u,
            label = "THE 'LOAD' CONTROL ROUTINE",
            description = "This routine controls the LOADing of a BASIC program, and its variables, or an array.",
        ),
        MemoryPointAnnotation(
            address = 0x08B6u,
            label = "THE 'MERGE' CONTROL ROUTINE",
            description = "There are three main parts to this routine. I. LOAD the data block into the work space. II. MERGE the lines of the new program into the old program. III. MERGE the new variables into the old variables. Start therefore with the LOADing of the data block.",
        ),
        MemoryPointAnnotation(
            address = 0x092Cu,
            label = "THE 'MERGE A LINE OR A VARIABLE' SUBROUTINE",
            description = "This subroutine is entered with the following parameters: Carry flag reset MERGE a BASIC line. set MERGE a variable. Zero reset It will be an 'addition'. set It is a 'replacement'. HL register pair Points to the start of the new entry. DE register pair Points to where it is to MERGE.",
        ),
        MemoryPointAnnotation(
            address = 0x0970u,
            label = "THE 'SAVE' CONTROL ROUTINE",
            description = "The operation of SAVing a program or a block of data is very straightforward.",
        ),
        MemoryPointAnnotation(
            address = 0x09A1u,
            label = "THE CASSETTE MESSAGES",
            description = "Each message is given with the last character inverted (+80 hex.).",
        ),
        MemoryPointAnnotation(
            address = 0x09F4u,
            label = "THE 'PRINT-OUT' ROUTINES",
            description = "All of the printing to the main part of the screen, the lower part of the screen and the printer is handled by this set of routines. The PRINT-OUT routine is entered with the A register holding the code for a control character, a printable character or a token.",
        ),
        MemoryPointAnnotation(
            address = 0x0A11u,
            label = "THE 'CONTROL CHARACTER' TABLE",
            description = "address offset character address offset character",
        ),
        MemoryPointAnnotation(
            address = 0x0A23u,
            label = "THE 'CURSOR LEFT' SUBROUTINE",
            description = "The subroutine is entered with the B register holding the current line number and the C register with the current column number.",
        ),
        MemoryPointAnnotation(
            address = 0x0A3Du,
            label = "THE 'CURSOR RIGHT' SUBROUTINE",
            description = "This subroutine performs an operation identical to the BASIC statement – PRINT OVER 1;CHR$ 32; -.",
        ),
        MemoryPointAnnotation(
            address = 0x0A4Fu,
            label = "THE 'CARRIAGE RETURN' SUBROUTINE",
            description = "If the printing being handled is going to the printer then a carriage return character leads to the printer buffer being emptied. If the printing is to the screen then a test for 'scroll?' is made before decreasing the line number.",
        ),
        MemoryPointAnnotation(
            address = 0x0A5Fu,
            label = "THE 'PRINT COMMA' SUBROUTINE",
            description = "The current column value is manipulated and the A register set to hold +00 (for TAB 0) or +10 (for TAB 16).",
        ),
        MemoryPointAnnotation(
            address = 0x0A69u,
            label = "THE 'PRINT A QUESTION MARK' SUBROUTINE",
            description = "A question mark is printed whenever an attempt is made to print an unprintable code.",
        ),
        MemoryPointAnnotation(
            address = 0x0A6Du,
            label = "THE 'CONTROL CHARACTERS WITH OPERANDS' ROUTINE",
            description = "The control characters from INK to OVER require a single operand whereas the control characters AT & TAB are required to be followed by two operands. The present routine leads to the control character code being saved in TVDATA-lo, the first operand in TVDATA-hi or the A register if there is only a single operand required, and the second operand in the A register.",
        ),
        MemoryPointAnnotation(
            address = 0x0AD9u,
            label = "PRINTABLE CHARACTER CODES",
        ),
        MemoryPointAnnotation(
            address = 0x0ADCu,
            label = "THE 'POSITION STORE' SUBROUTINE",
            description = "The new position's 'line & column' values and the 'pixel' address are stored in the appropriate system variables.",
        ),
        MemoryPointAnnotation(
            address = 0x0B03u,
            label = "THE 'POSITION FETCH' SUBROUTINE",
            description = "The current position's parameters are fetched from the appropriate system variables.",
        ),
        MemoryPointAnnotation(
            address = 0x0B24u,
            label = "THE 'PRINT ANY CHARACTER(S)' SUBROUTINE",
            description = "Ordinary character codes, token codes and user-defined graphic codes, and graphic codes are dealt with separately.",
        ),
        MemoryPointAnnotation(
            address = 0x0B7Fu,
            label = "THE 'PRINT ALL CHARACTERS' SUBROUTINE",
            description = "This subroutine is used to print all '8*8' bit characters. On entry the DE register pair holds the base address of the character form, the HL register the destination address and the BC register pair the current 'line & column' values.",
        ),
        MemoryPointAnnotation(
            address = 0x0BDBu,
            label = "THE 'SET ATTRIBUTE BYTE' SUBROUTINE",
            description = "The appropriate attribute byte is identified and fetched. The new value is formed by manipulating the old value, ATTR-T, MASK-T and P-FLAG. Finally this new value is copied to the attribute area.",
        ),
        MemoryPointAnnotation(
            address = 0x0C0Au,
            label = "THE 'MESSAGE PRINTING' SUBROUTINE",
            description = "This subroutine is used to print messages and tokens. The A register holds the 'entry number' of the message or token in a table. The DE register pair holds the base address of the table.",
        ),
        MemoryPointAnnotation(
            address = 0x0C3Bu,
            label = "THE 'PO-SAVE' SUBROUTINE",
            description = "This subroutine allows for characters to be printed 'recursively'. The appropriate registers are saved whilst 'PRINT-OUT' is called.",
        ),
        MemoryPointAnnotation(
            address = 0x0C41u,
            label = "THE 'TABLE SEARCH' SUBROUTINE",
            description = "The subroutine returns with the DE register pair pointing to the initial character of the required entry and the carry flag reset if a 'leading space' is to be considered.",
        ),
        MemoryPointAnnotation(
            address = 0x0C55u,
            label = "THE 'TEST FOR SCROLL' SUBROUTINE",
            description = "This subroutine is called whenever there might be the need to scroll the display. This occurs on three occasions; i. when handling a 'carriage return' character; ii. when using AT in an INPUT line; & iii. when the current line is full and the next line has to be used. On entry the B register holds the line number under test. 40",
        ),
        MemoryPointAnnotation(
            address = 0x0D4Du,
            label = "THE 'TEMPORARY COLOUR ITEMS' SUBROUTINE",
            description = "This is a most important subroutine. It is used whenever the 'permanent' details are required to be copied to the 'temporary' system variables. First ATTR-T & MASK-T are considered",
        ),
        MemoryPointAnnotation(
            address = 0x0D6Bu,
            label = "THE 'CLS' COMMAND ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x0DAFu,
            label = "THE 'CLEARING THE WHOLE DISPLAY AREA' SUBROUTINE",
            description = "This subroutine is called from; i. the CLS command routine. ii. the main execution routine, and iii. the automatic listing routine.",
        ),
        MemoryPointAnnotation(
            address = 0x0DD9u,
            label = "THE 'CL-SET' SUBROUTINE",
            description = "This subroutine is entered with the BC register pair holding the line and column numbers of a character areas, or the C register holding the column number within the printer buffer. The appropriate address of the first character bit is then found. The subroutine returns via PO-STORE so as to store all the values in the required system variables.",
        ),
        MemoryPointAnnotation(
            address = 0x0DFEu,
            label = "THE 'SCROLLING' SUBROUTINE",
            description = "The number of lines of the display that are to be scrolled has to be held on entry to the main subroutine in the B register.",
        ),
        MemoryPointAnnotation(
            address = 0x0E44u,
            label = "THE 'CLEAR LINES' SUBROUTINE",
            description = "This subroutine will clear the bottom 'B' lines of the display.",
        ),
        MemoryPointAnnotation(
            address = 0x0E88u,
            label = "THE 'CL-ATTR' SUBROUTINE",
            description = "This subroutine has two separate functions. i. For a given display area address the appropriate attribute address is returned in the DE register pair. Note that the value on entry points to the 'ninth' line of a character. ii. For a given line number, in the B register, the number of character areas in the display from the start of that line onwards is returned in the BC register pair.",
        ),
        MemoryPointAnnotation(
            address = 0x0E9Bu,
            label = "THE 'CL-ADDR' SUBROUTINE",
            description = "For a given line number, in the B register, the appropriate display file address is formed in the HL register pair.",
        ),
        MemoryPointAnnotation(
            address = 0x0EACu,
            label = "THE 'COPY' COMMAND ROUTINE",
            description = "The one hundred and seventy six pixel lines of the display are dealt with one by one.",
        ),
        MemoryPointAnnotation(
            address = 0x0ECDu,
            label = "THE 'COPY-BUFF' SUBROUTINE",
            description = "This subroutine is called whenever the printer buffer is to have its contents passed to the printer.",
        ),
        MemoryPointAnnotation(
            address = 0x0EDFu,
            label = "THE 'CLEAR PRINTER BUFFER' SUBROUTINE",
            description = "The printer buffer is cleared by calling this subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x0EF4u,
            label = "THE 'COPY-LINE' SUBROUTINE",
            description = "The subroutine is entered with the HL register pair holding the base address of the thirty two bytes that form the pixel-line and the B register holding the pixel-line number.",
        ),
        MemoryPointAnnotation(
            address = 0x0F2Cu,
            label = "THE 'EDITOR' ROUTINES",
            description = "The editor is called on two occasions: i. From the main execution routine so that the user can enter a BASIC line into the system. ii. From the INPUT command routine. First the 'error stack pointer' is saved and an alternative address provided.",
        ),
        MemoryPointAnnotation(
            address = 0x0F81u,
            label = "THE 'ADDCHAR' SUBROUTINE",
            description = "This subroutine actually adds a code to the current EDIT or INPUT line.",
        ),
        MemoryPointAnnotation(
            address = 0x0FA0u,
            label = "THE 'EDITING KEYS' TABLE",
            description = "address offset character address offset character",
        ),
        MemoryPointAnnotation(
            address = 0x0FA9u,
            label = "THE 'EDIT KEY' SUBROUTINE",
            description = "When in 'editing mode' pressing the EDIT key will bring down the 'current BASIC line'. However in 'INPUT mode' the action of the EDIT key is to clear the current reply and allow a fresh one.",
        ),
        MemoryPointAnnotation(
            address = 0x0FF3u,
            label = "THE 'CURSOR DOWN EDITING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1007u,
            label = "THE 'CURSOR LEFT EDITING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x100Cu,
            label = "THE 'CURSOR RIGHT EDITING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1015u,
            label = "THE 'DELETE EDITING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x101Eu,
            label = "THE 'ED-IGNORE' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1024u,
            label = "THE 'ENTER EDITING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1031u,
            label = "THE 'ED-EDGE' SUBROUTINE",
            description = "The address of the cursor is in the HL register pair and will be decremented unless the cursor is already at the start of the line. Care is taken not to put the cursor between control characters and their parameters.",
        ),
        MemoryPointAnnotation(
            address = 0x1059u,
            label = "THE 'CURSOR UP EDITING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1076u,
            label = "THE 'ED-SYMBOL' SUBROUTINE",
            description = "If SYMBOL & GRAPHICS codes were used they would be handled as follows:",
        ),
        MemoryPointAnnotation(
            address = 0x107Fu,
            label = "THE 'ED-ERROR' SUBROUTINE",
            description = "Come here when there has been some kind of error.",
        ),
        MemoryPointAnnotation(
            address = 0x1097u,
            label = "THE 'CLEAR-SP' SUBROUTINE",
            description = "The editing area or the work space is cleared as directed.",
        ),
        MemoryPointAnnotation(
            address = 0x10A8u,
            label = "THE 'KEYBOARD INPUT' SUBROUTINE",
            description = "This important subroutine returns the code of the last key to have bean pressed but note that CAPS LOCK, the changing of the mode and the colour control parameters are handled within the subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x111Du,
            label = "THE 'LOWER SCREEN COPYING' SUBROUTINE",
            description = "This subroutine is called whenever the line in the editing area or the INPUT area is to be printed in the lower part of the screen.",
        ),
        MemoryPointAnnotation(
            address = 0x1190u,
            label = "THE 'SET-HL' AND 'SET-DE' SUBROUTINES",
            description = "These subroutines return with HL pointing to the first location and DE the 'last' location of either the editing area or the work space.",
        ),
        MemoryPointAnnotation(
            address = 0x11A7u,
            label = "THE 'REMOVE-FP' SUBROUTINE",
            description = "This subroutine removes the hidden floating-point forms in a BASIC line.",
        ),
        MemoryPointAnnotation(
            address = 0x11B7u,
            label = "THE 'NEW' COMMAND ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x12A2u,
            label = "THE 'MAIN EXECUTION' LOOP",
            description = "The main loop extends from location 12A2 to location 15AE and it controls the 'editing mode', the execution of direct commands and the production of reports.",
        ),
        MemoryPointAnnotation(
            address = 0x1391u,
            label = "THE REPORT MESSAGES",
            description = "Each message is given with the last character inverted (+80 hex.).",
        ),
        MemoryPointAnnotation(
            address = 0x1539u,
            label = "THE COPYRIGHT MESSAGE",
        ),
        MemoryPointAnnotation(
            address = 0x1555u,
            label = "Report G - No room for line",
        ),
        MemoryPointAnnotation(
            address = 0x155Du,
            label = "THE 'MAIN-ADD' SUBROUTINE",
            description = "This subroutine allows for a new BASIC line to be added to the existing BASIC program in the program area. If a line has both an old and a new version then the old one is 'reclaimed'. A new line that consists of only a line number does not go into the program area.",
        ),
        MemoryPointAnnotation(
            address = 0x15AFu,
            label = "THE 'INITIAL CHANNEL INFORMATION'",
            description = "Initially there are four channels 'K', 'S', 'R', & 'P' for communicating with the 'keyboard', 'screen', 'work space' and 'printer'. For each channel the output routine address comes before the input routine address and the channel's code.",
        ),
        MemoryPointAnnotation(
            address = 0x15C4u,
            label = "Report J - Invalid I/O device",
        ),
        MemoryPointAnnotation(
            address = 0x15C6u,
            label = "THE 'INITIAL STREAM DATA'",
            description = "Initially there are seven streams +FD to +03.",
        ),
        MemoryPointAnnotation(
            address = 0x15D4u,
            label = "THE 'WAIT-KEY' SUBROUTINE",
            description = "This subroutine is the controlling subroutine for calling the current input subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x15E6u,
            label = "THE 'INPUT-AD' SUBROUTINE",
            description = "The registers are saved and HL made to point to the input address.",
        ),
        MemoryPointAnnotation(
            address = 0x15EFu,
            label = "THE 'MAIN PRINTING' SUBROUTINE",
            description = "The subroutine is called with either an absolute value or a proper character code in the A register.",
        ),
        MemoryPointAnnotation(
            address = 0x1601u,
            label = "THE 'CHAN-OPEN' SUBROUTINE",
            description = "This subroutine is called with the A register holding a valid stream number normally +FD to +03. Then depending on the stream data a particular channel will be made the current channel.",
        ),
        MemoryPointAnnotation(
            address = 0x1615u,
            label = "THE 'CHAN-FLAG' SUBROUTINE",
            description = "The appropriate flags for the different channels are set by this subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x162Du,
            label = "THE 'CHANNEL CODE LOOK-UP' TABLE",
        ),
        MemoryPointAnnotation(
            address = 0x1634u,
            label = "THE 'CHANNEL 'K' FLAG' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1642u,
            label = "THE 'CHANNEL 'S' FLAG' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x164Du,
            label = "THE 'CHANNEL 'P' FLAG' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1652u,
            label = "THE 'MAKE-ROOM' SUBROUTINE",
            description = "This is a very important subroutine. It is called on many occasions to 'open up' an area. In all cases the HL register pair points to the location after the place where 'room' is required and the BC register pair holds the length of the 'room' needed. When a single space only is required then the subroutine is entered at ONE-SPACE.",
        ),
        MemoryPointAnnotation(
            address = 0x1664u,
            label = "THE 'POINTERS' SUBROUTINE",
            description = "Whenever an area has to be 'made' or 'reclaimed' the system variables that address locations beyond the 'position' of the change have to be amended as required. On entry the BC register pair holds the number of bytes involved and the HL register pair addresses the location before the 'position'.",
        ),
        MemoryPointAnnotation(
            address = 0x168Fu,
            label = "THE 'COLLECT A LINE NUMBER' SUBROUTINE",
            description = "On entry the HL register pair points to the location under consideration. If the location holds a value that constitutes a suitable high byte for a line number then the line number is returned in DE. However if this is not so then the location addressed by DE is tried instead; and should this also be unsuccessful line number zero is returned.",
        ),
        MemoryPointAnnotation(
            address = 0x169Eu,
            label = "THE 'RESERVE' SUBROUTINE",
            description = "This subroutine is normally called by using RST 0030,BC-SPACES. On entry here the last value on the machine stack is WORKSP and the value above it is the number of spaces that is to be 'reserved'. This subroutine always makes 'room' between the existing work space and the calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x16B0u,
            label = "THE 'SET-MIN' SUBROUTINE",
            description = "This subroutine resets the editing area and the areas after it to their minimum sizes. In effect it 'clears' the areas.",
        ),
        MemoryPointAnnotation(
            address = 0x16D4u,
            label = "THE 'RECLAIM THE EDIT-LINE' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x16DBu,
            label = "THE 'INDEXER' SUBROUTINE",
            description = "This subroutine is used on several occasions to look through tables. The entry point is at INDEXER.",
        ),
        MemoryPointAnnotation(
            address = 0x16E5u,
            label = "THE 'CLOSE #' COMMAND ROUTINE",
            description = "This command allows the user to CLOSE streams. However for streams +00 to +03 the 'initial' stream data is restored and these streams cannot therefore be CLOSEd.",
        ),
        MemoryPointAnnotation(
            address = 0x1701u,
            label = "THE 'CLOSE-2' SUBROUTINE",
            description = "The code of the channel associated with the stream being closed has to be 'K', 'S', or 'P'.",
        ),
        MemoryPointAnnotation(
            address = 0x1716u,
            label = "THE 'CLOSE STREAM LOOK-UP' TABLE",
        ),
        MemoryPointAnnotation(
            address = 0x171Cu,
            label = "THE 'CLOSE STREAM' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x171Eu,
            label = "THE 'STREAM DATA' SUBROUTINE",
            description = "This subroutine returns in the BC register pair the stream data for a given stream.",
        ),
        MemoryPointAnnotation(
            address = 0x1736u,
            label = "THE 'OPEN #' COMMAND ROUTINE",
            description = "This command allows the user to OPEN streams. A channel code must be supplied and it must be 'K', 'k', 'S', 's', 'P', or 'p'. Note that no attempt is made to give streams +00 to +03 their initial data.",
        ),
        MemoryPointAnnotation(
            address = 0x175Du,
            label = "THE 'OPEN-2' SUBROUTINE",
            description = "The appropriate stream data bytes for the channel that is associated with the stream being OPENed are found.",
        ),
        MemoryPointAnnotation(
            address = 0x177Au,
            label = "THE 'OPEN STREAM LOOK-UP' TABLE",
        ),
        MemoryPointAnnotation(
            address = 0x1781u,
            label = "THE 'OPEN-K' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1785u,
            label = "THE 'OPEN-S' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1789u,
            label = "THE 'OPEN-P' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1793u,
            label = "THE 'CAT, ERASE, FORMAT and MOVE' COMMAND ROUTINES",
        ),
        MemoryPointAnnotation(
            address = 0x1795u,
            label = "THE 'LIST and LLIST' COMMAND ROUTINES",
        ),
        MemoryPointAnnotation(
            address = 0x17F5u,
            label = "THE 'LLIST' ENTRY POINT",
            description = "The printer channel will need to be opened.",
        ),
        MemoryPointAnnotation(
            address = 0x17F9u,
            label = "THE 'LIST' ENTRY POINT",
            description = "The 'main screen' channel will need to be opened.",
        ),
        MemoryPointAnnotation(
            address = 0x1855u,
            label = "THE 'PRINT A WHOLE BASIC LINE' SUBROUTINE",
            description = "The HL register pair points to the start of the line the location holding the high byte of the line number. Before the line number is printed it is tested to determine whether it comes before the 'current' line, is the 'current' line or comes after.",
        ),
        MemoryPointAnnotation(
            address = 0x18B6u,
            label = "THE 'NUMBER' SUBROUTINE",
            description = "If the A register holds the 'number marker' then the HL register pair is advanced past the floating-point form.",
        ),
        MemoryPointAnnotation(
            address = 0x18C1u,
            label = "THE 'PRINT A FLASHING CHARACTER' SUBROUTINE",
            description = "The 'error cursor' and the 'mode cursors' are printed using this subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x18E1u,
            label = "THE 'PRINT THE CURSOR' SUBROUTINE",
            description = "A return is made if it is not the correct place to print the cursor but if it is then either 'C', 'E', 'G', 'K' or 'L' will be printed.",
        ),
        MemoryPointAnnotation(
            address = 0x190Fu,
            label = "THE 'LN-FETCH' SUBROUTINE",
            description = "This subroutine is entered with the HL register pair addressing a system variable S-TOP or E-PPC. The subroutine returns with the system variable holding the line number of the following line.",
        ),
        MemoryPointAnnotation(
            address = 0x1925u,
            label = "THE 'PRINTING CHARACTERS IN A BASIC LINE' SUBROUTINE",
            description = "All of the character/token codes in a BASIC line are printed by repeatedly calling this subroutine. The entry point OUT-SP-NO is used when printing line numbers which may require leading spaces.",
        ),
        MemoryPointAnnotation(
            address = 0x196Eu,
            label = "THE 'LINE-ADDR' SUBROUTINE",
            description = "For a given line number, in the HL register pair, this subroutine returns the starting address of that line or the 'first line after', in the HL register pair, and the start of the previous line in the DE register pair. If the line number is being used the zero flag will be set. However if the 'first line after' is substituted then the zero flag is returned reset.",
        ),
        MemoryPointAnnotation(
            address = 0x1980u,
            label = "THE 'COMPARE LINE NUMBERS' SUBROUTINE",
            description = "The given line number in the BC register pair is matched against the addressed line number.",
        ),
        MemoryPointAnnotation(
            address = 0x1988u,
            label = "Unused",
        ),
        MemoryPointAnnotation(
            address = 0x198Bu,
            label = "THE 'FIND EACH STATEMENT' SUBROUTINE",
            description = "This subroutine has two distinct functions. I. It can be used to find the 'D'th. statement in a BASIC line returning with the HL register pair addressing the location before the start of the statement and the zero flag set. II. Also the subroutine can be used to find a statement, if any, that starts with a given token code (in the E register). 79 1988 INC HL Not used. INC HL INC HL",
        ),
        MemoryPointAnnotation(
            address = 0x19B8u,
            label = "THE 'NEXT-ONE' SUBROUTINE",
            description = "This subroutine can be used to find the 'next line' in the program area or the 'next variable' in the variables area. The subroutine caters for the six different types of variable that are used in the SPECTRUM system.",
        ),
        MemoryPointAnnotation(
            address = 0x19DDu,
            label = "THE 'DIFFERENCE' SUBROUTINE",
            description = "The 'length' between two 'starts' is formed in the BC register pair. The pointers are reformed but returned exchanged.",
        ),
        MemoryPointAnnotation(
            address = 0x19E5u,
            label = "THE 'RECLAIMING' SUBROUTINE",
            description = "The entry point RECLAIM-1 is used when the address of the first location to be reclaimed is in the DE register pair and the address of the first location to be left alone is in the HL register pair. The entry point RECLAIM-2 is used when the HL register pair points to the first location to be reclaimed and the BC register pair holds the number of the bytes that are to be reclaimed.",
        ),
        MemoryPointAnnotation(
            address = 0x19FBu,
            label = "THE 'E-LINE-NO' SUBROUTINE",
            description = "This subroutine is used to read the line number of the line in the editing area. If there is no line number, i.e. a direct BASIC line, then the line number is considered to be zero. In all cases the line number is returned in the BC register pair.",
        ),
        MemoryPointAnnotation(
            address = 0x1A1Bu,
            label = "THE 'REPORT AND LINE NUMBER PRINTING' SUBROUTINE",
            description = "The entry point OUT-NUM-1 will lead to the number in the BC register pair being printed. Any value over '9,999' will not however be printed correctly. The entry point OUT-NUM-2 will lead to the number indirectly addressed by the HL register pair being printed. This time any necessary leading spaces will appear. Again the limit of correctly printed numbers is '9,999'.",
        ),
        MemoryPointAnnotation(
            address = 0x1A48u,
            label = "THE SYNTAX TABLES",
            description = "i. The offset table There is an offset value for each of the fifty BASIC commands. command address command address",
        ),
        MemoryPointAnnotation(
            address = 0x1B17u,
            label = "THE 'MAIN PARSER' OF THE BASIC INTERPRETER",
            description = "The parsing routine of the BASIC interpreter is entered at LINE-SCAN when syntax is being checked, and at LINE-RUN when a BASIC program of one or more statements is to be executed. Each statement is considered in turn and the system variable CH-ADD is used to point to each code of the statement as it occurs in the program area or the editing area.",
        ),
        MemoryPointAnnotation(
            address = 0x1B28u,
            label = "THE STATEMENT LOOP",
        ),
        MemoryPointAnnotation(
            address = 0x1B6Fu,
            label = "THE 'SEPARATOR' SUBROUTINE",
            description = "The report 'Nonsense in BASIC is given if the required separator is not present. But note that when syntax is being checked the actual report does not appear on the screen only the 'error marker'.",
        ),
        MemoryPointAnnotation(
            address = 0x1B76u,
            label = "THE 'STMT-RET' SUBROUTINE",
            description = "After the correct interpretation of a statement a return is made to this entry point.",
        ),
        MemoryPointAnnotation(
            address = 0x1B8Au,
            label = "THE 'LINE-RUN' ENTRY POINT",
            description = "This entry point is used wherever a line in the editing area is to be 'run'. In such a case the syntax/run flag (bit 7 of FLAGS) will be set. The entry point is also used in the syntax checking of a line in the editing area that has more than one statement (bit 7 of FLAGS will be reset).",
        ),
        MemoryPointAnnotation(
            address = 0x1B9Eu,
            label = "THE 'LINE-NEW' SUBROUTINE",
            description = "There has been a jump in the program and the starting address of the new line has to be found.",
        ),
        MemoryPointAnnotation(
            address = 0x1BB2u,
            label = "THE 'REM' COMMAND ROUTINE",
            description = "The return address to STMT-RET is dropped which has the effect of forcing the rest of the line to be ignored.",
        ),
        MemoryPointAnnotation(
            address = 0x1BB3u,
            label = "THE 'LINE-END' ROUTINE",
            description = "If checking syntax a simple return is made but when 'running' the address held by NXTLIN has to be checked before it can be used.",
        ),
        MemoryPointAnnotation(
            address = 0x1BBFu,
            label = "THE 'LINE-USE' ROUTINE",
            description = "This short routine has three functions; i. Change statement zero to statement '1'; ii. Find the number of the new line and enter it into PPC; & iii. Form the address of the start of the line after.",
        ),
        MemoryPointAnnotation(
            address = 0x1BD1u,
            label = "THE 'NEXT-LINE' ROUTINE",
            description = "On entry the HL register pair points to the location after the end of the 'next' line to be handled and the DE register pair to the location before the first character of the line. This applies to lines in the program area and also to a line in the editing area where the next line will be the same line again whilst there are still statements to be interpreted.",
        ),
        MemoryPointAnnotation(
            address = 0x1BEEu,
            label = "THE 'CHECK-END' SUBROUTINE",
            description = "This is an important routine and is called from many places in the monitor program when the syntax of the edit-line is being checked. The purpose of the routine is to give an error report if the end of a statement has not been reached and to move on to the next statement if the syntax is correct.",
        ),
        MemoryPointAnnotation(
            address = 0x1BF4u,
            label = "THE 'STMT-NEXT' ROUTINE",
            description = "If the present character is a 'carriage return' then the 'next statement' is on the 'next line'; if ' : ' it is on the same line; but if any other character is found then there is an error in syntax.",
        ),
        MemoryPointAnnotation(
            address = 0x1C01u,
            label = "THE 'COMMAND CLASS' TABLE",
            description = "address offset class number address offset class number",
        ),
        MemoryPointAnnotation(
            address = 0x1C0Du,
            label = "THE 'COMMAND CLASSES - +00, +03 and +05'",
        ),
        MemoryPointAnnotation(
            address = 0x1C16u,
            label = "THE 'JUMP-C-R' ROUTINE",
            description = "After the command class entries and the separator entries in the parameter table have been considered the jump to the appropriate command routine is made.",
        ),
        MemoryPointAnnotation(
            address = 0x1C1Fu,
            label = "THE 'COMMAND CLASS +01' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1C22u,
            label = "THE 'VARIABLE IN ASSIGNMENT' SUBROUTINE",
            description = "This subroutine develops the appropriate values for the system variables DEST & STRLEN.",
        ),
        MemoryPointAnnotation(
            address = 0x1C4Eu,
            label = "THE 'COMMAND CLASS +02' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1C56u,
            label = "THE 'FETCH A VALUE' SUBROUTINE",
            description = "This subroutine is used by LET, READ & INPUT statements to first evaluate and then assign values to the previously designated variable. The entry point VAL-FET-1 is used by LET & READ and considers FLAGS whereas the entry point VAL-FET-2 is used by INPUT and considers FLAGX.",
        ),
        MemoryPointAnnotation(
            address = 0x1C6Cu,
            label = "THE 'COMMAND CLASS +04' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1C79u,
            label = "THE 'EXPECT NUMERIC/STRING EXPRESSIONS' SUBROUTINE",
            description = "There is a series of short subroutines that are used to fetch the result of evaluating the next expression. The result from a single expression is returned as a 'last value' on the calculator stack. The entry point NEXT-2NUM is used when CH-ADD needs updating to point to the start of the first expression.",
        ),
        MemoryPointAnnotation(
            address = 0x1C96u,
            label = "THE 'SET PERMANENT COLOURS' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1CBEu,
            label = "THE 'COMMAND CLASS +09' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1CDBu,
            label = "THE 'COMMAND CLASS +0B' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1CDEu,
            label = "THE 'FETCH A NUMBER' SUBROUTINE",
            description = "This subroutine leads to a following numeric expression being evaluated but zero being used instead if there is no expression.",
        ),
        MemoryPointAnnotation(
            address = 0x1CEEu,
            label = "THE 'STOP' COMMAND ROUTINE",
            description = "The command routine for STOP contains only a call to the error handling routine.",
        ),
        MemoryPointAnnotation(
            address = 0x1CF0u,
            label = "THE 'IF' COMMAND ROUTINE",
            description = "On entry the value of the expression between the IF and the THEN is the 'last value' on the calculator stack. If this is logically true then the next statement is considered; otherwise the line is considered to have been finished.",
        ),
        MemoryPointAnnotation(
            address = 0x1D03u,
            label = "THE 'FOR' COMMAND ROUTINE",
            description = "This command routine is entered with the VALUE and the LIMIT of the FOR statement already on the top of the calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x1D86u,
            label = "THE 'LOOK-PROG' SUBROUTINE",
            description = "This subroutine is used to find occurrences of either DATA, DEF FN or NEXT. On entry the appropriate token code is in the E register and the HL register pair points to the start of the search area.",
        ),
        MemoryPointAnnotation(
            address = 0x1DABu,
            label = "THE 'NEXT' COMMAND ROUTINE",
            description = "The 'variable in assignment' has already been determined (see CLASS-04,1C6C); and it remains to change the VALUE as required.",
        ),
        MemoryPointAnnotation(
            address = 0x1DDAu,
            label = "THE 'NEXT-LOOP' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1DECu,
            label = "THE 'READ' COMMAND ROUTINE",
            description = "The READ command allows for the reading of a DATA list and has an effect similar to a series of LET statements. Each assignment within a single READ statement is dealt with in turn. The system variable X-PTR is used as a storage location for the pointer to the READ statement whilst CH-ADD is used to step along the DATA list.",
        ),
        MemoryPointAnnotation(
            address = 0x1E27u,
            label = "THE 'DATA' COMMAND ROUTINE",
            description = "During syntax checking a DATA statement is checked to ensure that it contains a series of valid expressions, separated by commas. But in 'run-time' the statement is passed by.",
        ),
        MemoryPointAnnotation(
            address = 0x1E39u,
            label = "THE 'PASS-BY' SUBROUTINE",
            description = "On entry the A register will hold either the token 'DATA' or the token 'DEF FN' depending on the type of statement that is being 'passedby'.",
        ),
        MemoryPointAnnotation(
            address = 0x1E42u,
            label = "THE 'RESTORE' COMMAND ROUTINE",
            description = "The operand for a RESTORE command is taken as a line number, zero being used if no operand is given. The REST-RUN entry point is used by the RUN command routine.",
        ),
        MemoryPointAnnotation(
            address = 0x1E4Fu,
            label = "THE 'RANDOMIZE' COMMAND ROUTINE",
            description = "Once again the operand is compressed into the BC register pair and transferred to the required system variable. However if the operand is zero the value in FRAMES1 and FRAMES2 is used instead.",
        ),
        MemoryPointAnnotation(
            address = 0x1E5Fu,
            label = "THE 'CONTINUE' COMMAND ROUTINE",
            description = "The required line number and statement number within that line are made the object of a jump.",
        ),
        MemoryPointAnnotation(
            address = 0x1E67u,
            label = "THE 'GO TO' COMMAND ROUTINE",
            description = "The operand of a GO TO ought to be a line number in the range '1' to '9999' but the actual test is against an upper value of '61439'.",
        ),
        MemoryPointAnnotation(
            address = 0x1E7Au,
            label = "THE 'OUT' COMMAND ROUTINE",
            description = "The two parameters for the OUT instruction are fetched from the calculator stack and used as directed.",
        ),
        MemoryPointAnnotation(
            address = 0x1E80u,
            label = "THE 'POKE' COMMAND ROUTINE",
            description = "In a similar manner the POKE operation is performed.",
        ),
        MemoryPointAnnotation(
            address = 0x1E85u,
            label = "THE 'TWO-PARAM' SUBROUTINE",
            description = "The topmost parameter on the calculator stack must be compressible into a single register. It is two's complemented if it is negative. The second parameter must be compressible into a register pair.",
        ),
        MemoryPointAnnotation(
            address = 0x1E94u,
            label = "THE 'FIND INTEGERS' SUBROUTINE",
            description = "The 'last value' on the calculator stack is fetched and compressed into a single register or a register pair by entering at FIND-INT1 AND FIND-INT2 respectively.",
        ),
        MemoryPointAnnotation(
            address = 0x1EA1u,
            label = "THE 'RUN' COMMAND ROUTINE",
            description = "The parameter of the RUN command is passed to NEWPPC by calling the GO TO command routine. The operations of 'RESTORE 0' and 'CLEAR 0' are then performed before a return is made.",
        ),
        MemoryPointAnnotation(
            address = 0x1EACu,
            label = "THE 'CLEAR' COMMAND ROUTINE",
            description = "This routine allows for the variables area to be cleared, the display area cleared and RAMTOP moved. In consequence of the last operation the machine stack is rebuilt thereby having the effect of also clearing the GO SUB stack.",
        ),
        MemoryPointAnnotation(
            address = 0x1EEDu,
            label = "THE 'GO SUB' COMMAND ROUTINE",
            description = "The present value of PPC and the incremented value of SUBPPC are stored on the GO SUB stack.",
        ),
        MemoryPointAnnotation(
            address = 0x1F05u,
            label = "THE 'TEST-ROOM' SUBROUTINE",
            description = "A series of tests is performed to ensure that there is sufficient free memory available for the task being undertaken.",
        ),
        MemoryPointAnnotation(
            address = 0x1F1Au,
            label = "THE 'FREE MEMORY' SUBROUTINE",
            description = "There is no BASIC command 'FRE' in the SPECTRUM but there is a subroutine for performing such a task. An estimate of the amount of free space can be found at any time by using: 'PRINT 65536-USR 7962'",
        ),
        MemoryPointAnnotation(
            address = 0x1F23u,
            label = "THE 'RETURN' COMMAND ROUTINE",
            description = "The line number and the statement number that are to be made the object of a 'return' are fetched from the GO SUB stack.",
        ),
        MemoryPointAnnotation(
            address = 0x1F3Au,
            label = "THE 'PAUSE' COMMAND ROUTINE",
            description = "The period of the PAUSE is determined by counting the number of maskable interrupts as they occur every 1/50 th. of a second. A PAUSE is finished either after the appropriate number of interrupts or by the system Variable FLAGS indicating that a key has been pressed.",
        ),
        MemoryPointAnnotation(
            address = 0x1F54u,
            label = "THE 'BREAK-KEY' SUBROUTINE",
            description = "This subroutine is called in several instances to read the BREAK key. The carry flag is returned reset only if the SHIFT and the BREAK keys are both being pressed.",
        ),
        MemoryPointAnnotation(
            address = 0x1F60u,
            label = "THE 'DEF FN' COMMAND ROUTINE",
            description = "During syntax checking a DEF FN statement is checked to ensure that it has the correct form. Space is also made available for the result of evaluating the function. But in 'run-time' a DEF FN statement is passed-by.",
        ),
        MemoryPointAnnotation(
            address = 0x1FC3u,
            label = "THE 'UNSTACK-Z' SUBROUTINE",
            description = "This subroutine is called in several instances in order to 'return early' from a subroutine when checking syntax. The reason for this is to avoid actually printing characters or passing values to/from the calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x1FC9u,
            label = "THE 'LPRINT and PRINT' COMMAND ROUTINES",
        ),
        MemoryPointAnnotation(
            address = 0x1FDFu,
            label = "THE 'PRINT CONTROLLING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1FF5u,
            label = "THE 'PRINT A CARRIAGE RETURN' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x1FFCu,
            label = "THE 'PRINT ITEMS' SUBROUTINE",
            description = "This subroutine is called from the PRINT, LPRINT and INPUT command routines. The various types of print item are identified and printed.",
        ),
        MemoryPointAnnotation(
            address = 0x2045u,
            label = "THE 'END OF PRINTING' SUBROUTINE",
            description = "The zero flag will be set if no further printing is to be done.",
        ),
        MemoryPointAnnotation(
            address = 0x204Eu,
            label = "THE 'PRINT POSITION' SUBROUTINE",
            description = "The various position controlling characters are considered by this subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x2070u,
            label = "THE 'ALTER STREAM' SUBROUTINE",
            description = "This subroutine is called whenever there is the need to consider whether the user wishes to use a different stream.",
        ),
        MemoryPointAnnotation(
            address = 0x2089u,
            label = "THE 'INPUT' COMMAND ROUTINE",
            description = "This routine allows for values entered from the keyboard to be assigned to variables. It is also possible to have print items embedded in the INPUT statement and these items are printed in the lower part of the display.",
        ),
        MemoryPointAnnotation(
            address = 0x21B9u,
            label = "THE 'IN-ASSIGN' SUBROUTINE",
            description = "This subroutine is called twice for each INPUT value. Once with the syntax/run flag reset (syntax) and once with it set (run).",
        ),
        MemoryPointAnnotation(
            address = 0x21D6u,
            label = "THE 'IN-CHAN-K' SUBROUTINE",
            description = "This subroutine returns with the zero flag reset only if channel 'K' is being used.",
        ),
        MemoryPointAnnotation(
            address = 0x21E1u,
            label = "THE 'COLOUR ITEM' ROUTINES",
            description = "This set of routines can be readily divided into two parts: i. The embedded colour item' handler. ii. The 'colour system variable' handler. i. Embedded colour items are handled by calling the PRINT-OUT subroutine as required. A loop is entered to handle each item in turn. The entry point is at CO-TEMP-2.",
        ),
        MemoryPointAnnotation(
            address = 0x226Cu,
            label = "THE 'CO-CHANGE' SUBROUTINE",
            description = "This subroutine is used to 'impress' upon a system variable the 'nature' of the bits in the A register, The B register holds a mask that shows which bits are to be 'copied over' from A to (HL).",
        ),
        MemoryPointAnnotation(
            address = 0x2294u,
            label = "THE 'BORDER' COMMAND ROUTINE",
            description = "The parameter of the BORDER command is used with an OUT command to actually alter the colour of the border. The parameter is then saved in the system variable BORDCR.",
        ),
        MemoryPointAnnotation(
            address = 0x22AAu,
            label = "THE 'PIXEL ADDRESS' SUBROUTINE",
            description = "This subroutine is called by the POINT subroutine and by the PLOT command routine. Is is entered with the co-ordinates of a pixel in the BC register pair and returns with HL holding the address of the display file byte which contains that pixel and A pointing to the position of the pixel within the byte.",
        ),
        MemoryPointAnnotation(
            address = 0x22CBu,
            label = "THE 'POINT' SUBROUTINE",
            description = "This subroutine is called by the POINT function in SCANNING. It is entered with the co-ordinates of a pixel on the calculator stack, and returns a last value of 1 if that pixel is ink colour, and 0 if it is paper colour.",
        ),
        MemoryPointAnnotation(
            address = 0x22DCu,
            label = "THE 'PLOT' COMMAND ROUTINE",
            description = "This routine consists of a main subroutine plus one line to call it and one line to exit from it. The main routine is used twice by CIRCLE and the subroutine is called by DRAW. The routine is entered with the co-ordinates of a pixel on the calculator stack. It finds the address of that pixel and plots it, taking account of the status of INVERSE and OVER held in the P-FLAG.",
        ),
        MemoryPointAnnotation(
            address = 0x2307u,
            label = "THE 'STK-TO-BC' SUBROUTINE",
            description = "This subroutine loads two floating point numbers into the BC register pair. It is thus used to pick up parameters in the range +00-+FF. It also obtains in DE the 'diagonal move' values (+/-1,+/-1) which are used in the line drawing subroutine of DRAW.",
        ),
        MemoryPointAnnotation(
            address = 0x2314u,
            label = "THE 'STK-TO-A' SUBROUTINE",
            description = "This subroutine loads the A register with the floating point number held at the top of the calculator stack. The number must be in the range 00-FF.",
        ),
        MemoryPointAnnotation(
            address = 0x2320u,
            label = "THE 'CIRCLE' COMMAND ROUTINE",
            description = "This routine draws an approximation to the circle with centre co-ordinates X and Y and radius Z. These numbers are rounded to the nearest integer before use. Thus Z must be less than 87.5, even when (X,Y) is in the centre of the screen. The method used is to draw a series of arcs approximated by straight lines. It is illustrated in the BASIC program in the appendix. The notation of that program is followed here. CIRCLE has four parts: I. Tests the radius. If its modulus is less than 1, just plot X,Y; II. Calls CD-PRMS-1 at 2470-24B6, which is used to set the initial parameters for both CIRCLE and DRAW; III. Sets up the remaining parameters for CIRCLE, including the initial displacement for the first 'arc' (a straight line in fact); IV. Jumps into DRAW to use the arc-drawing loop at 2420-24FA. Parts i. to iii. will now be explained in turn. i. 2320-23AA. The radius, say Z', is obtained from the calculator stack. Its modulus Z is formed and used from now on. If Z is less than 1, it is deleted from the stack and the point X,Y is plotted by a jump to PLOT.",
        ),
        MemoryPointAnnotation(
            address = 0x2382u,
            label = "THE 'DRAW' COMMAND ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x247Du,
            label = "THE 'INITIAL PARAMETERS' SUBROUTINE",
            description = "This subroutine is called by both CIRCLE and DRAW to set their initial parameters. It is called by CIRCLE with X, Y and the radius Z on the top of the stack, reading upwards. It is called by DRAW with its own X, Y, SIN (G/2) and Z, as defined in DRAW i. above, on the top of the stack. In what follows the stack is only shown from Z upwards. The subroutine returns in B the arc-count A as explained in both CIRCLE and DRAW above, and in mem-0 to mem-5 the quantities G/A, SIN (G/2*A), 0, COS (G/A), SIN (G/A) and G. For a circle, G must be taken to be equal to 2*PI.",
        ),
        MemoryPointAnnotation(
            address = 0x24B7u,
            label = "THE 'LINE-DRAWING' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x24FBu,
            label = "THE 'SCANNING' SUBROUTINE",
            description = "This subroutine is used to produce an evaluation result of the 'next expression'. The result is returned as the 'last value' on the calculator stack. For a numerical result, the last value will be the actual floating point number. However, for a string result the last value will consist of a set of parameters. The first of the five bytes is unspecified, the second and third bytes hold the address of the start of the string and the fourth and fifth bytes hold the length of the string. Bit 6 of FLAGS is set for a numeric result and reset for a string result. When a next expression consists of only a single operand, e.g. ... A ..., ... RND ..., ... A$ (4, 3 TO 7) ... , then the last value is simply the value that is obtained from evaluating the operand. However when the next expression contains a function and an operand, e.g. ... CHR$ A..., ... NOT A ... , SIN 1 ..., the operation code of the function is stored on the machine stack until the last value of the operand has been calculated. This last value is then subjected to the appropriate operation to give a new last value. In the case of there being an arithmetic or logical operation to be performed, e.g. ... A+B ... , A*B ..., ... A=B ... , then both the last value of the first argument and the operation code have to be kept until the last value of the second argument has been found. Indeed the calculation of the last value of the second argument may also involve the storing of last values and operation codes whilst the calculation is being performed. It can therefore be shown that as a complex expression is evaluated, e.g. ... CHR$ (T+A 26*INT ((T+A)/26)+65)..., a hierarchy of operations yet to be performed is built up until the point is reached from which it must be dismantled to produce the final last value. Each operation code has associated with it an appropriate priority code and operations of higher priority are always performed before those of lower priority. The subroutine begins with the A register being set to hold the first character of the expression and a starting priority marker zero being put on the machine stack.",
        ),
        MemoryPointAnnotation(
            address = 0x250Fu,
            label = "THE 'SCANNING QUOTES' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2522u,
            label = "THE 'SCANNING TWO CO-ORDINATES' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2530u,
            label = "THE 'SYNTAX-Z' SUBROUTINE",
            description = "At this point the 'SYNTAX-Z' subroutine is interpolated. It is called 32 times, with a saving of just one byte each call. A simple test of bit 7 of FLAGS will give the zero flag reset during execution and set during syntax checking. i.e. SYNTAX gives Z set.",
        ),
        MemoryPointAnnotation(
            address = 0x2535u,
            label = "THE 'SCANNING SCREEN$' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2580u,
            label = "THE 'SCANNING ATTRIBUTES' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2596u,
            label = "THE SCANNING FUNCTION TABLE",
            description = "This table contains 8 functions and 4 operators. It thus incorporates 5 new Spectrum functions and provides a neat way of accessing some functions and operators which already existed on the ZX81. location code offset name address of handling routine",
        ),
        MemoryPointAnnotation(
            address = 0x25AFu,
            label = "THE 'SCANNING UNARY PLUS' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x25B3u,
            label = "THE 'SCANNING QUOTE' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x25E8u,
            label = "THE 'SCANNING BRACKET' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x25F5u,
            label = "THE 'SCANNING FN' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x25F8u,
            label = "THE 'SCANNING RND' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2627u,
            label = "THE 'SCANNING PI' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2634u,
            label = "THE' SCANNING INKEY$' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2668u,
            label = "THE 'SCANNING SCREEN$' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2672u,
            label = "THE 'SCANNING ATTR' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x267Bu,
            label = "THE 'SCANNING POINT' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2684u,
            label = "THE 'SCANNING ALPHANUMERIC' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x268Du,
            label = "THE 'SCANNING DECIMAL' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x26C9u,
            label = "THE 'SCANNING VARIABLE' ROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2795u,
            label = "THE TABLE OF OPERATORS",
            description = "operator operator location code code operator location code code operator",
        ),
        MemoryPointAnnotation(
            address = 0x27B0u,
            label = "THE TABLE OF PRIORITIES",
        ),
        MemoryPointAnnotation(
            address = 0x27BDu,
            label = "THE 'SCANNING FUNCTION' SUBROUTINE",
            description = "This subroutine is called by the 'scanning FN routine' to evaluate a user defined function which occurs in a BASIC line. The subroutine can be considered in four stages: I. The syntax of the FN statement is checked during syntax checking. II. During line execution, a search is made of the program area for a DEF FN statement, and the names of the functions are compared, until a match is found or an error is reported. III. The arguments of the FN are evaluated by calls to SCANNING. IV. The function itself is evaluated by calling SCANNING, which in turn calls LOOK-VARS and so the 'STACK FUNCTION ARGUMENT' subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x28ABu,
            label = "THE 'FUNCTION SKIPOVER' SUBROUTINE",
            description = "This subroutine is used by FN and by STK-F-ARG to move HL along the DEF FN statement while leaving CM-ADD undisturbed, as it points along the FN statement.",
        ),
        MemoryPointAnnotation(
            address = 0x28B2u,
            label = "THE 'LOOK-VARS' SUBROUTINE",
            description = "This subroutine is called whenever a search of the variables area or of the arguments of a DEF FN statement is required. The subroutine is entered with the system variable CH-ADD pointing to the first letter of the name of the variable whose location is being sought. The name will be in the program area or the work space. The subroutine initially builds up a discriminator byte, in the C register, that is based on the first letter of the variable's name. Bits 5 & 6 of this byte indicate the type of the variable that is being handled. The B register is used as a bit register to hold flags.",
        ),
        MemoryPointAnnotation(
            address = 0x2951u,
            label = "THE 'STACK FUNCTION ARGUMENT' SUBROUTINE",
            description = "This subroutine is called by LOOK-VARS when DEFADD-hi in non-zero, to make a search of the arguments area of a DEF FN statement, before searching in the variables area. If the variable is found in the DEF FN statement, then the parameters of a string variable are stacked and a signal is given that there is no need to call STK/VAR. But it is left to SCANNING to stack the value of a numerical variable at 26DA in the usual way.",
        ),
        MemoryPointAnnotation(
            address = 0x2996u,
            label = "THE 'STK-VAR' SUBROUTINE",
            description = "This subroutine is usually used either to find the parameters that define an existing string entry in the variables area or to return in the HL register pair the base address of a particular element or an array of numbers. When called from DIM the subroutine only checks the syntax of the BASIC statement. Note that the parameters that define a string may be altered by calling SLICING if this should be specified. Initially the A and the B registers are cleared and bit 7 of the C register is tested to determine whether syntax is being checked.",
        ),
        MemoryPointAnnotation(
            address = 0x2A52u,
            label = "THE 'SLICING' SUBROUTINE",
            description = "The present string can be sliced using this subroutine. The subroutine is entered with the parameters of the string being present on the top of the calculator stack and in the registers A, B, C, D & E. Initially the SYNTAX/RUN flag is tested and the parameters of the string are fetched only if a line is being executed.",
        ),
        MemoryPointAnnotation(
            address = 0x2AB1u,
            label = "THE 'STK-STORE' SUBROUTINE",
            description = "This subroutine passes the values held in the A, B, C, D & E registers to the calculator stack. The stack thereby grows in size by 5 bytes with every call to this subroutine. The subroutine is normally used to transfer the parameters of strings but it is also used by STACK-BC and LOG (2^A) to transfer 'small integers' to the stack. Note that when storing the parameters of a string the first value stored (coming from the A register) will be a zero if the string comes from an array of strings or is a 'slice' of a string. The value will be '1' for a complete simple string. This 'flag' is used in the 'LET' command routine when the '1' signals that the old copy of the string is to be 'reclaimed'.",
        ),
        MemoryPointAnnotation(
            address = 0x2ACCu,
            label = "THE 'INT-EXP' SUBROUTINE",
            description = "This subroutine returns the result of evaluating the 'next expression' as an integer value held in the BC register pair. The subroutine also tests this result against a limit-value supplied in the HL register pair. The carry flag becomes set if there is an 'out of range' error. The A register is used as an 'error register' and holds +00 of there is no 'previous error' and +FF if there has been one.",
        ),
        MemoryPointAnnotation(
            address = 0x2AEEu,
            label = "THE 'DE,(DE+1)' SUBROUTINE",
            description = "This subroutine performs the construction LD DE,(DE+1) and returns HL pointing to 'DE+2'.",
        ),
        MemoryPointAnnotation(
            address = 0x2AF4u,
            label = "THE 'GET-HL*DE' SUBROUTINE",
            description = "Unless syntax is being checked this subroutine calls 'HL=HL*DE' which performs the implied construction. Overflow of the 16 bits available in the HL register pair gives the report 'out of memory'. This is not exactly the true situation but it implies that the memory is not large enough for the task envisaged by the programmer.",
        ),
        MemoryPointAnnotation(
            address = 0x2AFFu,
            label = "THE 'LET' COMMAND ROUTINE",
            description = "This is the actual assignment routine for the LET, READ and INPUT commands. When the destination variable is a 'newly declared variable' then DEST will point to the first letter of the variable's name as it occurs in the BASIC line. Bit 1 of FLAGX will be set. However if the destination variable 'exists already' then bit 1 of FLAGX will be reset and DEST will point for a numeric variable to the location before the five bytes of the 151 'old number'; and for a string variable to the first location of the 'old string'. The use of DEST in this manner applies to simple variables and to elements of arrays. Bit 0 of FLAGX is set if the destination variable is a 'complete' simple string variable. (Signalling delete the old copy.) Initially the current value of DEST is collected and bit 1 of FLAGS tested.",
        ),
        MemoryPointAnnotation(
            address = 0x2BA6u,
            label = "THE 'L-ENTER' SUBROUTINE",
            description = "This short subroutine is used to pass either a numeric value, from the calculator stack, or a string, from the work space, to its appropriate position in the variables area. The subroutine is therefore used for all except 'newly declared' simple strings and 'complete & existing' simple strings.",
        ),
        MemoryPointAnnotation(
            address = 0x2BAFu,
            label = "THE LET SUBROUTINE CONTINUES HERE",
            description = "When handling a 'complete & existing' simple string the new string is entered as if it were a 'newly declared' simple string before the existing version is 'reclaimed'.",
        ),
        MemoryPointAnnotation(
            address = 0x2BC6u,
            label = "THE 'L-STRING' SUBROUTINE",
            description = "The parameters of the 'new' string are fetched, sufficient room is made available for it and the string is then transferred.",
        ),
        MemoryPointAnnotation(
            address = 0x2BEAu,
            label = "THE 'L-FIRST' SUBROUTINE",
            description = "This subroutine is entered with the letter of the variable, suitably marked, in the A register. The letter overwrites the 'old 80-byte' in the variables area. The subroutine returns with the HL register pair pointing to the 'new 80-byte'.",
        ),
        MemoryPointAnnotation(
            address = 0x2BF1u,
            label = "THE 'STK-FETCH' SUBROUTINE",
            description = "This important subroutine collects the 'last value' from the calculator stack. The five bytes can be either a floating-point number, in 'short' or 'long' form, or set of parameters that define a string.",
        ),
        MemoryPointAnnotation(
            address = 0x2C02u,
            label = "THE 'DIM' COMMAND ROUTINE",
            description = "This routine establishes new arrays in the variables area. The routine starts by searching the existing variables area to determine whether there is an existing array with the same name. If such an array is found then it is 'reclaimed' before the new array is established. A new array will have all its elements set to zero, if it is a numeric array, or to 'spaces', if it is an array of strings.",
        ),
        MemoryPointAnnotation(
            address = 0x2C88u,
            label = "THE 'ALPHANUM' SUBROUTINE",
            description = "This subroutine returns with the carry flag set if the present value of the A register denotes a valid digit or letter.",
        ),
        MemoryPointAnnotation(
            address = 0x2C8Du,
            label = "THE 'ALPHA' SUBROUTINE",
            description = "This subroutine returns with the carry flag set if the present value of the A register denotes a valid letter of the alphabet.",
        ),
        MemoryPointAnnotation(
            address = 0x2C9Bu,
            label = "THE 'DECIMAL TO FLOATING POINT' SUBROUTINE",
            description = "As part of syntax checking decimal numbers that occur in a BASIC line are converted to their floating-point forms. This subroutine reads the decimal number digit by digit and gives its result as a 'last value' on the calculator stack. But first it deals with the alternative notation BIN, which introduces a sequence of 0's and 1's giving the binary representation of the required number.",
        ),
        MemoryPointAnnotation(
            address = 0x2D1Bu,
            label = "THE 'NUMERIC' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2D22u,
            label = "THE 'STK-DIGIT' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2D28u,
            label = "THE 'STACK-A' SUBROUTINE",
            description = "This subroutine gives the floating-point form for the absolute binary value currently held in the A register.",
        ),
        MemoryPointAnnotation(
            address = 0x2D2Bu,
            label = "THE 'STACK-BC' SUBROUTINE",
            description = "This subroutine gives the floating-point form for the absolute binary value currently held in the BC register pair. The form used in this and hence in the two previous subroutines as well is the one reserved in the Spectrum for small integers n, where -65535 <= n <= 65535. The first and fifth bytes are zero; the third and fourth bytes are the less significant and more significant bytes of the 16 bit integer n in two's complement form (if n is negative, these two bytes hold 65536+n); and the second byte is a sign byte, 00 for '+' and FF for '-'.",
        ),
        MemoryPointAnnotation(
            address = 0x2D3Bu,
            label = "THE 'INTEGER TO FLOATING-POINT' SUBROUTINE",
            description = "This subroutine returns a 'last value' on the calculator stack that is the result of converting an integer in a BASIC line, i.e. the integer part of the decimal number or the line number, to its floating-point form. Repeated calls to CH-ADD+1 fetch each digit of the integer in turn. An exit is made when a code that does not represent a digit has been fetched.",
        ),
        MemoryPointAnnotation(
            address = 0x2D4Fu,
            label = "THE 'E-FORMAT TO FLOATING-POINT' SUBROUTINE (offset +3C)",
        ),
        MemoryPointAnnotation(
            address = 0x2D7Fu,
            label = "THE 'INT-FETCH' SUBROUTINE",
            description = "This subroutine collects in DE a small integer n (-65535<=n<=65535) from the location addressed by HL: i.e. n is normally the first (or second) number at the top of the calculator stack; but HL can alls access (by exchange with DE) a number which has been deleted from the stack. The subroutine does not itself delete the number from the stack or from memory; it returns HL pointing to the fourth byte of the number in its original position.",
        ),
        MemoryPointAnnotation(
            address = 0x2D8Cu,
            label = "THE 'POSITIVE-INT-STORE' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2D8Eu,
            label = "THE 'INT-STORE' SUBROUTINE",
            description = "This subroutine stores a small integer n (-65535<=n<=65535) in the location addressed by HL and the four following locations: i.e. n replaces the first (or second) number at the top of the calculator stack. The subroutine returns HL pointing to the first byte of n on the stack. 2D8C P-INT-STO LD C,+00 This entry point would store a number known to be positive",
        ),
        MemoryPointAnnotation(
            address = 0x2DA2u,
            label = "THE 'FLOATING-POINT TO BC' SUBROUTINE",
            description = "This subroutine is called from four different places for various purposes and is used to compress the floating-point 'last value' into the BC register pair. If the result is too large, i.e. greater than 65536 decimal, then the subroutine returns with the carry flag set. If the 'last value' is negative then the zero flag is reset. The low byte of the result is also copied to the A register.",
        ),
        MemoryPointAnnotation(
            address = 0x2DC1u,
            label = "THE 'LOG(2&#8593;A)' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2DD5u,
            label = "THE 'FLOATING-POINT TO A' SUBROUTINE",
            description = "This short but vital subroutine is called at least 8 times for various purposes. It uses the last but one subroutine, FP-TO-BC, to get the 'last value' into the A register where this is possible. It therefore tests whether the modulus of the number rounds to more than 255 and if it does the subroutine returns with the carry flag set. Otherwise it returns with the modulus of the number, rounded to the nearest integer, in the A register, and the zero flag set to imply that the number was positive, or reset to imply that it was negative.",
        ),
        MemoryPointAnnotation(
            address = 0x2DE3u,
            label = "THE 'PRINT A FLOATING-POINT NUMBER' SUBROUTINE",
            description = "This subroutine is called by the PRINT command routine at 2039 and by STR$ at 3630, which converts to a string the number as it would be printed. The subroutine prints x, the 'last value' on the calculator stack. The print format never occupies more than 14 spaces. The 8 most significant digits of x, correctly rounded, are stored in an ad hoc print buffer in mem-3 and mem-4. Small numbers, numerically less than 1, and large numbers, numerically greater than 2 ^ 27, are dealt with separately. The former are multiplied by 10 ^ n, where n is the approximate number of leading zeros after the decimal, while the latter are divided by 10 ^ (n-7), where n is the approximate number of digits before the decimal. This brings all numbers into the middle range, and the numbers of digits required before the decimal is built up in the second byte of mem-5. Finally the printing is done, using E-format if there are more than 8 digits before the decimal or, for small numbers, more than 4 leading zeros after the decimal. The following program shows the range of print formats: 10 FOR a=-11 TO 12: PRINT SGN a*9^a,: NEXT a i. First the sign of x is taken care of: If X is negative, the subroutine jumps to PF-NEGATIVE, takes ABS x and prints the minus sign. If x is zero, x is deleted from the calculator stack, a '0' is printed and a return is made from the subroutine. If x is positive, the subroutine just continues.",
        ),
        MemoryPointAnnotation(
            address = 0x2F8Bu,
            label = "THE 'CA=10*A+C' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2F9Bu,
            label = "THE 'PREPARE TO ADD' SUBROUTINE",
        ),
        MemoryPointAnnotation(
            address = 0x2FBAu,
            label = "THE 'FETCH TWO NUMBERS' SUBROUTINE",
            description = "This subroutine is called by ADDITION, MULTIPLICATION and DIVISION to get two numbers from the calculator stack and put them into the register, including the exchange registers. On entry to the subroutine the HL register pair points to the first byte of the first number and the DE register pair points to the first byte of the second number. When the subroutine is called from MULTIPLICATION or DIVISION the sign of the result is saved in the second byte of the first number. 174",
        ),
        MemoryPointAnnotation(
            address = 0x2FDDu,
            label = "THE 'SHIFT ADDEND' SUBROUTINE",
            description = "This subroutine shifts a floating-point number up to 32 decimal, Hex.20, places right to line it up properly for addition. The number with the smaller exponent has been put in the addend position before this subroutine is called. Any overflow to the right, into the carry, is added back into the number. If the exponent difference is greater than 32 decimal, or the carry ripples right back to the beginning of the number then the number is set to zero so that the addition will not alter the other number (the augend).",
        ),
        MemoryPointAnnotation(
            address = 0x3004u,
            label = "THE 'ADD-BACK' SUBROUTINE",
            description = "This subroutine adds back into the number any carry which has overflowed to the right. In the extreme case, the carry ripples right back to the left of the number. When this subroutine is called during addition, this ripple means that a mantissa of 0.5 was shifted a full 32 places right, and the addend will now be set to zero; when called from MULTIPLICATION, it means that the exponent must be incremented, and this may result in overflow.",
        ),
        MemoryPointAnnotation(
            address = 0x300Fu,
            label = "THE 'SUBTRACTION' OPERATION (offset +03)",
        ),
        MemoryPointAnnotation(
            address = 0x3014u,
            label = "THE 'ADDITION' OPERATION (offset +0F)",
        ),
        MemoryPointAnnotation(
            address = 0x30A9u,
            label = "THE 'HL=HL*DE' SUBROUTINE",
            description = "This subroutine is called by 'GET-HL*DE' and by 'MULTIPLICATION' to perform the 16-bit multiplication as stated. Any overflow of the 16 bits available is dealt with on return from the subroutine.",
        ),
        MemoryPointAnnotation(
            address = 0x30C0u,
            label = "THE 'PREPARE TO MULTIPLY OR DIVIDE' SUBROUTINE",
            description = "This subroutine prepares a floating-point number for multiplication or division, returning with carry set if the number is zero, getting the sign of the result into the A register, and replacing the sign bit in the number by the true numeric bit, 1.",
        ),
        MemoryPointAnnotation(
            address = 0x30CAu,
            label = "THE 'MULTIPLICATION' OPERATION (offset +04)",
        ),
        MemoryPointAnnotation(
            address = 0x31AFu,
            label = "THE 'DIVISION' OPERATION (offset +05)",
        ),
        MemoryPointAnnotation(
            address = 0x3214u,
            label = "THE 'INTEGER TRUNCATION TOWARDS ZERO' SUBROUTINE (offset +3A)",
        ),
        MemoryPointAnnotation(
            address = 0x3293u,
            label = "THE 'RE-STACK TWO' SUBROUTINE",
            description = "This subroutine is called to re-stack two ‘small integers’ in full five byte floating-point form for the binary operations of addition, multiplication and division. It does so by calling the following subroutine twice.",
        ),
        MemoryPointAnnotation(
            address = 0x3297u,
            label = "THE 'RE-STACK' SUBROUTINE (offset +3D)",
        ),
        MemoryPointAnnotation(
            address = 0x32C5u,
            label = "THE TABLE OF CONSTANTS",
            description = "This first table holds the five useful and frequently needed numbers zero, one, a half, a half of pi and ten. The numbers are held in a condensed form which is expanded by the STACK LITERALS subroutine, see below, to give the required floating-point form. data: constant when expanded gives: exp. mantissa: (Hex.)",
        ),
        MemoryPointAnnotation(
            address = 0x32D7u,
            label = "THE TABLE OF ADDRESSES",
        ),
        MemoryPointAnnotation(
            address = 0x335Bu,
            label = "THE 'CALCULATE' SUBROUTINE",
            description = "This subroutine is used to perform floating-point calculations. These can be considered to be of three types: I. Binary operations, e.g. addition, where two numbers in floating-point form are added together to give one 'last value'. II. Unary operations, e.g. sin, where the 'last value' is changed to give the appropriate function result as a new 'last value'. III. Manipulatory operations, e.g. st-mem-0, where the 'last value' is copied to the first five bytes of the calculator's memory area. The operations to be performed are specified as a series of data-bytes, the literals, that follow an RST 0028 instruction that calls this subroutine. The last literal in the list is always '38' which leads to an end to the whole operation. In the case of a single operation needing to be performed, the operation offset can be passed to the CALCULATOR in the B register, and operation '3B', the SINGLE CALCULATION operation, performed. It is also possible to call this subroutine recursively, i.e. from within itself, and in such a case it is possible to use the system variable BREG as a counter that controls how many operations are performed before returning. The first part of this subroutine is complicated but essentially it performs the two tasks of setting the registers to hold their required values, and to produce an offset, and possibly a parameter, from the literal that is currently being considered. The offset is used to index into the calculator's table of addresses, see above, to find the required subroutine address. The parameter is used when the multi-purpose subroutines are called. Note: A floating-point number may in reality be a set of string parameters.",
        ),
        MemoryPointAnnotation(
            address = 0x33A2u,
            label = "THE 'SINGLE OPERATION' SUBROUTINE (offset +3B)",
        ),
        MemoryPointAnnotation(
            address = 0x33A9u,
            label = "THE 'TEST 5-SPACES' SUBROUTINE",
            description = "This subroutine tests whether there is sufficient room in memory for another 5-byte floating-point number to be added to the calculator stack.",
        ),
        MemoryPointAnnotation(
            address = 0x33B4u,
            label = "THE 'STACK NUMBER' SUBROUTINE",
            description = "This subroutine is called by BEEP and SCANNING twice to copy STKEND to DE, move a floating-point number to the calculator stack, and reset STKEND from DE. It calls 'MOVE-FP' to do the actual move.",
        ),
        MemoryPointAnnotation(
            address = 0x33C0u,
            label = "THE 'MOVE A FLOATING-POINT NUMBER' SUBROUTINE (offset +31)",
        ),
        MemoryPointAnnotation(
            address = 0x33C6u,
            label = "THE 'STACK LITERALS' SUBROUTINE (offset +34)",
        ),
        MemoryPointAnnotation(
            address = 0x33F7u,
            label = "THE 'SKIP CONSTANTS' SUBROUTINE",
            description = "This subroutine is entered with the HL register pair holding the base address of the calculator's table of constants and the A register holding a parameter that shows which of the five constants is being requested. The subroutine performs the null operations of loading the five bytes of each unwanted constant into the locations 0000, 0001, 0002, 0003 and 0004 at the beginning of the ROM until the requested constant is reached. The subroutine returns with the HL register pair holding the base address of the requested constant within the table of constants.",
        ),
        MemoryPointAnnotation(
            address = 0x3406u,
            label = "THE 'MEMORY LOCATION' SUBROUTINE",
            description = "This subroutine finds the base address for each five byte portion of the calculator's memory area to or from which a floating-point number is to be moved from or to the calculator stack. It does this operation by adding five times the parameter supplied to the base address for the area which is held in the HL register pair. Note that when a FOR-NEXT variable is being handled then the pointers are changed so that the variable is treated as if it were the calculator's memory area (see address 1D20).",
        ),
        MemoryPointAnnotation(
            address = 0x340Fu,
            label = "THE 'GET FROM MEMORY AREA' SUBROUTINE (offset +41)",
        ),
        MemoryPointAnnotation(
            address = 0x341Bu,
            label = "THE 'STACK A CONSTANT' SUBROUTINE (offset +3F)",
        ),
        MemoryPointAnnotation(
            address = 0x342Du,
            label = "THE 'STORE IN MEMORY AREA' SUBROUTINE (offset +40)",
        ),
        MemoryPointAnnotation(
            address = 0x343Cu,
            label = "THE 'EXCHANGE' SUBROUTINE (offset +01)",
        ),
        MemoryPointAnnotation(
            address = 0x3449u,
            label = "THE 'SERIES GENERATOR' SUBROUTINE (offset +3E)",
        ),
        MemoryPointAnnotation(
            address = 0x346Au,
            label = "THE 'ABSOLUTE MAGNITUDE' FUNCTION (offset +2A)",
        ),
        MemoryPointAnnotation(
            address = 0x346Eu,
            label = "THE 'UNARY MINUS' OPERATION (offset +1B)",
        ),
        MemoryPointAnnotation(
            address = 0x3492u,
            label = "THE 'SIGNUM' FUNCTION (offset +29)",
        ),
        MemoryPointAnnotation(
            address = 0x34A5u,
            label = "THE 'IN' FUNCTION (offset +2C)",
        ),
        MemoryPointAnnotation(
            address = 0x34ACu,
            label = "THE 'PEEK' FUNCTION (offset +2B)",
        ),
        MemoryPointAnnotation(
            address = 0x34B3u,
            label = "THE 'USR' FUNCTION (offset +2D)",
        ),
        MemoryPointAnnotation(
            address = 0x34BCu,
            label = "THE 'USR STRING' FUNCTION (offset +19)",
        ),
        MemoryPointAnnotation(
            address = 0x34E9u,
            label = "THE 'TEST-ZERO' SUBROUTINE",
            description = "This subroutine is called at least nine times to test whether a floating-point number is zero. This test requires that the first four bytes of the number should each be zero. The subroutine returns with the carry flag set if the number was in fact zero.",
        ),
        MemoryPointAnnotation(
            address = 0x34F9u,
            label = "THE 'GREATER THAN ZERO' OPERATION (offset +37)",
        ),
        MemoryPointAnnotation(
            address = 0x3501u,
            label = "THE 'NOT' FUNCTION (offset +30)",
        ),
        MemoryPointAnnotation(
            address = 0x3506u,
            label = "THE 'LESS THAN ZERO' OPERATION (offset +36)",
        ),
        MemoryPointAnnotation(
            address = 0x350Bu,
            label = "THE 'ZERO OR ONE' SUBROUTINE",
            description = "This subroutine sets the 'last value' to zero if the carry flag is reset and to one if it is set. When called from 'E-TO-FP' however it creates the zero or one not on the stack but in mem-0.",
        ),
        MemoryPointAnnotation(
            address = 0x351Bu,
            label = "THE 'OR' OPERATION (offset +07)",
        ),
        MemoryPointAnnotation(
            address = 0x3524u,
            label = "THE 'NUMBER AND NUMBER' OPERATION (offset +08)",
        ),
        MemoryPointAnnotation(
            address = 0x352Du,
            label = "THE 'STRING AND NUMBER' OPERATION (offset +10)",
        ),
        MemoryPointAnnotation(
            address = 0x353Bu,
            label = "THE 'COMPARISON' OPERATIONS (offsets +09 to +0E, +11 to +16)",
        ),
        MemoryPointAnnotation(
            address = 0x359Cu,
            label = "THE 'STRING CONCATENATION' OPERATION (offset +17)",
        ),
        MemoryPointAnnotation(
            address = 0x35BFu,
            label = "THE 'STK-PNTRS' SUBROUTINE",
            description = "This subroutine resets the HL register pair to point to the first byte of the 'last value', i.e. STKEND-5, and the DE register pair to point one-past the 'last value', i.e. STKEND.",
        ),
        MemoryPointAnnotation(
            address = 0x35C9u,
            label = "THE 'CHR$' FUNCTION (offset +2F)",
        ),
        MemoryPointAnnotation(
            address = 0x35DEu,
            label = "THE 'VAL' AND 'VAL$' FUNCTIONS (offsets +18, +1D)",
        ),
        MemoryPointAnnotation(
            address = 0x361Fu,
            label = "THE 'STR$' FUNCTION (offset +2E)",
        ),
        MemoryPointAnnotation(
            address = 0x3645u,
            label = "THE 'READ-IN' SUBROUTINE (offset +1A)",
        ),
        MemoryPointAnnotation(
            address = 0x3669u,
            label = "THE 'CODE' FUNCTION (offset +1C)",
        ),
        MemoryPointAnnotation(
            address = 0x3674u,
            label = "THE 'LEN' FUNCTION (offset +1E)",
        ),
        MemoryPointAnnotation(
            address = 0x367Au,
            label = "THE 'DECREASE THE COUNTER' SUBROUTINE (offset +35)",
        ),
        MemoryPointAnnotation(
            address = 0x3686u,
            label = "THE 'JUMP' SUBROUTINE (offset +33)",
        ),
        MemoryPointAnnotation(
            address = 0x368Fu,
            label = "THE 'JUMP ON TRUE' SUBROUTINE (offset +00)",
        ),
        MemoryPointAnnotation(
            address = 0x369Bu,
            label = "THE 'END-CALC' SUBROUTINE (offset +38)",
        ),
        MemoryPointAnnotation(
            address = 0x36A0u,
            label = "THE 'MODULUS' SUBROUTINE (offset +32)",
        ),
        MemoryPointAnnotation(
            address = 0x36AFu,
            label = "THE 'INT' FUNCTION (offset +27)",
        ),
        MemoryPointAnnotation(
            address = 0x36C4u,
            label = "THE 'EXPONENTIAL' FUNCTION (offset +26)",
        ),
        MemoryPointAnnotation(
            address = 0x3713u,
            label = "THE 'NATURAL LOGARITHM' FUNCTION (offset +25)",
        ),
        MemoryPointAnnotation(
            address = 0x3783u,
            label = "THE 'REDUCE ARGUMENT' SUBROUTINE (offset +39)",
        ),
        MemoryPointAnnotation(
            address = 0x37AAu,
            label = "THE 'COSINE' FUNCTION (offset +20)",
        ),
        MemoryPointAnnotation(
            address = 0x37B5u,
            label = "THE 'SINE' FUNCTION (offset +1F)",
        ),
        MemoryPointAnnotation(
            address = 0x37DAu,
            label = "THE 'TAN' FUNCTION (offset +21)",
        ),
        MemoryPointAnnotation(
            address = 0x37E2u,
            label = "THE 'ARCTAN' FUNCTION (offset +24)",
        ),
        MemoryPointAnnotation(
            address = 0x3833u,
            label = "THE 'ARCSIN' FUNCTION (offset +22)",
        ),
        MemoryPointAnnotation(
            address = 0x3843u,
            label = "THE 'ARCCOS' FUNCTION (offset +23)",
        ),
        MemoryPointAnnotation(
            address = 0x384Au,
            label = "THE 'SQUARE ROOT' FUNCTION (offset +28)",
        ),
        MemoryPointAnnotation(
            address = 0x3851u,
            label = "THE 'EXPONENTIATION' OPERATION (offset +06)",
        ),
        MemoryPointAnnotation(
            address = 0x386Eu,
            label = "Spare locations",
            description = "These locations are 'spare'. They all hold +FF.",
        ),
        MemoryPointAnnotation(
            address = 0x3D00u,
            label = "Character set",
            description = "These locations hold the 'character set'. There are 8 byte representations for all the characters with codes +20 (space) to +7F (©).",
        ),
    )
}
