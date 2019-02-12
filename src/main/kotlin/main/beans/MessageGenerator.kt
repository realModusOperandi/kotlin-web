package main.beans

import javax.enterprise.context.ApplicationScoped
import kotlin.random.Random

@ApplicationScoped
open class MessageGenerator {
    open fun randomMessage(): Message {
        val result = Random.nextInt(20)
        val message = when (result) {
            in 0..4 -> "low"
            in 5..9 -> "medium"
            in 10..14 -> "high"
            in 15..19 -> "very high"
            else -> "impossible"
        }
        val resultString = "Wow, you got ${result}! That's ${message}!"
        return Message(result, resultString)
    }
}