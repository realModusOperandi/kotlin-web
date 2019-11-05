package main.beans

import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.ejb.*
import javax.jms.*

@Stateless
open class LocalMessageProducer {

    @Resource(lookup = "jms/localMessageCF")
    open lateinit var cf: QueueConnectionFactory

    @Resource(lookup = "jms/jmsLocalMessageQueue")
    open lateinit var queue: Queue

    open lateinit var conn: QueueConnection
    open lateinit var session: QueueSession

    init {

    }

    @PostConstruct
    open fun initBean() {
        conn = cf.createQueueConnection()
        conn.start()

        session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
        clearQueue(queue)
    }

    open fun clearQueue(queue: Queue): Int {
        var elements = 0

        val consumer = session.createConsumer(queue)
        while (null != consumer.receiveNoWait()) {
            elements++
        }
        consumer.close()

        return elements
    }

    open fun <T : Message> send(message: T, destination: Destination) {
        val producer = session.createProducer(destination)
        producer.send(message)
        producer.close()
    }
}