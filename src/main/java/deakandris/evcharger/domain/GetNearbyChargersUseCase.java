package deakandris.evcharger.domain;

import java.util.List;

public interface GetNearbyChargersUseCase {

    GetNearbyChargersResult getNearbyChargers(GetNearbyChargersCommand command);

    record GetNearbyChargersCommand(Location location) {
    }

    record GetNearbyChargersResult(List<Charger> chargers) {
    }
}
