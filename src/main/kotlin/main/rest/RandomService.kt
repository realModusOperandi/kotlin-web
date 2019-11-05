package main.rest

import main.beans.RandomMessage
import main.beans.MessageGenerator
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("random")
class RandomService {
    @Inject
    private lateinit var mg: MessageGenerator

    @GET
    @Produces("application/json")
    fun getMessage(): RandomMessage {
        return mg.randomMessage()
    }
}