package org.kondle.websocket.frameSequence;

import org.kondle.websocket.frame.WebSocketFrame;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrameSequence
{
    WebSocketFrame currentFrame;
    boolean isClosed = false;
    boolean needClose = false;
    private WebSocketFrameSequenceInputStream is;

    private InputStream baseInputStream;

    public WebSocketFrameSequence(InputStream is) throws IOException
    {
        this.baseInputStream = is;
        //get first frame
        nextFrame();
        this.is = new WebSocketFrameSequenceInputStream(this,this.baseInputStream);
    }

    public WebSocketFrameSequenceInputStream getInputStream()
    {
        return this.is;
    }

    public void nextFrame() throws IOException
    {
        this.currentFrame = new WebSocketFrame(this.baseInputStream);
        if (currentFrame.getMeta().isFin()) needClose = true;
    }

    public boolean isClosed()
    {
        return isClosed;
    }
}
