package shinil.direct.share.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import shinil.direct.share.util.ParseRequest;

/**
 * @author shinilms
 */

public final class ProxyConnectionThread extends Thread {

    private Socket socket;

    public ProxyConnectionThread(Socket socket) {
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        super.run();
        try {
            InputStream inputStream = this.socket.getInputStream();
            OutputStream outputStream = this.socket.getOutputStream();
            byte[] value = new byte[4096];
            String string = "";
            do {
                int read = inputStream.read(value);
                if (read == -1) {
                    break;
                }
                string = string + new String(value, 0, read);
            } while (inputStream.available() > 0);
            ParseRequest ParseRequest = new ParseRequest(string);
            if (ParseRequest.flag) {
                final OutputStream outputStream2;OutputStream outputStream1;
                if (ParseRequest.requestType.equals("CONNECT")) {
                    try {
                        Socket socket = new Socket(ParseRequest.url, ParseRequest.port);
                        try {
                            outputStream.write((ParseRequest.protocol + " 200 Connection established\r\nProxy-agent: LocalHost/1.0\r\n\r\n").getBytes());
                            outputStream.flush();
                            try {
                                outputStream1 = socket.getOutputStream();
                                final InputStream finalInputStream = inputStream;
                                final OutputStream finalOutputStream = outputStream1;
                                new Thread(this) {
                                    public final void run() {
                                        readWrite(finalInputStream, finalOutputStream);
                                        try {
                                            finalOutputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                try {
                                    inputStream = socket.getInputStream();
                                    readWrite(inputStream, outputStream);
                                    try {
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.socket.close();
                }
                try {
                    Socket socket2 = new Socket(ParseRequest.url, ParseRequest.port);
                    try {
                        outputStream1 = socket2.getOutputStream();
                        outputStream2 = outputStream1;
                        outputStream2.write(string.getBytes());
                        outputStream2.flush();
                        try {
                            InputStream inputStream2 = socket2.getInputStream();
                            readWrite(inputStream2, outputStream);
                            try {
                                inputStream2.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readWrite(InputStream inputStream, OutputStream outputStream) {
        byte[] bArr = new byte[4096];
        while (true) {
            try {
                int read = inputStream.read(bArr);
                if (read != -1) {
                    try {
                        outputStream.write(bArr, 0, read);
                        if (inputStream.available() <= 0) {
                            outputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
