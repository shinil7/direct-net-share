package shinil.direct.share.thread;

import java.io.IOException;
import java.net.ServerSocket;

import shinil.direct.share.util.Constants;

/**
 * @author shinilms
 */

public final class StartProxyThread extends Thread {

    private ServerSocket serverSocket;
    public boolean isTrue = true;

    public StartProxyThread() {
        try {
            this.serverSocket = new ServerSocket(Constants.PROXY_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (isTrue) {
            try {
                new ProxyConnectionThread(serverSocket.accept());
            } catch (Exception e) {/*ignore*/}
        }
    }

    public void stopProxy() {
        isTrue = false;
        try {
            serverSocket.close();
            serverSocket = null;
        } catch (Exception e) {/*ignore*/}
    }
}
