package deakandris.evcharger.persistence;

import deakandris.evcharger.domain.Charger;
import deakandris.evcharger.domain.ChargerRepository;
import deakandris.evcharger.domain.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChargerRepositoryImpl implements ChargerRepository {

    private static final String SELECT_BY_LATITUDE_AND_LONGITUDE_BETWEEN_SQL = "SELECT * FROM charger WHERE latitude BETWEEN :latitudeMin AND :latitudeMax AND longitude BETWEEN :longitudeMin AND :longitudeMax";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM charger WHERE id = :id";
    private static final String UPDATE_STATUS_BY_ID_SQL = "UPDATE charger SET status = :status WHERE ID = :id";
    private final NamedParameterJdbcTemplate template;

    @Override
    public List<Charger> findAllByLatitudeBetweenAndLongitudeBetween(double latitudeMin, double latitudeMax, double longitudeMin, double longitudeMax) {
        var params = new MapSqlParameterSource()
                .addValue("latitudeMin", latitudeMin)
                .addValue("latitudeMax", latitudeMax)
                .addValue("longitudeMin", longitudeMin)
                .addValue("longitudeMax", longitudeMax);
        return template.query(SELECT_BY_LATITUDE_AND_LONGITUDE_BETWEEN_SQL, params, ChargerRepositoryImpl::mapRow);
    }

    @Override
    public Optional<Charger> findById(long chargerId) {
        var params = new MapSqlParameterSource("id", chargerId);
        try {
            return Optional.ofNullable(template.queryForObject(SELECT_BY_ID_SQL, params, ChargerRepositoryImpl::mapRow));
        } catch (
        IncorrectResultSizeDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public long create(Charger charger) {
        if (charger.id() != null) {
            throw new IllegalArgumentException("Attempting to create charger with predefined ID.");
        }
        var params = new MapSqlParameterSource()
                .addValue("latitude", charger.location().latitude())
                .addValue("longitude", charger.location().longitude())
                .addValue("status", charger.status());
        var keyHolder = new GeneratedKeyHolder();
        template.update("INSERT INTO charger(latitude, longitude, status) VALUES(:latitude, :longitude, :status)", params, keyHolder);
        return Optional.ofNullable(keyHolder.getKeys())
                .map(it -> it.get("id"))
                .filter(it -> it instanceof Long)
                .map(Long.class::cast)
                .orElseThrow(() -> new IllegalStateException("Insert charging session did not generate a proper ID."));
    }

    @Override
    public void updateStatus(long chargerId, Charger.Status status) {
        var params = new MapSqlParameterSource()
                .addValue("id", chargerId)
                .addValue("status", status);
        int rowCount = template.update(UPDATE_STATUS_BY_ID_SQL, params);
        if (rowCount != 1) {
            throw new IllegalStateException(String.format("Update charger by ID did not result in exactly one row update. Actual row count: %d", rowCount));
        }
    }

    private static Charger mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Charger(
                rs.getLong("id"),
                new Location(rs.getDouble("latitude"), rs.getDouble("longitude")),
                Charger.Status.valueOf(rs.getString("status"))
        );
    }
}
