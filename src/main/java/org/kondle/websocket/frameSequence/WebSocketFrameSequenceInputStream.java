package org.kondle.websocket.frameSequence;

import org.kondle.websocket.structs.Opcode;

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
        if (seq.isClosed) return -1;
        if (seq.currentFrame.isClosed() && seq.needClose)
        {
            seq.close();
            return -1;
        }
        if (seq.currentFrame.isClosed() && !seq.needClose)
            seq.nextFrame();

        int rBuff = seq.currentFrame.getInputStream().read();

        if (seq.currentFrame.getMeta().getReadedBytesCount() == seq.currentFrame.getMeta().getLength()
                && seq.needClose && !(seq.getCurrentFrame().getMeta().getOpcode() == Opcode.CONTINUATION)) seq.close();

        if (rBuff == -1 && !seq.getCurrentFrame().getMeta().isFin())
        {
            seq.nextFrame();
            rBuff = seq.currentFrame.getInputStream().read();
        }

        return rBuff;
    }
}
