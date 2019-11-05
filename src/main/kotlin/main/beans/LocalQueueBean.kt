package main.beans

import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.ejb.*
import javax.jms.*

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
open class LocalQueueBean {

    @EJB
    open lateinit var localBean: LocalQueueBean

    @Resource(lookup = "jms/localCF")
    open lateinit var cf: ConnectionFactory

    @Resource(lookup = "jms/jmsQueue1")
    open lateinit var queue: Queue

    open lateinit var conn: Connection
    open lateinit var session: Session

    init {

    }

    @PostConstruct
    open fun initBean() {
        conn = cf.createConnection()
        conn.start()

        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)
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


    open fun <T : Message> receive(type: Class<T>, destination: Destination): T {
        val consumer = session.createConsumer(destination)
        val result = type.cast(consumer.receive(5000L))
        consumer.close()
        return result
    }

    open fun <T : Message> sendAndReceive(message: T, type: Class<T>, destination: Destination): T {
        localBean.send(message, destination)
        return localBean.receive(type, destination)
    }
}