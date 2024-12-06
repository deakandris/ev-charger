package deakandris.evcharger.domain;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChargingSessionRepository {

    List<ChargingSession> findAll();

    List<ChargingSession> findAllActiveByChargerId(long chargerId);

    long create(ChargingSession session);

    void updateEndByChargerId(Instant end);
}
