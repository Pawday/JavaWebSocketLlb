package org.kondle.websocket.frameSequence;

import org.kondle.websocket.frame.WebSocketFrame;
import org.kondle.websocket.frame.WebSocketFrameInputStream;

import java.io.IOException;
import java.net.Socket;

public class WebSocketFrameSequence
{
    WebSocketFrame currentFrame;
    boolean isClosed = false;
    boolean needClose = false;
    private Socket s;
    private WebSocketFrameSequenceInputStream is;

    public WebSocketFrameSequence(Socket s) throws IOException
    {
        this.s = s;
        nextFrame();
        this.is = new WebSocketFrameSequenceInputStream(this,s.getInputStream());
    }

    public WebSocketFrameInputStream getCurrentFrameInputStream() throws IOException
    {
        return this.currentFrame.getInputStream();
    }

    public WebSocketFrameSequenceInputStream getInputStream()
    {
        return this.is;
    }

    public void nextFrame() throws IOException
    {
        this.currentFrame = new WebSocketFrame(this.s.getInputStream());
        if (currentFrame.getMeta().isFin()) needClose = true;
    }
}
