package deakandris.evcharger.domain;

public interface StartChargingSessionUseCase {

    StartChargingSessionResult startChargingSession(StartChargingSessionCommand command);

    record StartChargingSessionCommand(long chargerId) {
    }

    record StartChargingSessionResult(Status status, Long sessionId) {

        static StartChargingSessionResult success(long sessionId) {
            return new StartChargingSessionResult(Status.SUCCESS, sessionId);
        }

        static StartChargingSessionResult notAvailable() {
            return new StartChargingSessionResult(Status.NOT_AVAILABLE, null);
        }

        enum Status {
            SUCCESS, NOT_AVAILABLE
        }
    }
}
