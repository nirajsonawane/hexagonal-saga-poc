package com.ns.adapter.persistence

import com.ns.adapter.persistence.entity.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository :JpaRepository<Client,Long>