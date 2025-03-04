package hanghae.four.taxiservice.domain.taxi.call

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * TODO: 2023.10.03
 * - aggregate root를 정하고 연관관계 맵핑하기
 */

@Entity
@Table(name = "call")
class Call(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "userId", nullable = false)
    val userId: Long,

    @Column(name = "taxiId", nullable = false)
    val taxiId: Long,

    @Column(name = "callNumber", nullable = false, unique = true)
    val callNumber: String = generateCallNumber(),

    @Column(name = "origin", nullable = false)
    val origin: String,

    @Column(name = "destination", nullable = false)
    val destination: String,

    @Column(name = "createdAt", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: CallStatus = CallStatus.WAITING,
) {

    fun accept() {
        this.status = CallStatus.RUNNING
    }

    fun complete() {
        if (this.status != CallStatus.RUNNING) {
            throw IllegalStateException("호출 상태를 확인 해주세요~!")
        }

        this.status = CallStatus.COMPLETE
    }

    enum class CallStatus(
        private val description: String,
    ) {
        WAITING("기사 응답 대기중"),
        RUNNING("수락 완료"),
        COMPLETE("완료"),
    }
}
