package deakandris.evcharger.domain;

import java.time.Instant;
import java.util.List;

public interface ChargingSessionRepository {

    List<ChargingSession> findAll();

    List<ChargingSession> findAllActiveByChargerId(long chargerId);

    long create(ChargingSession session);

    void updateEndByChargerId(Instant end);
}
