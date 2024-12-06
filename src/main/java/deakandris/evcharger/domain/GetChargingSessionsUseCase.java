package deakandris.evcharger.domain;

import java.util.List;

public interface GetChargingSessionsUseCase {

    GetChargingSessionsResult getChargingSessions();

    record GetChargingSessionsResult(List<ChargingSession> chargingSessions) {
    }
}
