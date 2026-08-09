package com.opor.lib.cfg.dbu.aut

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Shared PostgreSQL + JPA configuration for the Auth (aut) domain.
 *
 * This configuration class acts as the entry point for Spring Boot's
 * auto-configured DataSource and JPA infrastructure. Each microservice
 * under the aut domain depends on this library and provides its own
 * connection values via application.yml.
 *
 * The DataSource, EntityManagerFactory, and TransactionManager beans
 * are auto-configured by Spring Boot — this class exists to provide
 * a clear dependency boundary and enable component scanning from
 * the shared library package.
 */
@Configuration
class DbAutConfig
