package ufp.edu.pamo.project.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import ufp.edu.pamo.project.http.LocalHttpServer

class ServerService : Service(){
    private var server: LocalHttpServer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        server = LocalHttpServer(this)
        try {
            server?.startServer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
    }

    @Nullable
    override fun onBind(intent: Intent?) = null
}