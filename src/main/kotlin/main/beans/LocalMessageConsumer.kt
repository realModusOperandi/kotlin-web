package main.beans

import javax.ejb.ActivationConfigProperty
import javax.ejb.MessageDriven
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage

@MessageDriven(activationConfig = [
    ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Queue")
])
open class LocalMessageConsumer : MessageListener {
    override fun onMessage(message: Message) {
        val textMessage = message as TextMessage;
        try {
            println("Message received: ${textMessage.text}")
        } catch (e: JMSException) {
            println("Error while trying to consume messages: ${e.message}")
        }
    }
}