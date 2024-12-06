package deakandris.evcharger.domain;

import java.time.Instant;

public record ChargingSession(Long id, long chargerId, Instant start, Instant end) {
}
