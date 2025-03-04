package hanghae.four.taxiservice.unit.domain.pay

import hanghae.four.taxiservice.domain.pay.PayFactory
import hanghae.four.taxiservice.domain.pay.PaymentCommand
import hanghae.four.taxiservice.domain.pay.PaymentService
import hanghae.four.taxiservice.domain.pay.PaymentStore
import hanghae.four.taxiservice.domain.pay.payinfo.PayInfo
import hanghae.four.taxiservice.domain.taxi.Taxi
import hanghae.four.taxiservice.domain.taxi.call.Call
import hanghae.four.taxiservice.unit.infrastructures.pay.FakePayInfoRepository
import hanghae.four.taxiservice.unit.infrastructures.pay.FakePaymentRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentServiceTest {

    private lateinit var paymentService: PaymentService
    private lateinit var fakePayInfoRepository: FakePayInfoRepository
    private lateinit var paymentStore: PaymentStore
    private lateinit var payFactory: PayFactory

    private lateinit var call: Call
    private lateinit var taxi: Taxi

    @BeforeEach
    fun setup() {
        fakePayInfoRepository = FakePayInfoRepository()
        paymentStore = FakePaymentRepository()

        payFactory = mockk()

        paymentService = PaymentService(
            fakePayInfoRepository,
            paymentStore,
            payFactory
        )

        taxi = Taxi(driverId = 1L, type = Taxi.Type.NORMAL, number = 1234, status = Taxi.Status.RUNNING)
        call = Call(userId = 1L, taxiId = 1L, origin = "서울시 강남구", destination = "서울시 강북구")
    }

    @Test
    fun `택시 현금 결제 성공`() {
        call.accept()

        val command = PaymentCommand(
            clientId = 1L,
            callId = 1L,
            payInfoId = null,
            amount = BigDecimal(1000),
            payType = PayInfo.Type.CASH
        )

        every { payFactory.execute(PayInfo(1L, PayInfo.Type.CASH)) } just Runs

        val payId = paymentService.pay(command, call, taxi)

        assertThat(payId).isEqualTo(1L)
    }

    @Test
    fun `결제시 호출된 택시가 "운행중"이 아니면 에러`() {
        taxi = Taxi(driverId = 2L, type = Taxi.Type.NORMAL, number = 1234, status = Taxi.Status.WAITING)

        call = Call(userId = 1L, taxiId = 2L, origin = "서울시 강남구", destination = "서울시 강북구")
        call.accept()

        val command = PaymentCommand(
            clientId = 1L,
            callId = 2L,
            payInfoId = null,
            amount = BigDecimal(1000),
            payType = PayInfo.Type.CASH
        )

        Assertions.assertThatThrownBy { paymentService.pay(command, call, taxi) }
            .isInstanceOf(java.lang.IllegalArgumentException::class.java)
    }

    @Test
    fun `호출된 택시가 "RUNNING" 이 아니라면 에러`() {
        val command = PaymentCommand(
            clientId = 1L,
            callId = 1L,
            payInfoId = 1L,
            amount = BigDecimal(1000),
            payType = PayInfo.Type.SAMSUNGCARD
        )

        Assertions.assertThatThrownBy { paymentService.pay(command, call, taxi) }
            .isInstanceOf(java.lang.IllegalStateException::class.java)
    }

    @Test
    fun `카드, 페이 결제시 결제 방법이 등록되어 있지 않다면 에러`() {
        call.accept()

        val command = PaymentCommand(
            clientId = 1L,
            callId = 1L,
            payInfoId = 1L,
            amount = BigDecimal(1000),
            payType = PayInfo.Type.SAMSUNGCARD
        )

        Assertions.assertThatThrownBy { paymentService.pay(command, call, taxi) }
            .isInstanceOf(java.lang.IllegalArgumentException::class.java)
    }

    @Test
    fun `택시 카드 결제 성공`() {
        call.accept()

        val payment = PayInfo(clientId = 1L, type = PayInfo.Type.SAMSUNGCARD)

        fakePayInfoRepository.store(payment)

        val command = PaymentCommand(
            clientId = 1L,
            callId = 1L,
            payInfoId = 1L,
            amount = BigDecimal(1000),
            payType = PayInfo.Type.SAMSUNGCARD
        )

        every { payFactory.execute(any()) } just Runs

        val payId = paymentService.pay(command, call, taxi)

        assertThat(payId).isEqualTo(1L)
    }
}
