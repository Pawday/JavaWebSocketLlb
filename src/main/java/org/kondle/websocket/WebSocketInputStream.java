package org.kondle.websocket;

import org.kondle.websocket.structs.Opcode;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketInputStream extends InputStream
{
    private WebSocket ws;

    public WebSocketInputStream(WebSocket ws)
    {
        this.ws = ws;
    }

    @Override
    public int read() throws IOException
    {
        if (ws.currentFrameSequence.isClosed())
        {
            ws.nextSequence();

            //TODO: check control frames here
            if (ws.currentFrameSequence.getCurrentFrame().getMeta().getOpcode() == Opcode.CLOSE) return -1;
        }
        return ws.currentFrameSequence.getInputStream().read();
    }
}

