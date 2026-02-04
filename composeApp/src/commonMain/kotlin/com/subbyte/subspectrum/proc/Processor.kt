package com.subbyte.subspectrum.proc

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.units.toBytes

object Processor {
    var NMI_FF: Boolean = false
    var IFF1: Boolean = false
    var IFF2: Boolean = false
    var afterEIDI: Boolean = false

    var inHalt: Boolean = false

    // Mode 0: In this mode, the interrupting device can insert any instruction on the data bus for execution by the CPU.
    //         The first byte of a multibyte instruction is read during the interrupt acknowledge cycle.
    //         Subsequent bytes are read in by a normal memory read sequence.
    // Mode 1: In this mode, the processor responds to an interrupt by executing a restart at address 0038h.
    // Mode 2: This mode allows an indirect call to any memory location by an 8-bit vector supplied from the peripheral device.
    //         This vector then becomes the least-significant eight bits of the indirect pointer, while the I Register in the CPU provides the most-significant eight bits.
    //         This address points to an address in a vector table that is the starting address for the interrupt service routine.
    var interruptMode: Int = 0

    var running = false

    fun step() {
        if (NMI_FF) {
            if (!afterEIDI) {
                NMI_FF = false
                IFF2 = IFF1
                IFF1 = false

                val spRegisterValue = Registers.specialPurposeRegisters.getSP()
                Registers.specialPurposeRegisters.setSP(spRegisterValue.minus(2).toShort())

                if (inHalt) {
                    val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
                    Registers.specialPurposeRegisters.setPC(pcRegisterValue.inc())
                }

                val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
                val (highByte, lowByte) = pcRegisterValue.toBytes()
                Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
                Memory.memorySet.setMemoryCells(Registers.specialPurposeRegisters.getSP().toUShort(), byteArrayOf(lowByte, highByte))

                Registers.specialPurposeRegisters.setPC(0x0066)
            }
        }
        inHalt = false
        afterEIDI = false

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