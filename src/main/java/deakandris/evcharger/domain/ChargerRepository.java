package deakandris.evcharger.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargerRepository {

    List<Charger> findAllByLatitudeBetweenAndLongitudeBetween(double latitudeMin, double latitudeMax, double longitudeMin, double longitudeMax);

    Optional<Charger> findById(long chargerId);

    void updateStatus(Charger.Status status);
}
