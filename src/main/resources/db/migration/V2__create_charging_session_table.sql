CREATE TABLE IF NOT EXISTS charging_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    charger_id BIGINT REFERENCES charger,
    start TIMESTAMP NOT NULL,
    end TIMESTAMP,
)
