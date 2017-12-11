package shinil.direct_share;

import android.os.Bundle;
import android.widget.TextView;

import shinil.direct.share.DirectNetShare;
import shinil.direct.share.util.Constants;

public class MainActivity extends BaseActivity
        implements DirectNetShare.GroupCreatedListener {

    private DirectNetShare share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void startShare() {
        share = new DirectNetShare(MainActivity.this, this);
        share.start();
    }

    @Override
    public void onGroupCreated(String ssid, String password) {
        ((TextView) findViewById(R.id.text_view_info)).setText("SSID : "+ssid+"\nPassword : "+password);
        ((TextView) findViewById(R.id.text_view_hint)).setText("After connecting, set the proxy settings to" +
                "\nhost : "+ Constants.DEFAULT_GROUP_OWNER_IP+"\nport : "+Constants.PROXY_PORT+"\non the other device.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        share.stop();
    }
}
