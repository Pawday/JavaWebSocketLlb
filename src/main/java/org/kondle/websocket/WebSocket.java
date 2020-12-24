package org.kondle.websocket;


import org.kondle.websocket.frameSequence.WebSocketFrameSequence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebSocket
{
    private String webSocketKey;

    private InputStream baseInputStream;
    private OutputStream baseOutputStream;

    private OutputStream os;

    WebSocketFrameSequence currentFrameSequence;

    String reqLine;           // package-private
    String[][] reqHeaders;    // package-private //TODO: implement HTTP Header Class for this


    public WebSocket(InputStream is, OutputStream os) throws IOException {
        this.baseInputStream = is;
        this.baseOutputStream = os;
        //create first sequence in creating socket
        nextSequence();
    }

    void nextSequence() throws IOException
    {
        this.currentFrameSequence = new WebSocketFrameSequence(this.baseInputStream);
    }


    public WebSocketFrameSequence getCurrentFrameSequence()
    {
        return currentFrameSequence;
    }
}
