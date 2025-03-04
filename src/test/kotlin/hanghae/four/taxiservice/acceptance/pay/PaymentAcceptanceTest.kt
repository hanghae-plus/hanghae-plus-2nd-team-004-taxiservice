package hanghae.four.taxiservice.acceptance.pay

import hanghae.four.taxiservice.acceptance.AcceptanceTest
import hanghae.four.taxiservice.acceptance.taxi.TaxiSteps
import hanghae.four.taxiservice.domain.client.Client
import hanghae.four.taxiservice.domain.pay.payinfo.PayInfo
import hanghae.four.taxiservice.domain.taxi.Taxi
import hanghae.four.taxiservice.domain.taxi.call.Call
import hanghae.four.taxiservice.domain.taxi.call.driver.Driver
import hanghae.four.taxiservice.infrastructure.client.ClientRepository
import hanghae.four.taxiservice.infrastructure.driver.DriverRepository
import hanghae.four.taxiservice.infrastructure.pay.payinfo.PayInfoRepository
import hanghae.four.taxiservice.infrastructure.taxi.call.CallRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import java.math.BigDecimal

class PaymentAcceptanceTest : AcceptanceTest() {

    @Autowired
    private lateinit var driverRepository: DriverRepository

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @Autowired
    private lateinit var callRepository: CallRepository

    @Autowired
    private lateinit var payInfoRepository: PayInfoRepository

    private lateinit var client: Client
    private lateinit var call: Call

    // 기사/사용자 등록 -> 택시등록 -> 결제 방법 등록 -> 택시 호출 -> 결제
    @BeforeEach
    fun init() {
        client = clientRepository.save(Client())

        val driver = driverRepository.save(Driver(name = "홍길동", phoneNumber = "010-1234-5678", licenseNumber = "1234"))

        val taxiResponse = TaxiSteps.`택시 등록`(Taxi.Type.NORMAL, requireNotNull(driver.id), 1234)
        val taxiId = taxiResponse.jsonPath().getLong("taxiId")

        call = callRepository.save(
            Call(userId = requireNotNull(client.id), taxiId = taxiId, origin = "서울시 강남구", destination = "서울시 강북구")
        )
    }

    @Disabled
    @Test
    fun `택시 현금 결제하기`() {
        val response = PaymentSteps.`택시 요금 결제`(
            requireNotNull(client.id),
            requireNotNull(call.id),
            null,
            BigDecimal(1000),
            PayInfo.Type.CASH
        )

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(response.jsonPath().getLong("paymentHistoryId")).isEqualTo(1L)
    }

    @Disabled
    @Test
    fun `택시 카드 결제하기`() {
        val payInfo = payInfoRepository.save(PayInfo(requireNotNull(client.id), PayInfo.Type.SAMSUNGCARD))

        val response = PaymentSteps.`택시 요금 결제`(
            requireNotNull(client.id),
            requireNotNull(call.id),
            payInfo.id,
            BigDecimal(1000),
            PayInfo.Type.SAMSUNGCARD
        )

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(response.jsonPath().getLong("paymentHistoryId")).isEqualTo(1L)
    }

    @Disabled
    @Test
    fun `택시 페이 결제하기`() {
        val payInfo = payInfoRepository.save(PayInfo(requireNotNull(client.id), PayInfo.Type.NAVERPAY))

        val response = PaymentSteps.`택시 요금 결제`(
            requireNotNull(client.id),
            requireNotNull(call.id),
            payInfo.id,
            BigDecimal(1000),
            PayInfo.Type.NAVERPAY
        )

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(response.jsonPath().getLong("paymentHistoryId")).isEqualTo(1L)
    }
}
