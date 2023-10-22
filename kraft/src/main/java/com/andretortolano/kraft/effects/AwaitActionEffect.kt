package com.andretortolano.kraft.effects

import com.andretortolano.kraft.Action
import kotlinx.coroutines.CompletableDeferred

internal class AwaitActionEffect(private val action: Action) : DefaultSideEffect {

    private val completableDeferred = CompletableDeferred(Unit)

    suspend fun await() = completableDeferred.await()

    fun action() = completableDeferred.complete(Unit)

    override suspend fun launch() = action
}
