package com.andretortolano.kraft.effects

import com.andretortolano.kraft.Action
import com.andretortolano.kraft.SideEffect

interface DefaultSideEffect : SideEffect {

    suspend fun launch(): Action?
}
