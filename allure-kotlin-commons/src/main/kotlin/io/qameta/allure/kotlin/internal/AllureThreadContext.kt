package io.qameta.allure.kotlin.internal

import java.util.*

/**
 * Storage that stores information about not finished tests and steps.
 *
 */
class AllureThreadContext {
    private val context = Context()

    /**
     * Returns last (most recent) uuid.
     */
    val current: String?
        get() = context.get().firstOrNull()

    /**
     * Returns first (oldest) uuid.
     */
    val root: String?
        get() = context.get().lastOrNull()

    /**
     * Adds new uuid.
     */
    fun start(uuid: String) {
        context.get().push(uuid)
    }

    /**
     * Removes latest added uuid. Ignores empty context.
     *
     * @return removed uuid.
     */
    fun stop(): String? {
        val uuids: LinkedList<String> = context.get()
        return if (!uuids.isEmpty()) uuids.pop() else null
    }

    /**
     * Removes all the data stored for current thread.
     */
    fun clear() {
        context.remove()
    }

    /**
     * Thread local context that stores information about not finished tests and steps.
     */
    private class Context : InheritableThreadLocal<LinkedList<String>>() {
        public override fun initialValue(): LinkedList<String> = LinkedList()

        override fun childValue(parentStepContext: LinkedList<String>): LinkedList<String> =
            LinkedList(parentStepContext)
    }
}