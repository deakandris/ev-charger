package deakandris.evcharger.persistence;

import deakandris.evcharger.domain.ChargingSession;
import deakandris.evcharger.domain.ChargingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChargingSessionRepositoryImpl implements ChargingSessionRepository {

    private final NamedParameterJdbcTemplate template;

    @Override
    public List<ChargingSession> findAll() {
        return template.query("SELECT * FROM charging_session", ChargingSessionRepositoryImpl::mapRow);
    }

    @Override
    public List<ChargingSession> findAllActiveByChargerId(long chargerId) {
        var params = new MapSqlParameterSource("chargerId", chargerId);
        return template.query("SELECT * FROM charging_session WHERE charger_id = :chargerId AND end IS NULL", params, ChargingSessionRepositoryImpl::mapRow);
    }

    @Override
    public long create(ChargingSession session) {
        if (session.id() != null) {
            throw new IllegalArgumentException("Attempting to create charging session with predefined ID.");
        }
        var params = new MapSqlParameterSource()
                .addValue("chargerId", session.chargerId())
                .addValue("start", session.start());
        var keyHolder = new GeneratedKeyHolder();
        template.update("INSERT INTO charging_session(charger_id, start) VALUES(:chargerId, :start)", params, keyHolder);
        return Optional.ofNullable(keyHolder.getKeys())
                .map(it -> it.get("id"))
                .filter(it -> it instanceof Long)
                .map(Long.class::cast)
                .orElseThrow(() -> new IllegalStateException("Insert charging session did not generate a proper ID."));
    }

    @Override
    public void updateEndByChargerId(Instant end) {

    }

    private static ChargingSession mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ChargingSession(
                rs.getLong("id"),
                rs.getLong("charger_id"),
                rs.getTimestamp("start").toInstant(),
                Optional.ofNullable(rs.getTimestamp("end")).map(Timestamp::toInstant).orElse(null)
        );
    }
}
