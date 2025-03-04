package hanghae.four.taxiservice.interfaces.taxi

import hanghae.four.taxiservice.application.taxi.TaxiFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class TaxiApiController(
    private val taxiFacade: TaxiFacade,
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/taxis")
    fun register(
        @Valid @RequestBody
        request: RegisterRequest,
    ): RegisterResponse {
        val taxiId = taxiFacade.register(request.toTaxiCommand())
        return RegisterResponse(taxiId = taxiId)
    }
}
