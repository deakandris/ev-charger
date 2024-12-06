package deakandris.evcharger.domain

import org.apache.catalina.manager.StatusManagerServlet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Subject

import static java.time.Instant.now
import static java.time.temporal.ChronoUnit.MILLIS

import deakandris.evcharger.domain.StartChargingSessionUseCase.StartChargingSessionCommand
import deakandris.evcharger.domain.StartChargingSessionUseCase.StartChargingSessionResult

@SpringBootTest
class StartChargingSessionUseCaseIntSpec extends Specification {

    @Subject
    @Autowired
    StartChargingSessionUseCase useCase

    @Autowired
    ChargerRepository chargerRepository

    @Autowired
    ChargingSessionRepository chargingSessionRepository

    def "happy path"() {
        given:
            def chargerId = chargerRepository.create(new Charger(new Location(0, 0), Charger.Status.AVAILABLE))
            def command = new StartChargingSessionCommand(chargerId)
        when:
            def result = useCase.startChargingSession(command)
        then:
            result.status() == StartChargingSessionResult.Status.SUCCESS
            result.sessionId()
        and:
            chargerRepository.findById(chargerId).get().status() == Charger.Status.OCCUPIED
            chargingSessionRepository.findAllActiveByChargerId(chargerId).find { it.id() == result.sessionId() }
    }
}
