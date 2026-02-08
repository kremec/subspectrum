package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.wordFromBytes
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.setBit
import com.subbyte.subspectrum.units.toBytes
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class FlagSet(val position: Int) {
    C(0),
    N(1),
    PV(2),
    H(4),
    Z(6),
    S(7),
}

enum class RegisterCode(val code: Int) {
    A(0b111),
    B(0b000),
    C(0b001),
    D(0b010),
    E(0b011),
    H(0b100),
    L(0b101),
}

enum class ConditionCode(val code: Int) {
    NZ(0b000),
    Z(0b001),
    NC(0b010),
    C(0b011),
    PO(0b100),
    PE(0b101),
    P(0b110),
    M(0b111)
}

enum class RegisterPairSSCode(val code: Int) {
    BC(0b00),
    DE(0b01),
    HL(0b10),
    SP(0b11)
}
enum class RegisterPairQQCode(val code: Int) {
    BC(0b00),
    DE(0b01),
    HL(0b10),
    AF(0b11)
}
enum class RegisterPairPPCode(val code: Int) {
    BC(0b00),
    DE(0b01),
    IX(0b10),
    SP(0b11)
}
enum class RegisterPairRRCode(val code: Int) {
    BC(0b00),
    DE(0b01),
    IY(0b10),
    SP(0b11)
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
    fun setA(value: Byte) {
        A = value
        invalidate()
    }

    fun getF(): Byte = F
    fun setF(value: Byte) {
        F = value
        invalidate()
    }

    fun getB(): Byte = B
    fun setB(value: Byte) {
        B = value
        invalidate()
    }

    fun getC(): Byte = C
    fun setC(value: Byte) {
        C = value
        invalidate()
    }

    fun getD(): Byte = D
    fun setD(value: Byte) {
        D = value
        invalidate()
    }

    fun getE(): Byte = E
    fun setE(value: Byte) {
        E = value
        invalidate()
    }

    fun getH(): Byte = H
    fun setH(value: Byte) {
        H = value
        invalidate()
    }

    fun getL(): Byte = L
    fun setL(value: Byte) {
        L = value
        invalidate()
    }

    fun getAF(): Word = Pair(A, F).wordFromBytes()
    fun setAF(value: Word) {
        val bytes = value.toBytes()
        setA(bytes.first)
        setF(bytes.second)
        invalidate()
    }

    fun getBC(): Word = Pair(B, C).wordFromBytes()
    fun setBC(value: Word) {
        val bytes = value.toBytes()
        setB(bytes.first)
        setC(bytes.second)
        invalidate()
    }

    fun getDE(): Word = Pair(D, E).wordFromBytes()
    fun setDE(value: Word) {
        val bytes = value.toBytes()
        setD(bytes.first)
        setE(bytes.second)
        invalidate()
    }

    fun getHL(): Word = Pair(H, L).wordFromBytes()
    fun setHL(value: Word) {
        val bytes = value.toBytes()
        setH(bytes.first)
        setL(bytes.second)
        invalidate()
    }

    fun getRegister(code: RegisterCode): Byte = when (code) {
        RegisterCode.A -> A
        RegisterCode.B -> B
        RegisterCode.C -> C
        RegisterCode.D -> D
        RegisterCode.E -> E
        RegisterCode.H -> H
        RegisterCode.L -> L
    }

    fun setRegister(code: RegisterCode, value: Byte) {
        when (code) {
            RegisterCode.A -> A = value
            RegisterCode.B -> B = value
            RegisterCode.C -> C = value
            RegisterCode.D -> D = value
            RegisterCode.E -> E = value
            RegisterCode.H -> H = value
            RegisterCode.L -> L = value
        }
        invalidate()
    }
    fun setRegister(code: RegisterCode, value: UByte) {
        val registerValue = value.toByte()
        when (code) {
            RegisterCode.A -> A = registerValue
            RegisterCode.B -> B = registerValue
            RegisterCode.C -> C = registerValue
            RegisterCode.D -> D = registerValue
            RegisterCode.E -> E = registerValue
            RegisterCode.H -> H = registerValue
            RegisterCode.L -> L = registerValue
        }
        invalidate()
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
        invalidate()
    }

    fun getCFlag(): Boolean = F.getBit(FlagSet.C.position)
    fun setCFlag(value: Boolean) {
        F = F.setBit(FlagSet.C.position, value)
        invalidate()
    }

    fun getNFlag(): Boolean = F.getBit(FlagSet.N.position)
    fun setNFlag(value: Boolean) {
        F = F.setBit(FlagSet.N.position, value)
        invalidate()
    }

    fun getPVFlag(): Boolean = F.getBit(FlagSet.PV.position)
    fun setPVFlag(value: Boolean) {
        F = F.setBit(FlagSet.PV.position, value)
        invalidate()
    }

    fun getHFlag(): Boolean = F.getBit(FlagSet.H.position)
    fun setHFlag(value: Boolean) {
        F = F.setBit(FlagSet.H.position, value)
        invalidate()
    }

    fun getZFlag(): Boolean = F.getBit(FlagSet.Z.position)
    fun setZFlag(value: Boolean) {
        F = F.setBit(FlagSet.Z.position, value)
        invalidate()
    }

    fun getSFlag(): Boolean = F.getBit(FlagSet.S.position)
    fun setSFlag(value: Boolean) {
        F = F.setBit(FlagSet.S.position, value)
        invalidate()
    }

    fun checkCondition(condition: ConditionCode): Boolean {
        return when (condition) {
            ConditionCode.NZ -> !getZFlag()
            ConditionCode.Z -> getZFlag()
            ConditionCode.NC -> !getCFlag()
            ConditionCode.C -> getCFlag()
            ConditionCode.PO -> !getPVFlag()
            ConditionCode.PE -> getPVFlag()
            ConditionCode.P -> !getSFlag()
            ConditionCode.M -> getSFlag()
        }
    }

