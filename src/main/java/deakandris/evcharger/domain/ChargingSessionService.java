package deakandris.evcharger.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Currency;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargingSessionService implements GetChargingSessionsUseCase, StartChargingSessionUseCase, StopChargingSessionUseCase {

    private static final BigDecimal COST_PER_SEC_GBP = BigDecimal.valueOf(0.05);

    private final ChargingSessionRepository repository;
    private final ChargerService chargerService;

    @Override
    public GetChargingSessionsResult getChargingSessions() {
        var chargingSessions = repository.findAll();
        return new GetChargingSessionsResult(chargingSessions);
    }

    @Override
    public StartChargingSessionResult startChargingSession(StartChargingSessionCommand command) {
        boolean chargerOccupiedSuccessfully = chargerService.occupy(command.chargerId());
        if (!chargerOccupiedSuccessfully) {
            return StartChargingSessionResult.notAvailable();
        }
        var session = new ChargingSession(command.chargerId());
        long sessionId = repository.create(session);
        return StartChargingSessionResult.success(sessionId);
    }

    @Override
    public StopChargingSessionResult stopChargingSession(StopChargingSessionCommand command) {
        var sessions = repository.findAllActiveByChargerId(command.chargerId());
        if (sessions.isEmpty()) {
            return StopChargingSessionResult.noActiveChargingSession();
        }
        if (sessions.size() > 1) {
            log.warn("More then one active charging sessions found for charger {}. Charging by newest one and stopping all sessions", command.chargerId());
        }
        Instant end = Instant.now();
        repository.updateEndByChargerId(end);
        var latestSession = sessions.stream().reduce(maxBy(comparing(ChargingSession::start))).orElseThrow();
        var cost = calculateCost(Duration.between(latestSession.start(), end));
        return StopChargingSessionResult.success(cost);
    }

    private static Cost calculateCost(Duration duration) {
        var amount = COST_PER_SEC_GBP.multiply(BigDecimal.valueOf(duration.getSeconds()));
        return new Cost(amount, Currency.getInstance("GBP"));
    }
}
