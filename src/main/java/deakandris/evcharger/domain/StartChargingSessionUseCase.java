package deakandris.evcharger.domain;

public interface StartChargingSessionUseCase {

    StartChargingSessionResult startChargingSession(StartChargingSessionCommand command);

    record StartChargingSessionCommand(double chargerId) {
    }

    record StartChargingSessionResult(Status status) {

        enum Status {
            SUCCESS, NOT_AVAILABLE
        }
    }
}
