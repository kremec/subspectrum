package com.subbyte.subspectrum.proc

import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instructions

class Processor {
    fun step() {
        val pc = Registers.specialPurposeRegisters.getPC()
        val instruction = Instructions.decode(pc.toUShort())

        Registers.specialPurposeRegisters.setPC((pc + instruction.bytes.size).toShort())

        instruction.execute()
    }

    fun run(steps: Int) {
        repeat(steps) { step() }
    }
}