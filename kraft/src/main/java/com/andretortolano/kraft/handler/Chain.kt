package com.andretortolano.kraft.handler

import com.andretortolano.kraft.Action
import com.andretortolano.kraft.Kraft
import com.andretortolano.kraft.State
import com.andretortolano.kraft.Transition
import kotlinx.coroutines.CompletableDeferred

class Chain(
    private val kraft: Kraft,
    private val action: Action
) {

    data class ChainData(
        val state: State,
        val action: Action
    )

    private val completableDeferred = CompletableDeferred(Unit)

    internal var defaultTransition: (State.(Action) -> Transition) = { keep() }

    internal suspend fun begin() {
        completableDeferred.await()
    }

    fun before(): ChainData = ChainData(kraft.currentState, action)

    fun proceed(defaultTransition: (State.(Action) -> Transition)? = null) {
        defaultTransition?.let { this.defaultTransition = it }
        completableDeferred.complete(Unit)
    }
}
