package main.beans

import javax.json.bind.annotation.JsonbProperty

class RandomMessage (
        @JsonbProperty("number") val number: Int,
        @JsonbProperty("message") val message: String
)