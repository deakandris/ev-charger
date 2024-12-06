package deakandris.evcharger.domain;

public interface StopChargingSessionUseCase {

    StopChargingSessionResult stopChargingSession(StopChargingSessionCommand command);

    record StopChargingSessionCommand(long chargerId) {
    }

    record StopChargingSessionResult(Status status, Cost cost) {

        enum Status {
            SUCCESS, NO_ACTIVE_CHARGING_SESSION
        }
    }
}
