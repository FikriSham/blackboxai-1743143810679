package com.meerkat.converter.utils

import com.meerkat.converter.database.HistoryEntity
import java.text.SimpleDateFormat
import java.util.*

object CoordinateConverter {

    // Supported formats
    enum class Format {
        DD, DM, DMS, UTM, MGRS, MRSO_GDM2000, BRSO_GDM2000, RSO_KERTAU, RSO_TIMBALAI_SABAH, RSO_TIMBALAI_SARAWAK
    }

    fun convert(input: String, from: Format, to: Format): String {
        return when {
            from == to -> input
            from in listOf(Format.UTM, Format.MGRS, Format.RSO_KERTAU, Format.RSO_TIMBALAI_SABAH, 
                         Format.RSO_TIMBALAI_SARAWAK, Format.MRSO_GDM2000, Format.BRSO_GDM2000) && 
            to !in listOf(Format.UTM, Format.MGRS, Format.RSO_KERTAU, Format.RSO_TIMBALAI_SABAH,
                         Format.RSO_TIMBALAI_SARAWAK, Format.MRSO_GDM2000, Format.BRSO_GDM2000) -> {
                val (lat, lon) = when (from) {
                    Format.UTM -> parseUTM(input)
                    Format.MGRS -> parseMGRS(input)
                    Format.RSO_KERTAU -> parseRsoKertau(input)
                    Format.RSO_TIMBALAI_SABAH -> parseRsoTimbalaiSabah(input)
                    Format.RSO_TIMBALAI_SARAWAK -> parseRsoTimbalaiSarawak(input)
                    else -> throw IllegalArgumentException("Unsupported input format: $from")
                }
                when (to) {
                    Format.DD -> formatDD(lat) + ", " + formatDD(lon)
                    Format.DM -> formatDM(lat) + ", " + formatDM(lon)
                    Format.DMS -> formatDMS(lat) + ", " + formatDMS(lon)
                    else -> throw IllegalArgumentException("Unsupported output format: $to")
                }
            }
            to == Format.UTM -> {
                val latLon = when (from) {
                    Format.DD -> input.split(",").map { it.trim().toDouble() }
                    Format.DM -> input.split(",").map { parseDM(it.trim()) }
                    Format.DMS -> input.split(",").map { parseDMS(it.trim()) }
                    else -> throw IllegalArgumentException("Unsupported input format: $from")
                }
                formatUTM(latLon[0], latLon[1])
            }
            else -> {
                // Convert to decimal degrees first as intermediate format
                val decimalDegrees = when (from) {
                    Format.DD -> parseDD(input)
                    Format.DM -> parseDM(input)
                    Format.DMS -> parseDMS(input)
                    else -> throw IllegalArgumentException("Unsupported input format: $from")
                }

                // Convert from decimal degrees to target format
                when (to) {
                    Format.DD -> formatDD(decimalDegrees)
                    Format.DM -> formatDM(decimalDegrees)
                    Format.DMS -> formatDMS(decimalDegrees)
                    else -> throw IllegalArgumentException("Unsupported output format: $to")
                }
            }
        }
    }

    private fun parseDD(input: String): Double {
        return input.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid DD format")
    }

    private fun parseDM(input: String): Double {
        // Parse Degrees Minutes format (e.g. 45° 30.5')
        val parts = input.split("[°\\s']+".toRegex())
        if (parts.size != 2) throw IllegalArgumentException("Invalid DM format")
        val degrees = parts[0].toDouble()
        val minutes = parts[1].toDouble()
        return degrees + minutes / 60
    }

    private fun parseDMS(input: String): Double {
        // Parse Degrees Minutes Seconds format (e.g. 45° 30' 30")
        val parts = input.split("[°\\s'\"]+".toRegex())
        if (parts.size != 3) throw IllegalArgumentException("Invalid DMS format")
        val degrees = parts[0].toDouble()
        val minutes = parts[1].toDouble()
        val seconds = parts[2].toDouble()
        return degrees + minutes / 60 + seconds / 3600
    }

    private fun formatDD(value: Double): String {
        return "%.6f".format(value)
    }

    private fun formatDM(value: Double): String {
        val degrees = value.toInt()
        val minutes = (value - degrees) * 60
        return "$degrees° ${"%.3f'".format(minutes)}"
    }

    // UTM conversion constants
    private val UTM_SCALE_FACTOR = 0.9996
    private val EQUATORIAL_RADIUS = 6378137.0
    private val ECC_SQUARED = 0.00669438
    private val ECC_PRIME_SQUARED = ECC_SQUARED / (1 - ECC_SQUARED)

