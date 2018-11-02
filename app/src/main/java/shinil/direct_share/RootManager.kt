package shinil.direct_share

import android.util.Log
import java.util.*

class RootManager {
    suspend fun dhcpSetup(): Boolean {
        var success = true
        success = success && runRootCommand("echo 1 > /proc/sys/net/ipv4/ip_forward")
        success = success && runRootCommand("iptables -F")
        success = success && runRootCommand("iptables -t nat -A POSTROUTING  -j MASQUERADE")
        success = success && runRootCommand("iptables -A FORWARD -j ACCEPT")
        success = success && runRootCommand("iptables -t nat -A PREROUTING  -p udp --dport 53 -j DNAT --to-destination 8.8.8.8:53")
        success = success && runRootCommand("iptables -A FORWARD -p udp -d 8.8.8.8 --dport 53 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT")
        return success
    }

    /**
     * Warning: keep the 'suspend' here.
     * waitFor documentation: Causes the current thread to wait, if necessary, until the process represented by this Process object has terminated.
     */
    private suspend fun runRootCommand(command: String): Boolean {
        val commands = arrayOf("su", "-c", command)
        val start: Long = System.currentTimeMillis()
        val process: Process = Runtime.getRuntime().exec(commands)
        val result = process.waitFor()
        val end: Long = System.currentTimeMillis()
        Log.d("RootManager", "Command ${Arrays.toString(commands)} executed in ${end - start} ms " +
                " with result $result")
        return result == 0
    }
}