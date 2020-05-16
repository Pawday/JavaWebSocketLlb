package org.kondle.websocket.frame;

import org.kondle.websocket.structs.Opcode;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketFrameMeta
{
    private boolean isFin;
    private boolean rsv1;
    private boolean rsv2;
    private boolean rsv3;

    private Opcode opcode;        // 4 bits

    private boolean isMasked;

    /* unsigned */ long length = 0;  // 7 bits or 16 bits or 64 bits
    /* unsigned */ long readedBytesCount = 0;

    byte[] mask;  // no or 4 bytes


    public static WebSocketFrameMeta getInstanceFromInputStream(InputStream inputStream) throws IOException
    {
        WebSocketFrameMeta retMeta = new WebSocketFrameMeta();

        byte[] buffer;

        // reading first byte (FIN, RSV1, RSV2, RSV3, opcode)
        {
            buffer = new byte[1];
            inputStream.read(buffer);
            boolean[] opcodeArr = new boolean[4];

            retMeta.isFin     = (buffer[0] & 0b00000001) == 0b00000001;
            retMeta.rsv1      = (buffer[0] & 0b00000010) == 0b00000010;
            retMeta.rsv2      = (buffer[0] & 0b00000100) == 0b00000100;
            retMeta.rsv3      = (buffer[0] & 0b00001000) == 0b00001000;
            opcodeArr[0]      = (buffer[0] & 0b00010000) == 0b00010000;
            opcodeArr[1]      = (buffer[0] & 0b00100000) == 0b00100000;
            opcodeArr[2]      = (buffer[0] & 0b01000000) == 0b01000000;
            opcodeArr[3]      = (buffer[0] & 0b10000000) == 0b10000000;
            retMeta.opcode = Opcode.getFromBoolArr(opcodeArr);
        }

        // reading maskBit and length bits (7, 16 or 64 bits)
        {
            // sizeof buffer is 1;
            inputStream.read(buffer);
            retMeta.isMasked  = (buffer[0] & 0b10000000) == 0b10000000; // 8

            int a = 0b01000101;

            short firstSize = (short) (buffer[0] & 0b01111111);

            if (firstSize < 0b01111110)  //126
            {
                retMeta.length = 0;
                retMeta.length = buffer[0] & 0b01111111;
                System.out.println("length: " + retMeta.length);
            }
            else
            {
                if (firstSize == 126) buffer = new byte[2];
                if (firstSize == 127) buffer = new byte[8];

                inputStream.read(buffer);

                for (int i = 0; i < buffer.length; i++)
                {
                    retMeta.length &= ((i * 8) << buffer[buffer.length - 1 - i]);
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

    public long getLength()
    {
        return length;
    }

    public long getReadedBytesCount()
    {
        return readedBytesCount;
    }


    public Opcode getOpcode()
    {
        return opcode;
    }

    public boolean isMasked()
    {
        return isMasked;
    }

    public boolean isFin()
    {
        return isFin;
    }

    public boolean isRsv1()
    {
        return rsv1;
    }

    public boolean isRsv2()
    {
        return rsv2;
    }

    public boolean isRsv3()
    {
        return rsv3;
    }
}