    private val _invalidations = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val invalidations: SharedFlow<Unit> = _invalidations.asSharedFlow()
    fun invalidate() {
        _invalidations.tryEmit(Unit) // never suspends
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
    fun setI(value: Byte) {
        I = value
        invalidate()
    }

    fun getR(): Byte = R
    fun setR(value: Byte) {
        R = value
        invalidate()
    }

    fun incrementR(count: Int) {
        if (count <= 0) return
        val rValue = R.toInt() and 0xFF
        val newValue = (rValue and 0x80) or ((rValue + count) and 0x7F)
        R = newValue.toByte()
        invalidate()
    }

    fun getIX(): Word = IX
    fun setIX(value: Word) {
        IX = value
        invalidate()
    }

    fun setIX(value: UWord) {
        IX = value.toShort()
        invalidate()
    }

    fun getIY(): Word = IY
    fun setIY(value: Word) {
        IY = value
        invalidate()
    }

    fun setIY(value: UWord) {
        IY = value.toShort()
        invalidate()
    }

    fun getSP(): Word = SP
    fun setSP(value: Word) {
        SP = value
        invalidate()
    }

    fun getPC(): Word = PC
    fun setPC(value: Word) {
        PC = value
        invalidate()
        pcInvalidate()
    }

    fun reset() {
        I = 0
        R = 0
        IX = 0
        IY = 0
        SP = 0
        PC = 0
        invalidate()
        pcInvalidate()
    }

    private val _invalidations = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val invalidations: SharedFlow<Unit> = _invalidations.asSharedFlow()
    fun invalidate() {
        _invalidations.tryEmit(Unit) // never suspends
    }

    private val _pcInvalidations = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val pcInvalidations: SharedFlow<Unit> = _pcInvalidations.asSharedFlow()
    fun pcInvalidate() {
        _pcInvalidations.tryEmit(Unit) // never suspends
    }
}

object Registers {
    val normalRegisterSet = RegisterSet()
    val shadowRegisterSet = RegisterSet()
    val registerSet: RegisterSet = normalRegisterSet
    val specialPurposeRegisters: SpecialPurposeRegisters = SpecialPurposeRegisters()

    fun getRegisterPair(code: RegisterPairSSCode): Word = when (code) {
        RegisterPairSSCode.BC -> registerSet.getBC()
        RegisterPairSSCode.DE -> registerSet.getDE()
        RegisterPairSSCode.HL -> registerSet.getHL()
        RegisterPairSSCode.SP -> specialPurposeRegisters.getSP()
    }
    fun getRegisterPair(code: RegisterPairQQCode): Word = when (code) {
        RegisterPairQQCode.BC -> registerSet.getBC()
        RegisterPairQQCode.DE -> registerSet.getDE()
        RegisterPairQQCode.HL -> registerSet.getHL()
        RegisterPairQQCode.AF -> registerSet.getAF()
    }
    fun getRegisterPair(code: RegisterPairPPCode): Word = when (code) {
        RegisterPairPPCode.BC -> registerSet.getBC()
        RegisterPairPPCode.DE -> registerSet.getDE()
        RegisterPairPPCode.IX -> specialPurposeRegisters.getIX()
        RegisterPairPPCode.SP -> specialPurposeRegisters.getSP()
    }
    fun getRegisterPair(code: RegisterPairRRCode): Word = when (code) {
        RegisterPairRRCode.BC -> registerSet.getBC()
        RegisterPairRRCode.DE -> registerSet.getDE()
        RegisterPairRRCode.IY -> specialPurposeRegisters.getIY()
        RegisterPairRRCode.SP -> specialPurposeRegisters.getSP()
    }

    fun setRegisterPair(code: RegisterPairSSCode, value: Word) {
        when (code) {
            RegisterPairSSCode.BC -> {
                registerSet.setBC(value)
                registerSet.invalidate()
            }
            RegisterPairSSCode.DE -> {
                registerSet.setDE(value)
                registerSet.invalidate()
            }
            RegisterPairSSCode.HL -> {
                registerSet.setHL(value)
                registerSet.invalidate()
            }
            RegisterPairSSCode.SP -> {
                specialPurposeRegisters.setSP(value)
                specialPurposeRegisters.invalidate()
            }
        }
    }
    fun setRegisterPair(code: RegisterPairSSCode, value: UWord) {
        when (code) {
            RegisterPairSSCode.BC -> {
                registerSet.setBC(value.toShort())
                registerSet.invalidate()
            }
            RegisterPairSSCode.DE -> {
                registerSet.setDE(value.toShort())
                registerSet.invalidate()
            }
            RegisterPairSSCode.HL -> {
                registerSet.setHL(value.toShort())
                registerSet.invalidate()
            }
            RegisterPairSSCode.SP -> {
                specialPurposeRegisters.setSP(value.toShort())
                specialPurposeRegisters.invalidate()
            }
        }
    }
    fun setRegisterPair(code: RegisterPairQQCode, value: Word) {
        when (code) {
            RegisterPairQQCode.BC -> {
                registerSet.setBC(value)
                registerSet.invalidate()
            }
            RegisterPairQQCode.DE -> {
                registerSet.setDE(value)
                registerSet.invalidate()
            }
            RegisterPairQQCode.HL -> {
                registerSet.setHL(value)
                registerSet.invalidate()
            }
            RegisterPairQQCode.AF -> {
                registerSet.setAF(value)
                registerSet.invalidate()
            }
        }
    }
}
