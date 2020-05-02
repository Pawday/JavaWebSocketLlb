package org.kondle.websocket.frame;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrame
{
    boolean isClosed = false;
    WebSocketFrameMeta meta;
    private WebSocketFrameInputStream inputStream;

    public WebSocketFrame(InputStream is) throws IOException
    {
        this.meta = WebSocketFrameMeta.getInstanceFromInputStream(is);
        this.inputStream = new WebSocketFrameInputStream(this,is);
    }

    public void close()
    {
        this.isClosed = true;
    }

    public boolean isClosed()
    {
        return isClosed;
    }

    public WebSocketFrameInputStream getInputStream()
    {
        return inputStream;
    }

    public WebSocketFrameMeta getMeta()
    {
        return meta;
    }
}
