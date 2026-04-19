package com.subbyte.subspectrum.base.annotations

import com.subbyte.subspectrum.base.Address

data class MemoryPointAnnotation(
    val address: Address,
    val label: String,
    val description: String? = null,
)

data class MemoryRegionAnnotation(
    val start: Address,
    val endInclusive: Address,
    val label: String,
)

data class MemoryInstructionAnnotation(
    val address: Address,
    val description: String,
)

data class MemoryAnnotation(
    val label: String,
    val description: String,
)

fun buildAnnotationSplitAddresses(regionAnnotations: List<MemoryRegionAnnotation>): List<Int> {
    return (memoryPointAnnotations.map { it.address.toInt() } +
        regionAnnotations.map { it.start.toInt() })
        .distinct()
        .sorted()
}

fun buildMemoryAnnotationsByAddress(addresses: Iterable<Int>): Map<Int, List<MemoryAnnotation>> {
    return addresses.mapNotNull { address ->
        memoryAnnotationsByAddress[address]?.let { address to it }
    }.toMap()
}

fun <T> buildRegionDelimitersByRow(
    rows: List<T>,
    regionAnnotations: List<MemoryRegionAnnotation>,
    rowStart: (T) -> Int,
    rowEndInclusive: (T) -> Int,
): Map<Int, List<MemoryRegionAnnotation>> {
    return regionAnnotations
        .sortedBy { it.start.toInt() }
        .mapNotNull { region ->
            val row = rows.firstOrNull { region.start.toInt() in rowStart(it)..rowEndInclusive(it) }
                ?: return@mapNotNull null
            rowStart(row) to region
        }
        .groupBy({ it.first }, { it.second })
}

fun stickyAnnotationsForAddress(
    address: Int,
    annotationsByAddress: Map<Int, List<MemoryAnnotation>>,
    regionAnnotations: List<MemoryRegionAnnotation>,
): List<MemoryAnnotation> {
    val region = activeRegionAt(address, regionAnnotations) ?: return emptyList()

    return annotationsByAddress
        .filterKeys { it in region.start.toInt()..address && it <= region.endInclusive.toInt() }
        .maxByOrNull { it.key }
        ?.value
        .orEmpty()
}

fun activeRegionAt(
    address: Int,
    regionAnnotations: List<MemoryRegionAnnotation>,
): MemoryRegionAnnotation? = regionAnnotations
    .filter { address in it.start.toInt()..it.endInclusive.toInt() }
    .minByOrNull { it.endInclusive.toInt() - it.start.toInt() }

private val memoryPointAnnotations: List<MemoryPointAnnotation> =
    ROMSectionRegistry.sections + SystemVariableRegistry.variables

private val memoryAnnotationsByAddress: Map<Int, List<MemoryAnnotation>> =
    memoryPointAnnotations
        .groupBy { it.address.toInt() }
        .mapValues { (_, points) ->
            points
                .sortedBy { it.label }
                .map { point ->
                    MemoryAnnotation(
                        label = point.label,
                        description = point.description.orEmpty(),
                    )
                }
        }
