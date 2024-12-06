package deakandris.evcharger.domain;

public interface StopChargingSessionUseCase {

    StopChargingSessionResult stopChargingSession(StopChargingSessionCommand command);

    record StopChargingSessionCommand(long chargerId) {
    }

    record StopChargingSessionResult(Status status, Cost cost) {

        static StopChargingSessionResult success(Cost cost) {
            return new StopChargingSessionResult(Status.SUCCESS, cost);
        }

        static StopChargingSessionResult noActiveChargingSession() {
            return new StopChargingSessionResult(Status.NO_ACTIVE_CHARGING_SESSION, null);
        }

        enum Status {
            SUCCESS, NO_ACTIVE_CHARGING_SESSION
        }
    }
}
