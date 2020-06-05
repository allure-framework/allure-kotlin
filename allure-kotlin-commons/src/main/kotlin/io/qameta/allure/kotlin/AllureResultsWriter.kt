package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import java.io.InputStream

interface AllureResultsWriter {
    /**
     * Writes Allure test result bean.
     *
     * @param testResult the given bean to write.
     * @throws AllureResultsWriteException if some error occurs
     * during operation.
     */
    fun write(testResult: TestResult)

    /**
     * Writes Allure test result container bean.
     *
     * @param testResultContainer the given bean to write.
     * @throws AllureResultsWriteException if some error occurs
     * during operation.
     */
    fun write(testResultContainer: TestResultContainer)

    /**
     * Writes given attachment. Will close the given stream.
     *
     * @param source     the file name of the attachment. Make sure that file name
     * matches the following glob: <pre>*-attachment*</pre>. The right way
     * to generate attachment is generate UUID, determinate attachment
     * extension and then use it as <pre>{UUID}-attachment.{ext}</pre>
     * @param attachment the steam that contains attachment body.
     */
    fun write(source: String, attachment: InputStream)
}