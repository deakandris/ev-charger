package deakandris.evcharger.domain;

import java.util.List;

public interface GetChargingSessionsUseCase {

    // TODO filters + pagination
    GetChargingSessionsResult getChargingSessions();

    record GetChargingSessionsResult(List<ChargingSession> chargingSessions) {
    }
}
