package shinil.direct_share

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.widget.CompoundButton
import android.widget.TextView
import kotlinx.coroutines.experimental.launch
import shinil.direct.share.DirectNetShare

class MainActivity : AppCompatActivity() {
    private lateinit var infoTv: TextView
    private val rootManager = RootManager()
    private val groupCreatedListener = DirectNetShare.GroupCreatedListener { ssid, password -> infoTv.text = String.format("SSID : %s\nPassword : %s", ssid, password) }
    private lateinit var share: DirectNetShare
    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            checkWifiAndStart()
        } else {
            stopShare()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        infoTv = findViewById(R.id.info)
        (findViewById<SwitchCompat>(R.id.wifi_switch)).setOnCheckedChangeListener(onCheckedChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopShare()
    }

    private fun checkWifiAndStart() {
        if (!Utils.isWifiEnabled(applicationContext)) {
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action != null && intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                        val noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
                        if (!noConnectivity) {
                            startShare()
                            unregisterReceiver(this)
                        }
                    }
                }
            }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            Utils.enableWifi(applicationContext)
        } else {
            startShare()
        }
    }

    private fun startShare() {
        share = DirectNetShare(this@MainActivity, groupCreatedListener)
        share.start()
        launch {
            val success = rootManager.dhcpSetup()
            Log.d("MainActivity", "DHCP setup successful? $success")
        }
    }

    private fun stopShare() {
        share.stop()
        infoTv.text = ""
    }
}
