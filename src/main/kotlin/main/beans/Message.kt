package main.beans

import javax.json.bind.annotation.JsonbProperty

class Message (
        @JsonbProperty("number") val number: Int,
        @JsonbProperty("message") val message: String
)