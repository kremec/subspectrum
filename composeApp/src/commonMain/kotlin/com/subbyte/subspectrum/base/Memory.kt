package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.setBit

typealias Address = UShort
val MEMORY_SIZE = Address.MAX_VALUE.toInt() + 1

class MemorySet (
    private val memoryCells: ByteArray = ByteArray(MEMORY_SIZE)
) {
    fun getMemoryCell(address: Address): Byte {
        return memoryCells[address.toInt()]
    }
    fun setMemoryCell(address: Address, value: Byte) {
        memoryCells[address.toInt()] = value
    }

    fun getMemoryCells(startAddress: Address, endInclusiveAddress: Address): ByteArray {
        return memoryCells.sliceArray(IntRange(startAddress.toInt(), endInclusiveAddress.toInt()))
    }
    fun setMemoryCells(startAddress: Address, data: ByteArray) {
        if (data.size > MEMORY_SIZE) {
            throw IllegalArgumentException(
                "Data size exceeds memory capacity: size=${data.size}, max=$MEMORY_SIZE"
            )
        }

        val endDataAddress = startAddress.toInt() + data.size
        if (endDataAddress > MEMORY_SIZE) {
            throw IllegalArgumentException(
                "Memory write exceeds maximum address: " +
                        "start=$startAddress, size=${data.size}, max=$MEMORY_SIZE"
            )
        }

        data.copyInto(memoryCells, startAddress.toInt())
    }

    fun getBitInMemoryCell(address: Address, bit: Int): Boolean {
        if (bit !in 0..7) {
            throw IllegalArgumentException("Bit must be between 0 and 7, got $bit")
        }

        val byte = getMemoryCell(address)
        return byte.getBit(bit)
    }

    fun setBitInMemoryCell(address: Address, bit: Int, value: Boolean) {
        if (bit !in 0..7) {
            throw IllegalArgumentException("Bit must be between 0 and 7, got $bit")
        }

        val byte = getMemoryCell(address)
        setMemoryCell(address, byte.setBit(bit, value))
    }

    fun reset() {
        memoryCells.fill(0x00)
    }
}

object Memory {
    val memorySet: MemorySet = MemorySet()
}