package com.ns.core.domain

data class Client(val id: Long? = null, val firstName: String, val lastName: String, val email: String, val mobileNumber: String, val taxNumber: String, val country: Country)