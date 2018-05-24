package shinil.direct_share;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * @author shinilms
 */

public final class Utils {

    public static boolean enableWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(true);
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
}
