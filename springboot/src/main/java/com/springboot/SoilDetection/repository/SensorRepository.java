package com.springboot.SoilDetection.repository;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import com.springboot.SoilDetection.model.Sensor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public interface SensorRepository {

        @SqlQuery("SELECT * FROM sensors WHERE id = :id")
        Optional<Sensor> findById(@Bind("id") Long id);

        class SensorMapper implements RowMapper<Sensor> {

                @Override
                public Sensor map(ResultSet rs, StatementContext ctx) throws SQLException {
                        Sensor sensor = new Sensor();
                        sensor.setId(rs.getLong("id"));
                        sensor.setName(rs.getString("name"));
                        sensor.setSensorValue(rs.getDouble("sensor_value"));
                        return sensor;
                }
        }
}
