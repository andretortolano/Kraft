package com.andretortolano.kraft.effects

import com.andretortolano.kraft.Action

internal class ActionEffect(private val action: Action) : DefaultSideEffect {

    override suspend fun launch() = action
}
