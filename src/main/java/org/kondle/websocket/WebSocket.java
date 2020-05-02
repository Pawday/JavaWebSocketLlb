package org.kondle.websocket;


import org.kondle.websocket.frameSequence.WebSocketFrameSequence;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WebSocket
{
    Socket s;
    private String webSocketKey;

    private OutputStream os;

    WebSocketFrameSequence currentFrameSequence;

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
        //create first sequence in creating socket
        this.currentFrameSequence = new WebSocketFrameSequence(s);
    }

    void nextSequence() throws IOException
    {
        this.currentFrameSequence = new WebSocketFrameSequence(s);
    }


    public WebSocketFrameSequence getCurrentFrameSequence()
    {
        return currentFrameSequence;
    }
}
