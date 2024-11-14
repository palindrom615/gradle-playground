package com.example

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "review")
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(name = "application_id")
    val applicationId: Long?,
) {
    @OneToOne(cascade = [CascadeType.ALL, CascadeType.PERSIST])
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    var application: Application? = null
}
