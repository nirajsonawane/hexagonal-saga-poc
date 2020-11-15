package com.ns.adapter.persistence.entity

import com.ns.core.domain.Client
import com.ns.core.domain.Country
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Client(@Id @GeneratedValue val clientId: Long?, val firstName: String, val lastName: String, val email: String, val mobileNumber: String, val taxNumber: String, val country: Country) {
    companion object {
        fun fromDomain(client: Client): com.ns.adapter.persistence.entity.Client {
            return Client(
                    firstName = client.firstName,
                    lastName = client.lastName,
                    email = client.email,
                    mobileNumber = client.mobileNumber,
                    taxNumber = client.taxNumber,
                    country = client.country,
                    clientId = null
            )
        }
    }

    fun toDomain(): Client {
        return Client(firstName = firstName, lastName = lastName, email = email, mobileNumber = mobileNumber,
                taxNumber = taxNumber, country = country,id = clientId)

    }
}