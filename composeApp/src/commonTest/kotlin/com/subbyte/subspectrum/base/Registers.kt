package com.subbyte.subspectrum.base

import kotlin.test.Test
import kotlin.test.assertEquals

class RegistersTest {
    @Test
    fun getInitialRegisterValue() {
        val registerAValue = Registers.registerSet.getA()
        assertEquals( 0, registerAValue)
    }

    @Test
    fun setRegisterValue() {
        Registers.registerSet.setA(42.toByte())
        val registerAValue = Registers.registerSet.getA()
        assertEquals(42, registerAValue)
    }

    @Test
    fun setRegisterSetValueFromRegisters() {
        Registers.registerSet.setH(0x12)
        Registers.registerSet.setL(0x34)
        val registerHLValue = Registers.registerSet.getHL()
        assertEquals(0x1234, registerHLValue)
    }

    @Test
    fun setRegistersFromRegisterSetValue() {
        Registers.registerSet.setHL(0x5678)
        val registerHValue = Registers.registerSet.getH()
        val registerLValue = Registers.registerSet.getL()
        assertEquals(0x56, registerHValue)
        assertEquals(0x78, registerLValue)
    }
}