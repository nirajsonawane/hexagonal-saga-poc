package com.ns.core.configuration

import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.domain.States.*
import com.ns.core.service.BankingFacade
import com.ns.core.service.CrmFacade
import com.ns.core.service.PortfolioFacade
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer


@Configuration
@EnableStateMachineFactory
class AccountEnrollmentSagaConfigurations(val portfolioFacade: PortfolioFacade, val bankingFacade: BankingFacade, val crmFacade: CrmFacade) : EnumStateMachineConfigurerAdapter<States, Events>(), Logging {


    @Throws(Exception::class)
    override fun configure(states: StateMachineStateConfigurer<States, Events>) {
        states
                .withStates()
                //State Machine Happy Path flow configurations
                .initial(STARTED)
                .stateEntry(BANK_ACCOUNT_CREATION_INITIATED, createAccountAction(), errorAction(Events.BANK_ACCOUNT_CREATION_ERROR))
                .stateEntry(BANK_ACCOUNT_CREATED, createProfileAction(), errorAction(Events.PROFILE_CREATION_ERROR))
                .stateEntry(PROFILE_CREATED, updateCrmAction(), errorAction(Events.CRM_UPDATE_ERROR))
                .state(CRM_UPDATED)
                // Rollback Transaction Configurations. CompensatingTransaction
                .state(BANK_ACCOUNT_CREATION_ERROR, runCompensatingTransaction())
                .state(PROFILE_CREATION_ERROR, runCompensatingTransaction())
                .state(CRM_UPDATE_ERROR, runCompensatingTransaction())
                //Terminal states of State Machine
                .end(CRM_UPDATED)
                .end(BANK_ACCOUNT_CREATION_ERROR)
                .end(PROFILE_CREATION_ERROR)
                .end(CRM_UPDATE_ERROR)


    }

    @Throws(java.lang.Exception::class)
    override fun configure(config: StateMachineConfigurationConfigurer<States, Events>) {
        config.withConfiguration()
                .listener(StateMachineEventListener())
                .autoStartup(true)
    }

    @Throws(java.lang.Exception::class)
    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>) {
        transitions
                .withExternal()
                .source(STARTED).target(BANK_ACCOUNT_CREATION_INITIATED).event(Events.STARTED)
                .and()
                .withExternal()
                .source(BANK_ACCOUNT_CREATION_INITIATED).target(BANK_ACCOUNT_CREATED).event(Events.BANK_ACCOUNT_CREATED)
                .and()
                .withExternal()
                .source(BANK_ACCOUNT_CREATION_INITIATED).target(BANK_ACCOUNT_CREATION_ERROR).event(Events.BANK_ACCOUNT_CREATION_ERROR)
                .and()
                .withExternal()
                .source(BANK_ACCOUNT_CREATED).target(PROFILE_CREATED).event(Events.PROFILE_CREATED)
                .and()
                .withExternal()
                .source(BANK_ACCOUNT_CREATED).target(PROFILE_CREATION_ERROR).event(Events.PROFILE_CREATION_ERROR)
                .and()
                .withExternal()
                .source(PROFILE_CREATED).target(CRM_UPDATED).event(Events.CRM_UPDATED)
                .and()
                .withExternal()
                .source(PROFILE_CREATED).target(CRM_UPDATE_ERROR).event(Events.CRM_UPDATE_ERROR)


    }

    @Bean
    fun createAccountAction(): Action<States, Events> = Action { bankingFacade.createAccount(it.message.headers["clientId"] as Long) }

    @Bean
    fun createProfileAction(): Action<States, Events> = Action { portfolioFacade.createProfile(it.message.headers["clientId"] as Long) }

    @Bean
    fun updateCrmAction(): Action<States, Events> = Action { crmFacade.updateStatus(it.message.headers["clientId"] as Long) }

    @Bean
    fun runCompensatingTransaction(): Action<States, Events> {
        return Action {
            bankingFacade.deleteAccount(it.message.headers["clientId"] as Long)
            portfolioFacade.deleteProfile(it.message.headers["clientId"] as Long)

        }
    }

    @Bean
    @Scope("prototype")
    fun errorAction(event: Events): Action<States?, Events?>? {
        return Action { context ->
            logger.error("Error Action for State ${context.stateMachine.state.id} Error Message ${context.exception.message}")
            val message = MessageBuilder.withPayload(event)
                    .setHeader("clientId", context.messageHeaders["clientId"] as Long)
                    .build()
            context.stateMachine.sendEvent(message)

        }
    }


}