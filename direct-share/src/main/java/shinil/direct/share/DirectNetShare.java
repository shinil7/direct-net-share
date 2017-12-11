package shinil.direct.share;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import shinil.direct.share.thread.StartProxyThread;

/**
 * @author shinilms
 */

public final class DirectNetShare {

    private final String TAG = getClass().getSimpleName();

    private Context applicationContext;
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel channel;
    private GroupCreatedListener listener;
    private StartProxyThread proxyThread;

    public DirectNetShare(Context context, GroupCreatedListener listener) {
        this.listener = listener;
        applicationContext = context.getApplicationContext();
        proxyThread = new StartProxyThread();
    }

    public void start() {
        initP2p(applicationContext);
        startP2pGroup();
    }

    public void stop() {
        proxyThread.stopProxy();
        try {
            p2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "P2p removeGroup success");
                }
                @Override
                public void onFailure(int reason) {
                    Log.i(TAG, "P2p removeGroup failed. Reason : "+reason);
                }
            });
        } catch (Exception e) {/*ignore*/}
    }

    private void initP2p(Context context) {
        if (this.p2pManager == null || this.channel == null) {
            this.p2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
            this.channel = this.p2pManager.initialize(applicationContext, context.getMainLooper(),
                    new WifiP2pManager.ChannelListener() {
                        public void onChannelDisconnected() {
                            Log.i(TAG, "P2p channel initialization failed");
                        }
                    });
        }
    }

    private void startP2pGroup() {
        Thread createGroupThread = new Thread(createGroupRunnable);
        createGroupThread.start();
    }

    private Runnable createGroupRunnable = new Runnable() {
        @Override
        public void run() {
            p2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                public void onFailure(int error) {
                    Log.i(TAG, "createGroup failed. Error code : "+error);
                }
                public void onSuccess() {
                    p2pManager.requestGroupInfo(channel, groupInfoListener);
                }
            });
        }
    };

    private WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                if(group.isGroupOwner()) {
                    Log.i(TAG, "group created with ssid = "+group.getNetworkName() +
                            "\n and password = "+group.getPassphrase());
                    if(listener != null)
                        listener.onGroupCreated(group.getNetworkName(), group.getPassphrase());
                    proxyThread.start();
                }
            } else
                p2pManager.requestGroupInfo(channel, groupInfoListener);
        }
    };

    public interface GroupCreatedListener {
        void onGroupCreated(String ssid, String password);
    }
}
