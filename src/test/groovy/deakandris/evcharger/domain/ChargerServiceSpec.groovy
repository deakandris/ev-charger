package deakandris.evcharger.domain

import deakandris.evcharger.domain.GetNearbyChargersUseCase.GetNearbyChargersCommand
import spock.lang.Specification
import spock.lang.Subject

class ChargerServiceSpec extends Specification {

    ChargerRepository repository

    @Subject
    ChargerService chargerService

    def setup() {
        repository = Mock()
        chargerService = new ChargerService(repository)
    }

    def "get nearby chargers"() {
        given:
            def command = new GetNearbyChargersCommand(new Location(0, 0))
            def charger1 = new Charger(12345L, new Location(12.34, 23.45), Charger.Status.AVAILABLE)
            def charger2 = new Charger(12346L, new Location(23.45, 34.56), Charger.Status.BOOKED)
            def charger3 = new Charger(12347L, new Location(34.56, 45.67), Charger.Status.OCCUPIED)
        when:
            def result = chargerService.getNearbyChargers(command)
        then:
            result.chargers() == [charger1, charger2, charger3]
        and:
            1 * repository.findAllByLatitudeBetweenAndLongitudeBetween(-0.01, 0.01, -0.01, 0.01) >> [charger1, charger2, charger3]
    }

    def "occupy charger: not found"() {
        given:
            def chargerId = 12345L
        when:
            chargerService.occupy(chargerId)
        then:
            def exception = thrown ChargerNotFoundException
            exception.chargerId == chargerId
        and:
            1 * repository.findById(chargerId) >> Optional.empty()
    }

    def "occupy charger: result based on status"() {
        given:
            def charger = new Charger(12345L, new Location(0, 0), status)
        when:
            def result = chargerService.occupy(charger.id())
        then:
            result == expected
        and:
            1 * repository.findById(charger.id()) >> Optional.of(charger)
        where:
            status                   | expected
            Charger.Status.AVAILABLE | true
            Charger.Status.BOOKED    | false
            Charger.Status.OCCUPIED  | false
    }
}
