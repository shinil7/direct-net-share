package shinil.direct_share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!U.isWifiEnabled(getApplicationContext())) {
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        boolean noConnectivity = intent.getBooleanExtra(
                                ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                        if (!noConnectivity) {
                            startShare();
                            unregisterReceiver(this);
                        }
                    }
                }
            }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            U.enableWifi(getApplicationContext());
        } else
            startShare();
    }

    protected abstract void startShare();
}
