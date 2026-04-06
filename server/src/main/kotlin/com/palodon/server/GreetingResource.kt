package com.palodon.server

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import com.palodon.shared.Greeting
import jakarta.enterprise.event.Observes
import io.quarkus.runtime.StartupEvent
import org.jboss.logging.Logger

@Path("/hello")
class GreetingResource {

    private val log = Logger.getLogger(GreetingResource::class.java)

    fun onStart(@Observes ev: StartupEvent) {
        log.info("App started, Greeting message: ${Greeting().greeting()}")
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = Greeting().greeting()
}