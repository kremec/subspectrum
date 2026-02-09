package com.subbyte.subspectrum.proc

import androidx.compose.runtime.mutableStateOf
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULA
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.units.toBytes

object Processor {
    var NMI_FF: Boolean = false
    var IFF1: Boolean = false
    var IFF2: Boolean = false
    var afterEIDI: Boolean = false
    var interruptMode: Int = 0

    var inHalt: Boolean = false

    var running = mutableStateOf(false)

    fun step() {
        if (NMI_FF && !afterEIDI) {
            handleNMI()
        }
        inHalt = false
        afterEIDI = false

        val pc = Registers.specialPurposeRegisters.getPC()

        val decodedInstruction = Instructions.decode(pc.toUShort())
        Registers.specialPurposeRegisters.incrementR(decodedInstruction.opcodeFetchCount)

        val instruction = decodedInstruction.instruction
        Registers.specialPurposeRegisters.setPC((pc + instruction.bytes.size).toShort())

        instruction.execute()
        ULA.advanceCycles(instruction.getTStates())

        // Check for interrupt at the end of instruction (sampled on last T-state)
        if (IFF1 && ULA.isInterruptPending()) {
            ULA.clearInterrupt()
            handleInterrupt()
        }
    }

    private fun handleNMI() {
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
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        Registers.specialPurposeRegisters.setPC(0x0066)

        ULA.advanceNmiCycles()
    }

    private fun handleInterrupt() {
        IFF1 = false
        IFF2 = false

        // TODO: Implement interrupt mode handling
        when (interruptMode) {
            0 -> { /* Mode 0: Variable cycles based on instruction */ }
            1 -> { /* Mode 1: RST 0x38 - 13 cycles */ }
            2 -> { /* Mode 2: Vector table lookup - 19 cycles */ }
        }

        ULA.advanceInterruptCycles(interruptMode)
    }

    /*
    // TODO: Implement interrupt mode handling

    // Mode 0: In this mode, the interrupting device can insert any instruction on the data bus for execution by the CPU.
    //         The first byte of a multibyte instruction is read during the interrupt acknowledge cycle.
    private fun handleInterruptMode0() {

    }

    // Mode 1: In this mode, the processor responds to an interrupt by executing a restart at address 0038h.
    private fun handleInterruptMode1() {
        // Push PC onto stack
        val pc = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pc.toBytes()
        val sp = Registers.specialPurposeRegisters.getSP()
        Registers.specialPurposeRegisters.setSP(sp.minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        // Jump to 0x0038
        Registers.specialPurposeRegisters.setPC(0x0038)

        // Mode 1 takes 13 T-states
        ULA.advanceCycles(13)
    }

    // Mode 2: This mode allows an indirect call to any memory location by an 8-bit vector supplied from the peripheral device.
    //         This vector then becomes the least-significant eight bits of the indirect pointer, while the I Register in the CPU provides the most-significant eight bits.
    //         This address points to an address in a vector table that is the starting address for the interrupt service routine.
    private fun handleInterruptMode2() {
        // Get vector from I register and data bus (0xFF on Spectrum)
        val iRegister = Registers.specialPurposeRegisters.getI()
        val vector = 0xFF  // Data bus value on Spectrum
        val vectorAddress = ((iRegister.toInt() and 0xFF) shl 8) or (vector and 0xFE)

        // Push PC onto stack
        val pc = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pc.toBytes()
        val sp = Registers.specialPurposeRegisters.getSP()
        Registers.specialPurposeRegisters.setSP(sp.minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        // Read jump address from vector table
        val low = Memory.memorySet.getMemoryCell(vectorAddress.toUShort())
        val high = Memory.memorySet.getMemoryCell((vectorAddress + 1).toUShort())
        val jumpAddress = ((high.toInt() and 0xFF) shl 8) or (low.toInt() and 0xFF)

        Registers.specialPurposeRegisters.setPC(jumpAddress.toShort())

        // Mode 2 takes 19 T-states
        ULA.advanceCycles(19)
    }
    */

    fun run(steps: Int) {
        running.value = true

        repeat(steps) {
            if (!running.value) return@repeat
            step()
        }

        running.value = false
    }
    fun run() {
        running.value = true

        while(running.value) {
            step()
        }
    }

    fun stop() {
        running.value = false
    }
}