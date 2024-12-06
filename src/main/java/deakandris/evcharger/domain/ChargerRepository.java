package deakandris.evcharger.domain;

import java.util.List;
import java.util.Optional;

public interface ChargerRepository {

    List<Charger> findAllByLatitudeBetweenAndLongitudeBetween(double latitudeMin, double latitudeMax, double longitudeMin, double longitudeMax);

    Optional<Charger> findById(long chargerId);

    long create(Charger charger);

    void updateStatus(long chargerId, Charger.Status status);
}
