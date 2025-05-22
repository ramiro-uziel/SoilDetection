package com.springboot.SoilDetection.repository;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.springboot.SoilDetection.model.Plant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RegisterRowMapper(PlantRepository.PlantMapper.class)
public interface PlantRepository {

    @SqlQuery("SELECT * FROM PLANTS")
    List<Plant> findAll();

    @SqlQuery("SELECT * FROM PLANTS WHERE plant_id = :plantId")
    Optional<Plant> findById(@Bind("plantId") Long plantId);

    @SqlUpdate("INSERT INTO PLANTS (plant_name, plant_species, plant_location, date_added, notes) " +
            "VALUES (:plantName, :plantSpecies, :plantLocation, :dateAdded, :notes)")
    @GetGeneratedKeys("plant_id")
    Long insert(@BindBean Plant plant);

    @SqlUpdate("UPDATE PLANTS SET plant_name = :plantName, plant_species = :plantSpecies, " +
            "plant_location = :plantLocation, date_added = :dateAdded, notes = :notes " +
            "WHERE plant_id = :plantId")
    int update(@BindBean Plant plant);

    @SqlUpdate("DELETE FROM PLANTS WHERE plant_id = :plantId")
    int delete(@Bind("plantId") Long plantId);

    class PlantMapper implements RowMapper<Plant> {
        @Override
        public Plant map(ResultSet rs, StatementContext ctx) throws SQLException {
            Plant plant = new Plant();
            plant.setPlantId(rs.getLong("plant_id"));
            plant.setPlantName(rs.getString("plant_name"));
            plant.setPlantSpecies(rs.getString("plant_species"));
            plant.setPlantLocation(rs.getString("plant_location"));
            plant.setDateAdded(rs.getDate("date_added"));
            plant.setNotes(rs.getString("notes"));
            return plant;
        }
    }
}
