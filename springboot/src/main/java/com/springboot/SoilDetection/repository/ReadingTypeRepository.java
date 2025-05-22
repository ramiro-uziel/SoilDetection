package com.springboot.SoilDetection.repository;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.springboot.SoilDetection.model.ReadingType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RegisterRowMapper(ReadingTypeRepository.ReadingTypeMapper.class)
public interface ReadingTypeRepository {

    @SqlQuery("SELECT * FROM READING_TYPES")
    List<ReadingType> findAll();

    @SqlQuery("SELECT * FROM READING_TYPES WHERE reading_type_id = :readingTypeId")
    Optional<ReadingType> findById(@Bind("readingTypeId") Long readingTypeId);

    @SqlQuery("SELECT * FROM READING_TYPES WHERE reading_name = :readingName")
    Optional<ReadingType> findByName(@Bind("readingName") String readingName);

    @SqlUpdate("INSERT INTO READING_TYPES (reading_name, unit_of_measure, min_healthy_value, max_healthy_value, description) "
            +
            "VALUES (:readingName, :unitOfMeasure, :minHealthyValue, :maxHealthyValue, :description)")
    @GetGeneratedKeys("reading_type_id")
    Long insert(@BindBean ReadingType readingType);

    @SqlUpdate("UPDATE READING_TYPES SET reading_name = :readingName, unit_of_measure = :unitOfMeasure, " +
            "min_healthy_value = :minHealthyValue, max_healthy_value = :maxHealthyValue, " +
            "description = :description WHERE reading_type_id = :readingTypeId")
    int update(@BindBean ReadingType readingType);

    @SqlUpdate("DELETE FROM READING_TYPES WHERE reading_type_id = :readingTypeId")
    int delete(@Bind("readingTypeId") Long readingTypeId);

    class ReadingTypeMapper implements RowMapper<ReadingType> {
        @Override
        public ReadingType map(ResultSet rs, StatementContext ctx) throws SQLException {
            ReadingType readingType = new ReadingType();
            readingType.setReadingTypeId(rs.getLong("reading_type_id"));
            readingType.setReadingName(rs.getString("reading_name"));
            readingType.setUnitOfMeasure(rs.getString("unit_of_measure"));
            readingType.setMinHealthyValue(rs.getDouble("min_healthy_value"));
            readingType.setMaxHealthyValue(rs.getDouble("max_healthy_value"));
            readingType.setDescription(rs.getString("description"));
            return readingType;
        }
    }
}
