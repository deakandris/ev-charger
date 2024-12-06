package deakandris.evcharger.domain;

public interface BookChargerUseCase {

    BookChargerResult bookCharger(BookChargerCommand command);

    record BookChargerCommand(long chargerId) {
    }

    record BookChargerResult(Status status) {

        enum Status {
            SUCCESS, NOT_AVAILABLE
        }
    }
}
