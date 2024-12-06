package deakandris.evcharger.domain;

public record Charger(Long id, Location location, Status status) {

    public enum Status {
        AVAILABLE, BOOKED, OCCUPIED
    }
}
