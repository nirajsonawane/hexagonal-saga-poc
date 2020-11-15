package com.ns

import com.ns.adapter.banking.BankingPortAdapter
import com.ns.adapter.crm.CrmPortAdapter
import com.ns.adapter.persistence.ClientRepository
import com.ns.adapter.persistence.entity.Client
import com.ns.adapter.portfolio.PortfolioService
import com.ns.core.configuration.StateMachineInMemoryFactory
import com.ns.core.domain.ClientStatus
import com.ns.core.domain.Country
import com.ns.core.domain.States
import org.apache.logging.log4j.kotlin.Logging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class RunCompensatingTransactionIntegrationTest : Logging {


    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var stateMachineInMemoryFactory: StateMachineInMemoryFactory

    @Autowired
    lateinit var clientRepository: ClientRepository

    @MockBean
    lateinit var portfolioService: PortfolioService

    @MockBean
    lateinit var bankingPortAdapter: BankingPortAdapter

    @MockBean
    lateinit var crmPortAdapter:CrmPortAdapter

    @BeforeEach
    fun setup() {
        stateMachineInMemoryFactory.cleanUp()
        clientRepository.deleteAll()
        val client = Client(clientId = null, firstName = "Test", lastName = "", email = "", mobileNumber = "", taxNumber = "", country = Country.DE)
        clientRepository.save(client)

    }

    @Nested
    inner class BankingPort{

        @Test
        fun `Account Enrollment Saga should end with Account Creation Error state if Banking port throws exception`() {
            val client = clientRepository.findAll()[0].toDomain()
            Mockito.`when`(bankingPortAdapter.createAccount(client = client)).thenThrow(RuntimeException("Mock Exception"))
            mockMvc
                    .perform(post("/client/${client.id}/enroll"))
                    .andExpect(status().isAccepted)

            assertEquals(States.BANK_ACCOUNT_CREATION_ERROR.name, stateMachineInMemoryFactory.getStateMachine(client.id!!).state.id.name)
        }
    }

    @Nested
    inner class PortfolioPort{

        @Test
        fun `Account Enrollment Saga should end with Profile Creation Error state if Portfolio port throws exception`() {
            val client = clientRepository.findAll()[0].toDomain()
            Mockito.doNothing().`when`(bankingPortAdapter).createAccount(client = client)
            Mockito.`when`(portfolioService.createProfile(client)).thenThrow(RuntimeException("Mock Exception from portfolio Service"))
            mockMvc
                    .perform(post("/client/${client.id}/enroll"))
                    .andExpect(status().isAccepted);

            assertEquals(States.PROFILE_CREATION_ERROR.name, stateMachineInMemoryFactory.getStateMachine(client.id!!).state.id.name)
        }
    }

    @Nested
    inner class CRMPort{

        @Test
        fun `Account Enrollment Saga should end with Profile Creation Error state if CRM port throws exception`() {
            val client = clientRepository.findAll()[0].toDomain()
            Mockito.doNothing().`when`(bankingPortAdapter).createAccount(client = client)
            Mockito.doNothing().`when`(portfolioService).createProfile(client = client)
            Mockito.`when`(crmPortAdapter.updateClientStatus(client.id!!,ClientStatus.REGISTERED)).thenThrow(RuntimeException("Mock Exception from CRM  Port"))
            mockMvc
                    .perform(post("/client/${client.id}/enroll"))
                    .andExpect(status().isAccepted);

            assertEquals(States.CRM_UPDATE_ERROR.name, stateMachineInMemoryFactory.getStateMachine(client.id!!).state.id.name)
        }
    }


}
