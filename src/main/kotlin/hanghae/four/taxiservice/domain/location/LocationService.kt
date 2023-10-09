package hanghae.four.taxiservice.domain.location

import hanghae.four.taxiservice.domain.location.locationCaller.Location
import hanghae.four.taxiservice.domain.location.locationCaller.LocationApiCaller
import hanghae.four.taxiservice.domain.location.locationCaller.LocationCoordinates

class LocationService(
    private val locationApiCaller: LocationApiCaller,
) {
    fun getCurrentLocationByCoordinates(coordinates: LocationCoordinates): Location {
        return locationApiCaller.getCurrentLocationByCoordinates(coordinates)
    }

    fun getCurrentLocationByKeyword(keyword: String): Location {
        return locationApiCaller.getCurrentLocationByKeyword(keyword)
    }
}