    private fun formatDMS(value: Double): String {
        val degrees = value.toInt()
        val remaining = (value - degrees) * 60
        val minutes = remaining.toInt()
        val seconds = (remaining - minutes) * 60
        return "$degrees° $minutes' ${"%.2f\"".format(seconds)}"
    }

    // MGRS conversion methods (simplified)
    private fun parseMGRS(mgrs: String): Pair<Double, Double> {
        // Simplified MGRS to Lat/Lon conversion
        // Actual implementation would require complex calculations
        val parts = mgrs.split(" ")
        if (parts.size != 3) throw IllegalArgumentException("Invalid MGRS format")
        
        // Convert to UTM first (simplified)
        val utm = "${parts[0]} ${parts[1]} ${parts[2]}"
        return parseUTM(utm)
    }

    private fun formatMGRS(lat: Double, lon: Double): String {
        // Simplified Lat/Lon to MGRS conversion
        // Actual implementation would require complex calculations
        val utm = formatUTM(lat, lon)
        val parts = utm.split(" ")
        return "${parts[0]} ${parts[1]} ${parts[2]}"
    }

    // RSO Kertau conversion methods (simplified)
    private fun parseRsoKertau(rso: String): Pair<Double, Double> {
        // Simplified RSO Kertau to Lat/Lon conversion
        // Uses same format as UTM but different parameters
        return parseUTM(rso)
    }

    private fun formatRsoKertau(lat: Double, lon: Double): String {
        // Simplified Lat/Lon to RSO Kertau conversion
        return formatUTM(lat, lon)
    }

    // RSO Timbalai Sabah conversion methods (simplified)
    private fun parseRsoTimbalaiSabah(rso: String): Pair<Double, Double> {
        // Simplified RSO Timbalai Sabah to Lat/Lon conversion
        return parseUTM(rso)
    }

    private fun formatRsoTimbalaiSabah(lat: Double, lon: Double): String {
        // Simplified Lat/Lon to RSO Timbalai Sabah conversion
        return formatUTM(lat, lon)
    }

    // RSO Timbalai Sarawak conversion methods (simplified)
    private fun parseRsoTimbalaiSarawak(rso: String): Pair<Double, Double> {
        // Simplified RSO Timbalai Sarawak to Lat/Lon conversion
        return parseUTM(rso)
    }

    private fun formatRsoTimbalaiSarawak(lat: Double, lon: Double): String {
        // Simplified Lat/Lon to RSO Timbalai Sarawak conversion
        return formatUTM(lat, lon)
    }

    // MRSO GDM2000 conversion methods (simplified)
    private fun parseMrsoGdm2000(rso: String): Pair<Double, Double> {
        // Simplified MRSO GDM2000 to Lat/Lon conversion
        return parseUTM(rso)
    }

    private fun formatMrsoGdm2000(lat: Double, lon: Double): String {
        // Simplified Lat/Lon to MRSO GDM2000 conversion
        return formatUTM(lat, lon)
    }

    // BRSO GDM2000 conversion methods (simplified)
    private fun parseBrsoGdm2000(rso: String): Pair<Double, Double> {
        // Simplified BRSO GDM2000 to Lat/Lon conversion
        return parseUTM(rso)
    }

    private fun formatBrsoGdm2000(lat: Double, lon: Double): String {
        // Simplified Lat/Lon to BRSO GDM2000 conversion
        return formatUTM(lat, lon)
    }

    // UTM conversion methods
    private fun parseUTM(utm: String): Pair<Double, Double> {
        val parts = utm.split(" ")
        if (parts.size != 3) throw IllegalArgumentException("Invalid UTM format")
        
        val zone = parts[0].dropLast(1).toInt()
        val zoneLetter = parts[0].last()
        val easting = parts[1].toDouble()
        val northing = parts[2].toDouble()
        
        return convertUTMToLatLon(zone, zoneLetter, easting, northing)
    }

    private fun convertUTMToLatLon(zone: Int, zoneLetter: Char, easting: Double, northing: Double): Pair<Double, Double> {
        // Implementation of UTM to Lat/Lon conversion
        // (Actual implementation would involve complex geodetic calculations)
        // This is a simplified placeholder that approximates the conversion
        
        val x = easting - 500000.0
        val y = northing
        
        val lonOrigin = (zone - 1) * 6 - 180 + 3
        
        // Simplified calculation (real implementation would be more complex)
        val lat = y / 110574.0
        val lon = lonOrigin + x / (111320.0 * Math.cos(Math.toRadians(lat)))
        
        return Pair(lat, lon)
    }

    private fun formatUTM(lat: Double, lon: Double): String {
        // Implementation of Lat/Lon to UTM conversion
        // (Actual implementation would involve complex geodetic calculations)
        // This is a simplified placeholder that approximates the conversion
        
        val zone = ((lon + 180) / 6).toInt() + 1
        val zoneLetter = if (lat >= 0) 'N' else 'S'
        
        // Simplified calculation (real implementation would be more complex)
        val easting = 500000.0 + (lon - ((zone - 1) * 6 - 180 + 3)) * 111320.0 * Math.cos(Math.toRadians(lat))
        val northing = lat * 110574.0
        
        return "$zone$zoneLetter ${easting.toInt()} ${northing.toInt()}"
    }

    fun createHistoryEntry(
        input: String,
        output: String,
        from: Format,
        to: Format
    ): HistoryEntity {
        return HistoryEntity(
            conversionType = "${from.name} to ${to.name}",
            input = input,
            output = output,
            timestamp = System.currentTimeMillis()
        )
    }
}