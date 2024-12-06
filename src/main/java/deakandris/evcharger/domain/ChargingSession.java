package deakandris.evcharger.domain;

import java.time.Instant;

public record ChargingSession(Long id, long chargerId, Instant start, Instant end) {

    public ChargingSession(long chargerId) {
        this(null, chargerId, Instant.now(), null);
    }

    public ChargingSession(long id, long chargerId, Instant start) {
        this(id, chargerId, start, null);
    }
}
