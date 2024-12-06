package deakandris.evcharger.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargerService implements GetNearbyChargersUseCase { // TODO implement BookChargerUseCase

    private static final double LATITUDE_DELTA = 0.01;
    private static final double LONGITUDE_DELTA = 0.01;

    private final ChargerRepository repository;

    @Override
    public GetNearbyChargersResult getNearbyChargers(GetNearbyChargersCommand command) {
        var location = command.location();
        var chargers = repository.findAllByLatitudeBetweenAndLongitudeBetween(
                location.latitude() - LATITUDE_DELTA, location.latitude() + LATITUDE_DELTA,
                location.longitude() - LONGITUDE_DELTA, location.longitude() + LONGITUDE_DELTA);
        return new GetNearbyChargersResult(chargers);
    }

    boolean occupy(long chargerId) {
        var charger = repository.findById(chargerId)
                .orElseThrow(() -> new ChargerNotFoundException(chargerId));
        if (charger.status() == Charger.Status.AVAILABLE) {
            repository.updateStatus(Charger.Status.OCCUPIED);
            return true;
        } else {
            return false;
        }
    }
}
