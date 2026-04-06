package com.palodon.server

import com.palodon.shared.Greeting
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

@QuarkusTest
class GreetingResourceTest {

    @Test
    fun testHelloEndpoint() {
        LoggerFactory.getLogger(this::class.java).info("MSG: \"${Greeting().greeting()}\"")
        given()
          .`when`().get("/hello")
          .then()
             .statusCode(200)
             .body(`is`("Hello from Palodon shared-lib!"))
    }
}
