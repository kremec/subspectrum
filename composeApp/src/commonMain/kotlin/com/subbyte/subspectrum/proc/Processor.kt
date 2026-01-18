package com.subbyte.subspectrum.proc

import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instructions

object Processor {
    var running = false
    fun step() {
        val pc = Registers.specialPurposeRegisters.getPC()
        val instruction = Instructions.decode(pc.toUShort())

        Registers.specialPurposeRegisters.setPC((pc + instruction.bytes.size).toShort())

        instruction.execute()
    }

    fun run(steps: Int) {
        running = true

        repeat(steps) {
            if (!running) return@repeat
            step()
        }

        running = false
    }
    fun run() {
        while(running) {
            step()
        }
    }

    fun stop() {
        running = false
    }
}