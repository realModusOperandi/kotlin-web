package main

import java.io.BufferedWriter
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.random.Random

@WebServlet("/")
class App : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        doGet(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest?, response: HttpServletResponse?) {
        val result = Random.nextInt(20)
        val message = when (result) {
            in 0..4 -> "low"
            in 5..9 -> "medium"
            in 10..14 -> "high"
            in 15..19 -> "very high"
            else -> "impossible"
        }
        BufferedWriter(response!!.writer).use { it.write("Wow, you got ${result}! That's ${message}!") }
    }
}
