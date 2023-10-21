package hanghae.four.taxiservice.applications.taxi

import hanghae.four.taxiservice.domain.taxi.RegisterTaxi
import hanghae.four.taxiservice.domain.taxi.TaxiService
import hanghae.four.taxiservice.domain.taxi.call.driver.DriverService
import hanghae.four.taxiservice.utils.annotations.Facade

@Facade
class TaxiFacade(
    private val taxiService: TaxiService,
    private val driverService: DriverService,
) {
    fun register(command: RegisterTaxi): Long {
        driverService.getDriver(command.driverId)
        return taxiService.register(command)
    }
}
