package io.qameta.allure.kotlin.internal

import io.qameta.allure.kotlin.model.FixtureResult
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Internal Allure data storage.
 *
 * @since 2.0
 */
class AllureStorage {
    private val storage: MutableMap<String, Any> = ConcurrentHashMap()
    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    fun getContainer(uuid: String): TestResultContainer? = get<TestResultContainer>(uuid)

    fun getTestResult(uuid: String): TestResult? = get<TestResult>(uuid)

    fun getFixture(uuid: String): FixtureResult? = get<FixtureResult>(uuid)

    fun getStep(uuid: String): StepResult? = get<StepResult>(uuid)

    operator fun <T> get(uuid: String): T? {
        lock.readLock().lock()
        return try {
            storage[uuid] as? T
        } finally {
            lock.readLock().unlock()
        }
    }

    fun <T : Any> put(uuid: String, item: T): T {
        lock.writeLock().lock()
        return try {
            storage[uuid] = item
            item
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun remove(uuid: String) {
        lock.writeLock().lock()
        try {
            storage.remove(uuid)
        } finally {
            lock.writeLock().unlock()
        }
    }
}