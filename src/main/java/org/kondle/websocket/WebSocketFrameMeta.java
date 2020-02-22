package org.kondle.websocket;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrameMeta
{
    private boolean isFin;
    private boolean rsv1;
    private boolean rsv2;
    private boolean rsv3;

    private boolean[] opcode;        // 4 bits

    private boolean isMasked;

    boolean[] length;                // 7 bits or 16 bits or 64 bits

    byte[] mask;             // no or 4 bytes


    public static WebSocketFrameMeta getInstanceFromInputStream(InputStream inputStream) throws IOException
    {
        WebSocketFrameMeta retMeta = new WebSocketFrameMeta();

        byte[] buffer;

        // reading first byte (FIN, RSV1, RSV2, RSV3, opcode)
        {
            buffer = new byte[1];
            inputStream.read(buffer);
            retMeta.opcode = new boolean[4];

            retMeta.isFin     = (buffer[0] & 0b00000001) == 0b00000001;
            retMeta.rsv1      = (buffer[0] & 0b00000010) == 0b00000010;
            retMeta.rsv2      = (buffer[0] & 0b00000100) == 0b00000100;
            retMeta.rsv3      = (buffer[0] & 0b00001000) == 0b00001000;
            retMeta.opcode[0] = (buffer[0] & 0b00010000) == 0b00010000;
            retMeta.opcode[1] = (buffer[0] & 0b00100000) == 0b00100000;
            retMeta.opcode[2] = (buffer[0] & 0b01000000) == 0b01000000;
            retMeta.opcode[3] = (buffer[0] & 0b10000000) == 0b10000000;
        }

        // reading maskBit and length bits (7, 16 or 64 bits)
        {
            inputStream.read(buffer);
            retMeta.isMasked  = (buffer[0] & 0b10000000) == 0b10000000; // 8

            short firstSize = (short) (buffer[0] & 0b1111111);

            if (firstSize < 126)
            {
                retMeta.length = new boolean[7];
                retMeta.length[0] = (buffer[0] & 0b00000001) == 0b00000001; // 1
                retMeta.length[1] = (buffer[0] & 0b00000010) == 0b00000010; // 2
                retMeta.length[2] = (buffer[0] & 0b00000100) == 0b00000100; // 3
                retMeta.length[3] = (buffer[0] & 0b00001000) == 0b00001000; // 4
                retMeta.length[4] = (buffer[0] & 0b00010000) == 0b00010000; // 5
                retMeta.length[5] = (buffer[0] & 0b00100000) == 0b00100000; // 6
                retMeta.length[6] = (buffer[0] & 0b01000000) == 0b01000000; // 7
            }
            else
            {
                if (firstSize == 126) buffer = new byte[2];
                if (firstSize == 127) buffer = new byte[8];

                inputStream.read(buffer);
                retMeta.length = new boolean[buffer.length * 8];

                for (int i = 0; i < buffer.length; i++)
                {
                    retMeta.length[0 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00000001) == 0b00000001;
                    retMeta.length[1 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00000010) == 0b00000010;
                    retMeta.length[2 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00000100) == 0b00000100;
                    retMeta.length[3 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00001000) == 0b00001000;
                    retMeta.length[4 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00010000) == 0b00010000;
                    retMeta.length[5 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00100000) == 0b00100000;
                    retMeta.length[6 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b01000000) == 0b01000000;
                    retMeta.length[7 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b10000000) == 0b10000000;
                }
            }
        }

        // reading mask if need
        if (retMeta.isMasked)
        {
            buffer = new byte[4];
            inputStream.read(buffer);
            retMeta.mask = buffer;
        }

        return retMeta;
    }

}
