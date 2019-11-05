package main.rest

import main.beans.LocalMessageProducer
import javax.ejb.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/jms/mdb")
@Stateless
open class LocalMdbApi {

    @EJB
    open lateinit var producer: LocalMessageProducer

    @POST
    @Path("/do_message")
    @Produces(MediaType.APPLICATION_JSON)
    open fun doMessage(message: String): List<String> {
        val m = producer.session.createTextMessage(message)
        producer.send(m, producer.queue)
        return listOf("Sent $message")
    }
}