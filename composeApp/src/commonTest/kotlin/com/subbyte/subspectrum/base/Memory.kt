package com.subbyte.subspectrum.base

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MemoryTest {
    @Test
    fun getInitialMemoryCellValue() {
        val memoryCellValue = Memory.memorySet.getMemoryCell(0x0000u)
        assertEquals( 0x00, memoryCellValue)
    }

    @Test
    fun setMemoryCellValue() {
        Memory.memorySet.setMemoryCell(0x1234u, 0x42)
        val memoryCellValue = Memory.memorySet.getMemoryCell(0x1234u)
        assertEquals(0x42, memoryCellValue)
    }

    @Test
    fun getMemoryCellsRange() {
        Memory.memorySet.setMemoryCells(0x2000u, byteArrayOf(0x12, 0x34, 0x56))
        val memoryCell1Value = Memory.memorySet.getMemoryCell(0x2000u)
        val memoryCell2Value = Memory.memorySet.getMemoryCell(0x2001u)
        val memoryCell3Value = Memory.memorySet.getMemoryCell(0x2002u)

        assertEquals(0x12, memoryCell1Value)
        assertEquals(0x34, memoryCell2Value)
        assertEquals(0x56, memoryCell3Value)
    }

    @Test
    fun setMultipleMemoryCells() {
        val data = byteArrayOf(0x11, 0x22, 0x33, 0x44)
        Memory.memorySet.setMemoryCells(0x1000u, data)
        val memoryCell1Value = Memory.memorySet.getMemoryCell(0x1000u)
        val memoryCell2Value = Memory.memorySet.getMemoryCell(0x1001u)
        val memoryCell3Value = Memory.memorySet.getMemoryCell(0x1002u)
        val memoryCell4Value = Memory.memorySet.getMemoryCell(0x1003u)

        assertEquals(0x11, memoryCell1Value)
        assertEquals(0x22, memoryCell2Value)
        assertEquals(0x33, memoryCell3Value)
        assertEquals(0x44, memoryCell4Value)
    }

    @Test
    fun setMemoryCellsDataSizeTooLarge() {
        val data = ByteArray(70000)
        assertFailsWith<IllegalArgumentException> {
            Memory.memorySet.setMemoryCells(0x0000u, data)
        }
    }

    @Test
    fun setMemoryCellsExceedsMaxAddress() {
        val data = byteArrayOf(0x01, 0x02)
        assertFailsWith<IllegalArgumentException> {
            Memory.memorySet.setMemoryCells(0xFFFFu, data)
        }
    }

    @Test
    fun getBitInMemoryCell() {
        Memory.memorySet.setMemoryCell(0x3000u, 0b1010)
        val bit1 = Memory.memorySet.getBitInMemoryCell(0x3000u, 1)
        val bit2 = Memory.memorySet.getBitInMemoryCell(0x3000u, 2)
        val bit0 = Memory.memorySet.getBitInMemoryCell(0x3000u, 0)

        assertEquals(true, bit1)
        assertEquals(false, bit2)
        assertEquals(false, bit0)
    }

    @Test
    fun getBitInMemoryCellInvalidBit() {
        assertFailsWith<IllegalArgumentException> {
            Memory.memorySet.getBitInMemoryCell(0x3000u, 8)
        }
    }

    @Test
    fun setBitInMemoryCell() {
        Memory.memorySet.setMemoryCell(0x4000u, 0x00)
        Memory.memorySet.setBitInMemoryCell(0x4000u, 3, true)
        Memory.memorySet.setBitInMemoryCell(0x4000u, 5, true)
        val memoryCell = Memory.memorySet.getMemoryCell(0x4000u)

        assertEquals(0b00101000, memoryCell)
    }

    @Test
    fun setBitInMemoryCellInvalidBit() {
        Memory.memorySet.setMemoryCell(0x4000u, 0x00)
        assertFailsWith<IllegalArgumentException> {
            Memory.memorySet.setBitInMemoryCell(0x4000u, -1, true)
        }
    }
}