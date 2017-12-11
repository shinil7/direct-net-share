package shinil.direct.share.util;

/**
 * @author shinilms
 */

public final class ParseRequest {

    public boolean flag = true;
    public String requestType;
    public String address;
    public String protocol;
    public String url;
    public int port;

    public ParseRequest(String request) {
        String[] split = request.split("\r\n");
        if (split.length > 1) {
            for (int i = 0; i < split.length; i++) {
                String[] split2;
                if (i == 0) {
                    split2 = split[i].split(" ");
                    if (split2.length >= 3) {
                        this.requestType = split2[0];
                        this.address = split2[1];
                        this.protocol = split2[2];
                    } else {
                        this.flag = false;
                    }
                } else {
                    split2 = split[i].split(": ");
                    if (split2[0].equals("Host")) {
                        byte[] bytes = split2[1].getBytes();
                        if (bytes.length > 0) {
                            if (bytes[bytes.length - 1] == (byte) 13) {
                                this.url = new String(bytes, 0, bytes.length - 1);
                            } else {
                                this.url = split2[1];
                            }
                            String[] split3 = this.url.split(":");
                            if (split3.length > 1) {
                                this.url = split3[0];
                                this.port = Integer.parseInt(split3[1]);
                                return;
                            } else if (this.requestType.equals("CONNECT")) {
                                this.port = 443;
                                return;
                            } else {
                                this.port = 80;
                                return;
                            }
                        }
                        this.flag = false;
                        return;
                    }
                }
            }
            return;
        }
        this.flag = false;
    }
}
