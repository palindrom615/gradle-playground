package com.example

import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.slf4j.LoggerFactory


class Main {
    private val logger = LoggerFactory.getLogger(Main::class.java)
    fun setUp() {
        // A SessionFactory is set up once for an application!
        val registry =
            StandardServiceRegistryBuilder()
                .build()
        try {
            val sessionFactory =
                MetadataSources(registry)
                    .addAnnotatedClass(Application::class.java)
                    .addAnnotatedClasses(Review::class.java)
                    .buildMetadata()
                    .buildSessionFactory()
            sessionFactory.inTransaction { session ->
                val application = Application()
                session.persist(application)
                session.flush()
                val review = Review(
                    id = null,
                    applicationId = application.id
                )
                session.persist(review)
                session.flush()
                session.getReference(Review::class.java, review.id)
            }

        } catch (e: Exception) {
            logger.error("Error building SessionFactory", e)
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry)
        }
    }
}

fun main() {
    Main().setUp()
}