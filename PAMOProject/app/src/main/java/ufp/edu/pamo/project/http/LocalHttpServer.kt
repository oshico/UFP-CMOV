package ufp.edu.pamo.project.http


import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import ufp.edu.pamo.project.database.ParkingDatabase
import ufp.edu.pamo.project.database.ParkingEvent

class LocalHttpServer(context: Context) : NanoHTTPD(8080) {
    private val db = ParkingDatabase.getInstance(context)

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri

        return try {
            session.parseBody(null)
            val params = session.parms

            when {
                uri == "/sensor/update" && session.method == Method.POST -> {
                    val event = ParkingEvent(
                        status = params["status"] ?: "UNKNOWN",
                        timestamp = System.currentTimeMillis()
                    )

                    Thread {
                        db.parkingEventDao().insert(event)
                    }.start()

                    newFixedLengthResponse(Response.Status.OK, "text/plain", "ok")
                }

                uri == "/sensor/history" && session.method == Method.GET -> {
                    val events = db.parkingEventDao().getAll()
                    val sb = StringBuilder()
                    if (events != null) {
                        for (e in events) {
                            sb.append("${e?.status},${e?.timestamp}\n")
                        }
                    }
                    newFixedLengthResponse(sb.toString())
                }

                uri == "/actuate/reset" && session.method == Method.POST -> {
                    newFixedLengthResponse("ACTUATION SENT")
                }

                else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "NOT FOUND")
            }
        } catch (e: Exception) {
            newFixedLengthResponse("ERROR: ${e.message}")
        }
    }

    fun startServer() {
        start(SOCKET_READ_TIMEOUT, false)
    }
}