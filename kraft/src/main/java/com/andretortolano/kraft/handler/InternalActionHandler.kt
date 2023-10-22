package com.andretortolano.kraft.handler

class InternalActionHandler : ActionHandler {
    override suspend fun handle(chain: Chain) {
        chain.proceed { keep() }
    }
}
