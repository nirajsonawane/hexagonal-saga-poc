package com.ns.core.configuration

import com.ns.core.domain.Events
import com.ns.core.domain.States
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.context.annotation.Lazy
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.stereotype.Component

@Component
class StateMachineInMemoryFactoryFactory(@Lazy val stateMachineFactory: StateMachineFactory<States, Events>) : Logging {


    val stateMachineMap = HashMap<Long, StateMachine<States, Events>>()

    fun getStateMachine(machineId: Long): StateMachine<States, Events> {
        val stateMachine = stateMachineMap[machineId]
        if (stateMachine == null) {
            logger.info("State Machine")
            val stateMachine1 = stateMachineFactory.getStateMachine(machineId.toString())
            stateMachineMap[machineId] = stateMachine1
            return stateMachine1
        }
        logger.info("State Machine Found,Current State : ${stateMachine.state.id}")
        return stateMachine

    }

}