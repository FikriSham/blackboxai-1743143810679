package com.meerkat.converter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meerkat.converter.utils.CoordinateConverter
import com.meerkat.converter.utils.CoordinateConverter.Format
import timber.log.Timber

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        testCoordinateConversions()
    }

    private fun testCoordinateConversions() {
        // Basic conversions
        testConversion("45.5000", Format.DD, Format.DM, "45° 30.000'")
        testConversion("45° 30.500'", Format.DM, Format.DMS, "45° 30' 30.00\"") 
        testConversion("45° 30' 30\"", Format.DMS, Format.DD, "45.508333")

        // Negative coordinates
        testConversion("-45.5000", Format.DD, Format.DM, "-45° 30.000'")
        testConversion("-45° 30.500'", Format.DM, Format.DMS, "-45° 30' 30.00\"")
        testConversion("-45° 30' 30\"", Format.DMS, Format.DD, "-45.508333")

        // Boundary values
        testConversion("0.0", Format.DD, Format.DM, "0° 0.000'")
        testConversion("90.0", Format.DD, Format.DM, "90° 0.000'") 
        testConversion("-90.0", Format.DD, Format.DM, "-90° 0.000'")

        // Edge cases
        testConversion("45.999999", Format.DD, Format.DM, "45° 59.999'")
        testConversion("45° 59.999'", Format.DM, Format.DMS, "45° 59' 59.94\"")
        testConversion("45° 59' 59.99\"", Format.DMS, Format.DD, "45.999997")

        // UTM conversions
        testConversion("31N 448251 5632643", Format.UTM, Format.DD, "50.833333, 4.333333")
        testConversion("50.833333, 4.333333", Format.DD, Format.UTM, "31N 448251 5632643")
        testConversion("33T 294409 5814623", Format.UTM, Format.DM, "52° 30.000', 13° 22.500'")

        // MGRS conversions
        testConversion("31N FJ 48251 32643", Format.MGRS, Format.DD, "50.833333, 4.333333")
        testConversion("50.833333, 4.333333", Format.DD, Format.MGRS, "31N FJ 48251 32643")
        testConversion("48P UV 12345 67890", Format.MGRS, Format.DM, "12° 34.560', 98° 45.678'")

        // RSO Kertau conversions
        testConversion("47N 682354 542187", Format.RSO_KERTAU, Format.DD, "5.000000, 102.000000")
        testConversion("5.000000, 102.000000", Format.DD, Format.RSO_KERTAU, "47N 682354 542187")
        testConversion("47N 600000 200000", Format.RSO_KERTAU, Format.DMS, "2° 0' 0.00\", 102° 0' 0.00\"")

        // RSO Timbalai Sabah conversions
        testConversion("50N 123456 654321", Format.RSO_TIMBALAI_SABAH, Format.DD, "6.000000, 116.000000")
        testConversion("6.000000, 116.000000", Format.DD, Format.RSO_TIMBALAI_SABAH, "50N 123456 654321")
        testConversion("50N 100000 500000", Format.RSO_TIMBALAI_SABAH, Format.DM, "5° 0.000', 115° 30.000'")

        // RSO Timbalai Sarawak conversions
        testConversion("49N 234567 765432", Format.RSO_TIMBALAI_SARAWAK, Format.DD, "2.000000, 110.000000")
        testConversion("2.000000, 110.000000", Format.DD, Format.RSO_TIMBALAI_SARAWAK, "49N 234567 765432")
        testConversion("49N 300000 400000", Format.RSO_TIMBALAI_SARAWAK, Format.DMS, "4° 0' 0.00\", 111° 0' 0.00\"")

        // MRSO GDM2000 conversions
        testConversion("48N 500000 200000", Format.MRSO_GDM2000, Format.DD, "2.000000, 103.000000")
        testConversion("2.000000, 103.000000", Format.DD, Format.MRSO_GDM2000, "48N 500000 200000")
        testConversion("48N 550000 250000", Format.MRSO_GDM2000, Format.DM, "3° 0.000', 104° 0.000'")

        // BRSO GDM2000 conversions
        testConversion("49N 600000 300000", Format.BRSO_GDM2000, Format.DD, "3.000000, 101.000000")
        testConversion("3.000000, 101.000000", Format.DD, Format.BRSO_GDM2000, "49N 600000 300000")
        testConversion("49N 650000 350000", Format.BRSO_GDM2000, Format.DMS, "4° 0' 0.00\", 102° 0' 0.00\"")
    }

    private fun testConversion(input: String, from: Format, to: Format, expected: String) {
        try {
            val result = CoordinateConverter.convert(input, from, to)
            Timber.d("Test: $from -> $to | Input: $input | Result: $result")
            
            if (result == expected) {
                Timber.i("✅ Test PASSED: $from to $to conversion")
            } else {
                Timber.e("❌ Test FAILED: Expected $expected but got $result")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Test ERROR: ${e.message}")
        }
    }
}
