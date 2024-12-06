package deakandris.evcharger.domain;

import lombok.Getter;

@Getter
public class ChargerNotFoundException extends RuntimeException {

    private final long chargerId;

    public ChargerNotFoundException(long chargerId) {
        super(String.format("Charger not found with ID: %d", chargerId));
        this.chargerId = chargerId;
    }
}
