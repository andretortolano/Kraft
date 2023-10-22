package com.andretortolano.kraft

import com.andretortolano.kraft.effects.ActionEffect
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class KraftTest {

    @Test
    fun `Test Water sample`() {
        val kraft = Kraft(Liquid(20))

        assertThat(kraft.currentState).isEqualTo(Liquid(20))
    }

    @Test
    fun `Test Water sample 2`() = runBlocking {
        val kraft = Kraft(Liquid(20))

        kraft.handle(WaterAction.Heat(90))

        assertThat(kraft.currentState).isEqualTo(Gas(110))
    }

    @Test
    fun `Test Water sample 3`() = runBlocking {
        val kraft = Kraft(Liquid(20))

        kraft.handle(WaterAction.Cool(60))

        assertThat(kraft.currentState).isEqualTo(Solid(-40))
    }


    sealed class WaterAction : Action {
        data class Heat(val add: Int) : WaterAction()
        data class Cool(val subtract: Int) : WaterAction()
    }

    data class Liquid(val temperature: Int) : State {
        override fun take(action: Action): Transition? {
            return when (action) {
                is WaterAction.Heat -> {
                    val newTemp = temperature + action.add
                    if (newTemp >= 100) {
                        next(Gas(newTemp))
                    } else {
                        next(Liquid(newTemp))
                    }
                }

                is WaterAction.Cool -> {
                    val newTemp = temperature - action.subtract
                    if (newTemp <= 0) {
                        next(Solid(newTemp))
                    } else {
                        next(Liquid(newTemp))
                    }
                }

                else -> null
            }
        }
    }

    data class Gas(val temperature: Int) : State {
        override fun take(action: Action): Transition? {
            return when (action) {
                is WaterAction.Heat -> {
                    val newTemp = temperature + action.add
                    next(Gas(newTemp))
                }

                is WaterAction.Cool -> {
                    val newTemp = temperature - action.subtract
                    if (newTemp < 100) {
                        next(Liquid(newTemp), ActionEffect(WaterAction.Cool(0)))
                    } else {
                        next(Gas(newTemp))
                    }
                }

                else -> null
            }
        }
    }

    data class Solid(val temperature: Int) : State {
        override fun take(action: Action): Transition? {
            return when (action) {
                is WaterAction.Heat -> {
                    val newTemp = temperature + action.add
                    if (newTemp > 0) {
                        next(Liquid(newTemp), ActionEffect(WaterAction.Heat(0)))
                    } else {
                        next(Solid(newTemp))
                    }
                }

                is WaterAction.Cool -> {
                    val newTemp = temperature - action.subtract
                    next(Solid(newTemp))
                }

                else -> null
            }
        }
    }
}
