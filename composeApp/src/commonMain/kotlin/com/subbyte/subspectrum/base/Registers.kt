package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.setBit
import com.subbyte.subspectrum.units.toBytes

enum class FlagSet(position: Int) {
    C(0),
    N(1),
    PV(2),
    H(4),
    Z(6),
    S(7),
}

data class RegisterSet(
    private var A: Byte = 0,
    private var F: Byte = 0,
    private var B: Byte = 0,
    private var C: Byte = 0,
    private var D: Byte = 0,
    private var E: Byte = 0,
    private var H: Byte = 0,
    private var L: Byte = 0,

) {
    fun getA(): Byte = A
    fun setA(value: Byte) { A = value }

    fun getB(): Byte = B
    fun setB(value: Byte) { B = value }

    fun getC(): Byte = C
    fun setC(value: Byte) { C = value }

    fun getD(): Byte = D
    fun setD(value: Byte) { D = value }

    fun getE(): Byte = E
    fun setE(value: Byte) { E = value }

    fun getH(): Byte = H
    fun setH(value: Byte) { H = value }

    fun getL(): Byte = L
    fun setL(value: Byte) { L = value }

    fun getAF(): Word = Pair(A, F).fromBytes()

    fun getBC(): Word = Pair(B, C).fromBytes()
    fun setBC(value: Word) {
        val bytes = value.toBytes()
        setB(bytes.first)
        setC(bytes.second)
    }

    fun getDE(): Word = Pair(D, E).fromBytes()
    fun setDE(value: Word) {
        val bytes = value.toBytes()
        setD(bytes.first)
        setE(bytes.second)
    }

    fun getHL(): Word = Pair(H, L).fromBytes()
    fun setHL(value: Word) {
        val bytes = value.toBytes()
        setH(bytes.first)
        setL(bytes.second)
    }

    fun reset() {
        A = 0
        F = 0
        B = 0
        C = 0
        D = 0
        E = 0
        H = 0
        L = 0
    }

    fun getCFlag(): Boolean = F.getBit(FlagSet.C.ordinal)
    fun setCFlag(value: Boolean) {
        F.setBit(FlagSet.C.ordinal, value)
    }

    fun getNFlag(): Boolean = F.getBit(FlagSet.N.ordinal)
    fun setNFlag(value: Boolean) {
        F.setBit(FlagSet.N.ordinal, value)
    }

    fun getPVFlag(): Boolean = F.getBit(FlagSet.PV.ordinal)
    fun setPVFlag(value: Boolean) {
        F.setBit(FlagSet.PV.ordinal, value)
    }

    fun getHFlag(): Boolean = F.getBit(FlagSet.H.ordinal)
    fun setHFlag(value: Boolean) {
        F.setBit(FlagSet.H.ordinal, value)
    }

    fun getZFlag(): Boolean = F.getBit(FlagSet.Z.ordinal)
    fun setZFlag(value: Boolean) {
        F.setBit(FlagSet.Z.ordinal, value)
    }

    fun getSFlag(): Boolean = F.getBit(FlagSet.S.ordinal)
    fun setSFlag(value: Boolean) {
        F.setBit(FlagSet.S.ordinal, value)
    }
}

data class SpecialPurposeRegisters(
    private var I: Byte = 0,
    private var R: Byte = 0,
    private var IX: Word = 0,
    private var IY: Word = 0,
    private var SP: Word = 0,
    private var PC: Word = 0,
) {
    fun getI(): Byte = I
    fun setI(value: Byte) { I = value }

    fun getR(): Byte = R
    fun setR(value: Byte) { R = value }

    fun getIX(): Word = IX
    fun setIX(value: Word) { IX = value }

    fun getIY(): Word = IY
    fun setIY(value: Word) { IY = value }

    fun getSP(): Word = SP
    fun setSP(value: Word) { SP = value }

    fun getPC(): Word = PC
    fun setPC(value: Word) { PC = value }

    fun reset() {
        I = 0
        R = 0
        IX = 0
        IY = 0
        SP = 0
        PC = 0
    }
}

object Registers {
    private val normalRegisterSetSet = RegisterSet()
    private val shadowRegisterSetSet = RegisterSet()
    var registerSet: RegisterSet = normalRegisterSetSet
    val specialPurposeRegisters: SpecialPurposeRegisters = SpecialPurposeRegisters()

    fun switchRegisterSets() {
        if (registerSet == normalRegisterSetSet) {
            registerSet = shadowRegisterSetSet
        } else {
            registerSet = normalRegisterSetSet
        }
    }
}