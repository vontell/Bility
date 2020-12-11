package org.vontech.bility.server

import io.kotlintest.specs.FeatureSpec
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*


class AppTest: FeatureSpec({

    feature("the webserver") {
        scenario("should have container info") {
            withTestApplication(Application::main) {
                handleRequest(HttpMethod.Get, "/information").apply {
                    assertEquals(200, response.status()?.value)
                    assertEquals(
                            """
                                |{
                                |  "version": "0.0.1"
                                |}
                            """.trimMargin(),
                            response.content
                    )
                }
            }
        }
    }

})