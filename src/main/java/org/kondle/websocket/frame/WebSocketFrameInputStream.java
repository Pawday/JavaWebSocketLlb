package org.kondle.websocket.frame;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrameInputStream extends InputStream
{

    byte maskIndex = 0;

    private final WebSocketFrame frame;
    private final InputStream is;

    public WebSocketFrameInputStream(WebSocketFrame frame, InputStream is)
    {
        this.frame = frame;
        this.is = is;
    }


    @Override
    public int read() throws IOException
    {
        if ( this.frame.meta.length == this.frame.meta.readedBytesCount)
        {
            this.frame.close();
            return -1;
        }
        this.frame.meta.readedBytesCount++;
        if (this.frame.getMeta().isMasked())
        {
            if (maskIndex == 3)
            {
                maskIndex = 0;
                return ((byte) is.read()) ^ this.frame.meta.mask[3];
            }
            else
            {
                maskIndex++;
                return ((byte) is.read()) ^ this.frame.meta.mask[maskIndex - 1];
            }
        }
        else
            return is.read();
    }
}
