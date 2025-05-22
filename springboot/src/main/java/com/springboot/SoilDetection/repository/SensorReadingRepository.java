package com.springboot.SoilDetection.repository;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.springboot.SoilDetection.model.SensorReading;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RegisterRowMapper(SensorReadingRepository.SensorReadingMapper.class)
public interface SensorReadingRepository {

    @SqlQuery("SELECT * FROM SENSOR_READINGS")
    List<SensorReading> findAll();

    @SqlQuery("SELECT * FROM SENSOR_READINGS WHERE reading_id = :readingId")
    Optional<SensorReading> findById(@Bind("readingId") Long readingId);

    @SqlQuery("SELECT * FROM SENSOR_READINGS WHERE plant_id = :plantId")
    List<SensorReading> findByPlantId(@Bind("plantId") Long plantId);

    @SqlQuery("SELECT * FROM SENSOR_READINGS WHERE plant_id = :plantId AND reading_type_id = :readingTypeId")
    List<SensorReading> findByPlantAndReadingType(@Bind("plantId") Long plantId,
            @Bind("readingTypeId") Long readingTypeId);

    @SqlQuery("SELECT * FROM SENSOR_READINGS WHERE plant_id = :plantId AND reading_type_id = :readingTypeId " +
            "AND reading_timestamp BETWEEN :startTime AND :endTime ORDER BY reading_timestamp DESC")
    List<SensorReading> findByPlantAndReadingTypeAndTimeRange(
            @Bind("plantId") Long plantId,
            @Bind("readingTypeId") Long readingTypeId,
            @Bind("startTime") Timestamp startTime,
            @Bind("endTime") Timestamp endTime);

    @SqlQuery("SELECT * FROM SENSOR_READINGS WHERE plant_id = :plantId AND reading_type_id = :readingTypeId " +
            "ORDER BY reading_timestamp DESC FETCH FIRST :limit ROWS ONLY")
    List<SensorReading> findLatestByPlantAndReadingType(
            @Bind("plantId") Long plantId,
            @Bind("readingTypeId") Long readingTypeId,
            @Bind("limit") int limit);

    @SqlUpdate("INSERT INTO SENSOR_READINGS (plant_id, reading_type_id, reading_value, reading_timestamp) " +
            "VALUES (:plantId, :readingTypeId, :readingValue, :readingTimestamp)")
    @GetGeneratedKeys("reading_id")
    Long insert(@BindBean SensorReading sensorReading);

    @SqlUpdate("UPDATE SENSOR_READINGS SET plant_id = :plantId, reading_type_id = :readingTypeId, " +
            "reading_value = :readingValue, reading_timestamp = :readingTimestamp " +
            "WHERE reading_id = :readingId")
    int update(@BindBean SensorReading sensorReading);

    @SqlUpdate("DELETE FROM SENSOR_READINGS WHERE reading_id = :readingId")
    int delete(@Bind("readingId") Long readingId);

    class SensorReadingMapper implements RowMapper<SensorReading> {
        @Override
        public SensorReading map(ResultSet rs, StatementContext ctx) throws SQLException {
            SensorReading reading = new SensorReading();
            reading.setReadingId(rs.getLong("reading_id"));
            reading.setPlantId(rs.getLong("plant_id"));
            reading.setReadingTypeId(rs.getLong("reading_type_id"));
            reading.setReadingValue(rs.getDouble("reading_value"));
            reading.setReadingTimestamp(rs.getTimestamp("reading_timestamp"));
            return reading;
        }
    }
}
