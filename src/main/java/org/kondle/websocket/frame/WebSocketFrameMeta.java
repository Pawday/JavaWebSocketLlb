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


    public WebSocketFrameMeta (InputStream inputStream) throws IOException
    {

        byte[] buffer;

        // reading first byte (FIN, RSV1, RSV2, RSV3, opcode)
        {
            buffer = new byte[1];
            inputStream.read(buffer);
            boolean[] opcodeArr = new boolean[4];

            this.isFin        = (buffer[0] & 0b10000000) == 0b10000000;
            this.rsv1         = (buffer[0] & 0b01000000) == 0b01000000;
            this.rsv2         = (buffer[0] & 0b00100000) == 0b00100000;
            this.rsv3         = (buffer[0] & 0b00010000) == 0b00010000;
            opcodeArr[0]      = (buffer[0] & 0b00001000) == 0b00001000;
            opcodeArr[1]      = (buffer[0] & 0b00000100) == 0b00000100;
            opcodeArr[2]      = (buffer[0] & 0b00000010) == 0b00000010;
            opcodeArr[3]      = (buffer[0] & 0b00000001) == 0b00000001;
            this.opcode = Opcode.getFromBoolArr(opcodeArr);
        }

        // reading maskBit and length bits (7, 16 or 64 bits)
        {
            // sizeof buffer is 1;
            inputStream.read(buffer);
            this.isMasked  = (buffer[0] & 0b10000000) == 0b10000000; // 8


            short firstSize = (short) (buffer[0] & 0b01111111);

            if (firstSize < 0b01111110)  //126
            {
                this.length = 0;
                this.length = buffer[0] & 0b01111111;
            }
            else
            {
                if (firstSize == 126) buffer = new byte[2];
                if (firstSize == 127) buffer = new byte[8];

                inputStream.read(buffer);

                for (int i = 0; i < buffer.length; i++)
                    this.length |= (((long) buffer[i] & 0xff) << ((buffer.length - 1 - i) * 8));
            }
        }

        // reading mask if need
        if (this.isMasked)
        {
            buffer = new byte[4];
            inputStream.read(buffer);
            this.mask = buffer;
        }
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
