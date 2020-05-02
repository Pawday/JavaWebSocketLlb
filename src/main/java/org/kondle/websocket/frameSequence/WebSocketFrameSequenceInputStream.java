package org.kondle.websocket.frameSequence;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrameSequenceInputStream extends InputStream
{
    private final WebSocketFrameSequence seq;

    public WebSocketFrameSequenceInputStream(WebSocketFrameSequence seq, InputStream is)
    {
        this.seq = seq;
    }

    @Override
    public int read() throws IOException
    {
        if (seq.isClosed && seq.currentFrame.isClosed()) return -1;
        int readed = seq.currentFrame.getInputStream().read();
        if (readed == -1 && !seq.needClose)
        {
            seq.nextFrame();
            readed = seq.currentFrame.getInputStream().read();
        }
        else seq.isClosed = true;
        return readed;
    }
}
