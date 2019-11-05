package main.rest

import main.beans.LocalQueueBean
import javax.ejb.EJB
import javax.ejb.Stateless
import javax.jms.Message
import javax.ws.rs.*

@Path("/jms/local")
@Stateless
open class LocalJmsApi {
    @EJB
    open lateinit var localBean: LocalQueueBean

    @POST
    @Path("/echo")
    @Produces("application/json")
    open fun echoTest(message: String): List<String> {
        val m = localBean.session.createMessage()
        m.setObjectProperty("testKey", message)
        return listOf(localBean.sendAndReceive(m, Message::class.java, localBean.queue).getObjectProperty("testKey") as String)
    }
}