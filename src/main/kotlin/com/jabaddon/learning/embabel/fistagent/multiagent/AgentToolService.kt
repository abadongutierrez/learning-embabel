package com.jabaddon.learning.embabel.fistagent.multiagent

import org.springframework.stereotype.Service
import java.text.Normalizer
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class CountryCapitalResponse(
    val status: String,
    val report: String
)

data class CurrentTimeResponse(
    val status: String,
    val report: String
)

/**
 * Service providing various utility tools for agents.
 */
@Service
class AgentToolService {

    companion object {
        // Pre-defined mapping of countries to their capital cities
        private val COUNTRY_CAPITALS = mapOf(
            "france" to "Paris",
            "germany" to "Berlin",
            "japan" to "Tokyo",
            "united states" to "Washington, D.C.",
            "canada" to "Ottawa",
            "india" to "New Delhi",
            "brazil" to "Brasília",
            "australia" to "Canberra",
            "south africa" to "Pretoria",
            "china" to "Beijing",
            "mexico" to "Mexico City",
        )

        // Time format for reporting current time
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    }

    /**
     * Gets the capital city for a given country.
     *
     * @param country The name of the country
     * @return Response containing capital city information or an error
     */
    fun getCapitalCity(country: String?): CountryCapitalResponse {
        return when {
            country.isNullOrBlank() -> CountryCapitalResponse(
                status = "error",
                report = "Country name cannot be empty."
            )
            else -> {
                val countryLower = country.lowercase()
                COUNTRY_CAPITALS[countryLower]?.let { capital ->
                    CountryCapitalResponse(
                        status = "success",
                        report = "The capital city of $country is $capital."
                    )
                } ?: CountryCapitalResponse(
                    status = "error",
                    report = "Sorry, I don't have information about the capital city of $country."
                )
            }
        }
    }

    /**
     * Gets the current time for a given city.
     *
     * @param city The name of the city
     * @return Response containing current time information or an error
     */
    fun getCurrentTime(city: String?): CurrentTimeResponse {
        return when {
            city.isNullOrBlank() -> CurrentTimeResponse(
                status = "error",
                report = "City name cannot be empty."
            )
            else -> {
                val normalizedCity = normalizeLocationName(city)

                ZoneId.getAvailableZoneIds()
                    .stream()
                    .filter { zoneId -> zoneId.lowercase().endsWith("/$normalizedCity") }
                    .findFirst()
                    .map { zoneId ->
                        val currentTime = ZonedDateTime.now(ZoneId.of(zoneId))
                            .format(TIME_FORMATTER)

                        CurrentTimeResponse(
                            status = "success",
                            report = "The current time in $city is $currentTime."
                        )
                    }
                    .orElse(
                        CurrentTimeResponse(
                            status = "error",
                            report = "Sorry, I don't have timezone information for $city."
                        )
                    )
            }
        }
    }

    /**
     * Normalizes location names for timezone lookup.
     *
     * @param locationName The location name to normalize
     * @return Normalized location name
     */
    private fun normalizeLocationName(locationName: String): String {
        return Normalizer.normalize(locationName, Normalizer.Form.NFD)
            .trim()
            .lowercase()
            .replace(Regex("(\\p{IsM}+|\\p{IsP}+)"), "")
            .replace(Regex("\\s+"), "_")
    }
}
