package com.springboot.SoilDetection.config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
        private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

        @Bean
        public Jdbi jdbi(DataSource dataSource) {
                logger.info("Initializing Jdbi with transaction-aware DataSource");
                TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
                Jdbi jdbi = Jdbi.create(dataSourceProxy);

                jdbi.installPlugin(new SqlObjectPlugin());

                jdbi.registerRowMapper(com.springboot.SoilDetection.model.Sensor.class,
                                new com.springboot.SoilDetection.repository.SensorRepository.SensorMapper());

                logger.info("Jdbi initialized with all repository mappers");
                return jdbi;
        }
}