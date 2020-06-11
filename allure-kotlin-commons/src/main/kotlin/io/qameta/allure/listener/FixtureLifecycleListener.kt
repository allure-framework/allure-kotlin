package io.qameta.allure.listener

import io.qameta.allure.model.FixtureResult

/**
 * Notifies about Allure test fixtures lifecycle events.
 *
 * @since 2.0
 */
interface FixtureLifecycleListener : LifecycleListener {
    fun beforeFixtureStart(result: FixtureResult) { //do nothing
    }

    fun afterFixtureStart(result: FixtureResult) { //do nothing
    }

    fun beforeFixtureUpdate(result: FixtureResult) { //do nothing
    }

    fun afterFixtureUpdate(result: FixtureResult) { //do nothing
    }

    fun beforeFixtureStop(result: FixtureResult) { //do nothing
    }

    fun afterFixtureStop(result: FixtureResult) { //do nothing
    }
}