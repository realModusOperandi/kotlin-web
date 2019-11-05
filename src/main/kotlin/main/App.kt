package main

import main.beans.MessageGenerator
import java.io.BufferedWriter
import java.io.IOException
import javax.inject.Inject
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("servlet")
class App : HttpServlet() {
    @Inject
    private lateinit var mg: MessageGenerator

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        doGet(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest?, response: HttpServletResponse?) {
        val result = mg.randomMessage()

        BufferedWriter(response!!.writer).use { it.write(result.message) }
    }
}
