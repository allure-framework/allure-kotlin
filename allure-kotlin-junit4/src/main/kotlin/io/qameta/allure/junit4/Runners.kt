package io.qameta.allure.junit4

import org.junit.runner.notification.*
import org.junit.runners.*

class AllureRunner(clazz: Class<*>) : BlockJUnit4ClassRunner(clazz) {

    override fun run(notifier: RunNotifier) {
        notifier.addListener(AllureJunit4())
        super.run(notifier)
    }
}

class AllureParametrizedRunner(clazz: Class<*>) : Parameterized(clazz) {
    override fun run(notifier: RunNotifier) {
        notifier.addListener(AllureJunit4())
        super.run(notifier)
    }
}
