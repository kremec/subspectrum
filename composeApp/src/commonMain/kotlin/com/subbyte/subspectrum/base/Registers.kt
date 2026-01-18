package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.fromBytes
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

enum class RegisterPairCode(val code: Int) {
    BC(0b00),
    DE(0b01),
    HL(0b10),
    SP(0b11)
}

enum class RegisterPairStackCode(val code: Int) {
    BC(0b00),
    DE(0b01),
    HL(0b10),
    AF(0b11)
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

    fun getAF(): Word = Pair(A, F).fromBytes()
    fun setAF(value: Word) {
        val bytes = value.toBytes()
        setA(bytes.first)
        setF(bytes.second)
        invalidate()
    }

    fun getBC(): Word = Pair(B, C).fromBytes()
    fun setBC(value: Word) {
        val bytes = value.toBytes()
        setB(bytes.first)
        setC(bytes.second)
        invalidate()
    }

    fun getDE(): Word = Pair(D, E).fromBytes()
    fun setDE(value: Word) {
        val bytes = value.toBytes()
        setD(bytes.first)
        setE(bytes.second)
        invalidate()
    }

    fun getHL(): Word = Pair(H, L).fromBytes()
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

    fun getIX(): Word = IX
    fun setIX(value: Word) {
        IX = value
        invalidate()
    }

    fun getIY(): Word = IY
    fun setIY(value: Word) {
        IY = value
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
    var registerSet: RegisterSet = normalRegisterSet
    val specialPurposeRegisters: SpecialPurposeRegisters = SpecialPurposeRegisters()

    fun getRegisterPair(code: RegisterPairCode): Word = when (code) {
        RegisterPairCode.BC -> registerSet.getBC()
        RegisterPairCode.DE -> registerSet.getDE()
        RegisterPairCode.HL -> registerSet.getHL()
        RegisterPairCode.SP -> specialPurposeRegisters.getSP()
    }
    fun setRegisterPair(code: RegisterPairCode, value: Word) {
        when (code) {
            RegisterPairCode.BC -> {
                registerSet.setBC(value)
                registerSet.invalidate()
            }
            RegisterPairCode.DE -> {
                registerSet.setDE(value)
                registerSet.invalidate()
            }
            RegisterPairCode.HL -> {
                registerSet.setHL(value)
                registerSet.invalidate()
            }
            RegisterPairCode.SP -> {
                specialPurposeRegisters.setSP(value)
                specialPurposeRegisters.invalidate()
            }
        }
    }

    fun getRegisterPair(code: RegisterPairStackCode): Word = when (code) {
        RegisterPairStackCode.BC -> registerSet.getBC()
        RegisterPairStackCode.DE -> registerSet.getDE()
        RegisterPairStackCode.HL -> registerSet.getHL()
        RegisterPairStackCode.AF -> registerSet.getAF()
    }
    fun setRegisterPair(code: RegisterPairStackCode, value: Word) {
        when (code) {
            RegisterPairStackCode.BC -> {
                registerSet.setBC(value)
                registerSet.invalidate()
            }
            RegisterPairStackCode.DE -> {
                registerSet.setDE(value)
                registerSet.invalidate()
            }
            RegisterPairStackCode.HL -> {
                registerSet.setHL(value)
                registerSet.invalidate()
            }
            RegisterPairStackCode.AF -> {
                registerSet.setAF(value)
                registerSet.invalidate()
            }
        }
    }

    fun switchRegisterSets() {
        registerSet = if (registerSet == normalRegisterSet) shadowRegisterSet else normalRegisterSet
    }
}