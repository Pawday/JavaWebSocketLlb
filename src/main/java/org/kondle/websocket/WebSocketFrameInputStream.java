package org.kondle.websocket;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrameInputStream extends InputStream
{

    byte maskIndex = 0;

    private final WebSocketFrameMeta meta;
    private final InputStream is;

    public WebSocketFrameInputStream(WebSocketFrameMeta meta, InputStream is)
    {
        this.meta = meta;
        this.is = is;
    }


    @Override
    public int read() throws IOException
    {
        if (maskIndex == 3)
        {
            maskIndex = 0;
            return is.read() ^ meta.mask[3];
        } else
        {
            maskIndex++;
            return ((byte)is.read()) ^ meta.mask[maskIndex - 1];
        }
    }
}
