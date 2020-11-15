package com.ns

import com.ns.adapter.persistence.ClientRepository
import com.ns.adapter.persistence.entity.Client
import com.ns.core.configuration.StateMachineInMemoryFactoryFactory
import com.ns.core.domain.Country
import com.ns.core.domain.States
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class HappyPathIntegrationTest {


    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var stateMachineInMemoryFactoryFactory: StateMachineInMemoryFactoryFactory

    @Autowired
    lateinit var clientRepository: ClientRepository


    @BeforeEach
    fun setup() {
        clientRepository.deleteAll()
        val client = Client(clientId = 1, firstName = "Test", lastName = "", email = "", mobileNumber = "", taxNumber = "", country = Country.DE)
        clientRepository.save(client)

    }

    @Test
    fun `Account Enrollment Saga should end with CRM_UPDATE state`() {
        mockMvc
                .perform(post("/client/1/enroll"))
                .andExpect(status().isAccepted);

        assertEquals(States.CRM_UPDATED.name, stateMachineInMemoryFactoryFactory.getStateMachine(1).state.id.name)


    }

}
