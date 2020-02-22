package org.kondle.websocket;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebSocket
{
    Socket s;
    private String webSocketKey;
    private InputStream is;
    private OutputStream os;

    private WebSocketFrameMeta currentFrameMeta;
    private boolean[] readedBytesCount;

    String reqLine;           // package-private
    String[][] reqHeaders;    // package-private

    public WebSocket(String host, int port) throws IOException
    {
        this.s = new Socket(host,port);
    }

    public WebSocket(Socket socket) throws IOException
    {
        this.s = socket;
        this.currentFrameMeta = WebSocketFrameMeta.getInstanceFromInputStream(this.s.getInputStream());
        this.readedBytesCount = new boolean[currentFrameMeta.length.length];
    }

    WebSocket(){}    // package-private

    public void send(byte[] data)
    {

    }

    public void receive(byte[] buffer)
    {

    }

    public void close()
    {

    }
}
