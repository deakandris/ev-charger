package deakandris.evcharger.domain

import deakandris.evcharger.domain.StartChargingSessionUseCase.StartChargingSessionCommand
import deakandris.evcharger.domain.StartChargingSessionUseCase.StartChargingSessionResult
import deakandris.evcharger.domain.StopChargingSessionUseCase.StopChargingSessionCommand
import deakandris.evcharger.domain.StopChargingSessionUseCase.StopChargingSessionResult
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

import static java.time.Instant.now
import static java.time.temporal.ChronoUnit.MILLIS

class ChargingSessionServiceSpec extends Specification {

    ChargingSessionRepository repository
    ChargerService chargerService

    @Subject
    ChargingSessionService chargingSessionService

    def setup() {
        repository = Mock()
        chargerService = Mock()
        chargingSessionService = new ChargingSessionService(repository, chargerService)
    }

    def "get charging sessions"() {
        given:
            def session1 = new ChargingSession(12345L, 23456L, now() - Duration.ofMinutes(10), now() - Duration.ofMinutes(5))
            def session2 = new ChargingSession(12346L, 23456L, now() - Duration.ofMinutes(10))
        when:
            def result = chargingSessionService.getChargingSessions()
        then:
            result.chargingSessions() == [session1, session2]
        and:
            1 * repository.findAll() >> [session1, session2]
    }

    def "start charging session: not available"() {
        given:
            def command = new StartChargingSessionCommand(23456L)
        when:
            def result = chargingSessionService.startChargingSession(command)
        then:
            result.status() == StartChargingSessionResult.Status.NOT_AVAILABLE
        and:
            1 * chargerService.occupy(command.chargerId()) >> false
    }

    def "start charging session: success"() {
        given:
            def chargerId = 23456L
            def sessionId = 34567L
            def command = new StartChargingSessionCommand(chargerId)
        when:
            def result = chargingSessionService.startChargingSession(command)
        then:
            result.status() == StartChargingSessionResult.Status.SUCCESS
            result.sessionId() == sessionId
        and:
            1 * chargerService.occupy(chargerId) >> true
            1 * repository.create({ it.chargerId() == chargerId && it.start().until(now(), MILLIS) < 100 }) >> sessionId
    }

    def "stop charging session: no active charging session"() {
        given:
            def chargerId = 23456L
            def command = new StopChargingSessionCommand(chargerId)
        when:
            def result = chargingSessionService.stopChargingSession(command)
        then:
            result.status() == StopChargingSessionResult.Status.NO_ACTIVE_CHARGING_SESSION
        and:
            1 * repository.findAllActiveByChargerId(chargerId) >> []
    }

    def "stop charging session: sucess"() {
        given:
            def chargerId = 23456L
            def sessionId = 34567L
            def command = new StopChargingSessionCommand(chargerId)
            def session = new ChargingSession(sessionId, chargerId, now() - Duration.ofMinutes(10))
        when:
            def result = chargingSessionService.stopChargingSession(command)
        then:
            result.status() == StopChargingSessionResult.Status.SUCCESS
            result.cost().amount() == 30
            result.cost().currency() == Currency.getInstance('GBP')
        and:
            1 * repository.findAllActiveByChargerId(chargerId) >> [session]
    }
}
