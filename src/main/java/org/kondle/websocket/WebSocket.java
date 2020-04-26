package org.kondle.websocket;


import org.kondle.websocket.frame.WebSocketFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class WebSocket
{
    Socket s;
    private String webSocketKey;
    private InputStream is;
    private OutputStream os;

    private WebSocketFrame currentFrame;

    String reqLine;           // package-private
    String[][] reqHeaders;    // package-private //TODO: implement HTTP Header Class for this

    public WebSocket(String host, int port) throws IOException
    {
        this.s = new Socket(host,port);
        //TODO: implement HTTP Switching Protocols

    }

    public WebSocket(Socket socket) throws IOException
    {
        this.s = socket;
        //create first frame in creating socket
        nextFrame();
    }

    public void send(byte[] data)
    {

    }

    public int receive(byte[] buffer)
    {
        return 0;
    }

    public void close()
    {

    }

    public InputStream getInputStream() throws IOException
    {
        if (currentFrame.isClosed()) nextFrame();
        return this.currentFrame.getInputStream();
    }
    public void nextFrame() throws IOException
    {
        this.currentFrame = new WebSocketFrame(s.getInputStream());
        boolean[] readedBytesCount = this.currentFrame.getMeta().getReadedBytesCount();
        Arrays.fill(readedBytesCount,false);
        this.currentFrame.getMeta().setReadedBytesCount(readedBytesCount);
    }
}
