package deakandris.evcharger.domain;

public record Charger(Long id, Location location, Status status) {

    public Charger(Location location, Status status) {
        this(null, location, status);
    }

    public enum Status {
        AVAILABLE, BOOKED, OCCUPIED
    }
}
