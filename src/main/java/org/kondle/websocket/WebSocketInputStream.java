package org.kondle.websocket;

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
            /*
                TODO: check ping frame in other thread if sequence is closed (need to send pong frame)
                 + read spec about ping and pong frames
             */
            ws.nextSequence();
        }
        return ws.currentFrameSequence.getInputStream().read();
    }
}

