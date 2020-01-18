package org.kondle.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class WebSocketFrame
{
    private boolean isFin;
    private boolean rsv1;
    private boolean rsv2;
    private boolean rsv3;

    private boolean[] opcode;        // 4 bits

    private boolean isMasked;

    private boolean[] length;        // 7 bits or 16 bits or 64 bits

    private byte[] mask;             // no or 4 bytes

    private byte[][][] data;           // because max size can be bigger than array's max length




    public static WebSocketFrame readWebSocketFrame(InputStream is) throws IOException
    {
        WebSocketFrame retFrame = new WebSocketFrame();

        byte[] buffer;

        // reading first byte (FIN, RSV1, RSV2, RSV3, opcode)
        {
            buffer = new byte[1];
            is.read(buffer);
            retFrame.opcode = new boolean[4];

            retFrame.isFin     = (buffer[0] & 0b00000001) == 0b00000001;
            retFrame.rsv1      = (buffer[0] & 0b00000010) == 0b00000010;
            retFrame.rsv2      = (buffer[0] & 0b00000100) == 0b00000100;
            retFrame.rsv3      = (buffer[0] & 0b00001000) == 0b00001000;
            retFrame.opcode[0] = (buffer[0] & 0b00010000) == 0b00010000;
            retFrame.opcode[1] = (buffer[0] & 0b00100000) == 0b00100000;
            retFrame.opcode[2] = (buffer[0] & 0b01000000) == 0b01000000;
            retFrame.opcode[3] = (buffer[0] & 0b10000000) == 0b10000000;
        }

        // reading maskBit and length bits (7, 16 or 64 bits)
        {
            is.read(buffer);
            retFrame.isMasked  = (buffer[0] & 0b10000000) == 0b10000000; // 8

            short firstSize = (short) (buffer[0] & 0b1111111);

            if (firstSize < 126)
            {
                retFrame.length = new boolean[7];
                retFrame.length[0] = (buffer[0] & 0b00000001) == 0b00000001; // 1
                retFrame.length[1] = (buffer[0] & 0b00000010) == 0b00000010; // 2
                retFrame.length[2] = (buffer[0] & 0b00000100) == 0b00000100; // 3
                retFrame.length[3] = (buffer[0] & 0b00001000) == 0b00001000; // 4
                retFrame.length[4] = (buffer[0] & 0b00010000) == 0b00010000; // 5
                retFrame.length[5] = (buffer[0] & 0b00100000) == 0b00100000; // 6
                retFrame.length[6] = (buffer[0] & 0b01000000) == 0b01000000; // 7
            }
            else
            {
                if (firstSize == 126) buffer = new byte[2];
                if (firstSize == 127) buffer = new byte[8];

                is.read(buffer);
                retFrame.length = new boolean[buffer.length * 8];

                for (int i = 0; i < buffer.length; i++)
                {
                    retFrame.length[0 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00000001) == 0b00000001;
                    retFrame.length[1 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00000010) == 0b00000010;
                    retFrame.length[2 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00000100) == 0b00000100;
                    retFrame.length[3 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00001000) == 0b00001000;
                    retFrame.length[4 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00010000) == 0b00010000;
                    retFrame.length[5 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b00100000) == 0b00100000;
                    retFrame.length[6 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b01000000) == 0b01000000;
                    retFrame.length[7 + 8 * i] = (buffer[buffer.length - 1 - i] & 0b10000000) == 0b10000000;
                }
            }
        }

        // reading mask if need
        if (retFrame.isMasked)
        {
            buffer = new byte[4];
            is.read(buffer);
            retFrame.mask = buffer;
        }

        // reading data (if (isMasked) {decode it})
        {
            if (retFrame.length.length == 7)
            {
                retFrame.data = new byte[1][1][];
                short dataSize7bits = 0;
                for (int i = 0; i < retFrame.length.length; i++)
                    if (retFrame.length[i]) dataSize7bits |= 1 << i;
                retFrame.data[0][0] = new byte[dataSize7bits];
                buffer = new byte[dataSize7bits];
                is.read(buffer);
                if (retFrame.isMasked)
                {
                    for (int i = 0; i < retFrame.data[0][0].length; i++)
                        retFrame.data[0][0][i] = (byte) (buffer[i] ^ retFrame.mask[i % 4]);
                }
                else System.arraycopy(buffer, 0, retFrame.data[0][0], 0, retFrame.data[0].length);
            }
            else if (retFrame.length.length == 16)
            {
                retFrame.data = new byte[1][1][];

                int dataSize16bits = 0;

                for (int i = 0; i < retFrame.length.length; i++)
                    if (retFrame.length[i]) dataSize16bits |= 1 << i;

                retFrame.data[0][0] = new byte[dataSize16bits];
                buffer = new byte[dataSize16bits];
                is.read(buffer);

                if (retFrame.isMasked)
                    for (int i = 0; i < retFrame.data[0][0].length; i++)
                        retFrame.data[0][0][i] = (byte) (buffer[i] ^ retFrame.mask[i % 4]);
                else System.arraycopy(buffer, 0, retFrame.data[0][0], 0, retFrame.data[0].length);
            }
            else if (retFrame.length.length == 64)
            {
                //                         2 147 483 647   : int java max
                //            18 446 744 073 709 551 616‬‬‬‬   : 64 bits max value
                //             9 223 372 036 854 775 808‬‬‬‬‬   : 63 bits max value
                //             4 611 686 014 132 420 609‬‬‬‬‬‬   : max count of elements in 2 dimensional array
                // ‭9 903 520 300 447 984 150 353 281 023‬‬‬‬‬   : max count of elements in 3 dimensional array

                int firstDataLengthSegment = 0;
                int secondDataLengthSegment = 0;
                int thirdDataLengthSegment = 0;

                for (int i = 0; i < 32; i++)
                    if (retFrame.length[i]) firstDataLengthSegment |= (1 << i);
                for (int i = 0; i < 32; i++)
                    if (retFrame.length[i + 32]) secondDataLengthSegment |= (1 << i);
                for (int i = 0; i < 1; i++)
                    if (retFrame.length[i + 62]) thirdDataLengthSegment |= (1 << i);


                if (secondDataLengthSegment == 0 && thirdDataLengthSegment > 0)
                    secondDataLengthSegment = Integer.MAX_VALUE;
                if (firstDataLengthSegment == 0 && secondDataLengthSegment > 0)
                    firstDataLengthSegment = Integer.MAX_VALUE;
                if (secondDataLengthSegment == 0)
                {
                    secondDataLengthSegment = 1;
                    thirdDataLengthSegment = 1;
                }

                retFrame.data = new byte[thirdDataLengthSegment][secondDataLengthSegment][firstDataLengthSegment];

                for (int i = 0; i < retFrame.data.length; i++)
                    for (int j = 0; j < retFrame.data[i].length; j++)
                        for (int k = 0; k < retFrame.data[i][j].length; k++)
                            retFrame.data[i][j][k] = (byte) is.read();

                if (retFrame.isMasked)
                {
                    byte counter = 0;
                    for (int i = 0; i < retFrame.data.length; i++)
                        for (int j = 0; j < retFrame.data[i].length; j++)
                            for (int k = 0; k < retFrame.data[i][j].length; k++)
                            {
                                retFrame.data[i][j][k] ^= retFrame.mask[counter % 4];
                                if (counter == 4) counter = 0;
                                counter++;
                            }
                }
            }
        }

        return retFrame;

    }

    @Override
    public String toString()
    {
        return "WebSocketFrame\n" +
                "{\n" +
                "\tisFin="     + isFin                      + ",\n" +
                "\trsv1="     + rsv1                        + ",\n" +
                "\trsv2="     + rsv2                        + ",\n" +
                "\trsv3="     + rsv3                        + ",\n" +
                "\topcode="   + Arrays.toString(opcode)     + ",\n" +
                "\tisMasked=" + isMasked                    + ",\n" +
                "\tlength="   + Arrays.toString(length)     + ",\n" +
                "\tmask="     + Arrays.toString(mask)       + ",\n" +
                "\tdata="     + Arrays.deepToString(data)   + "\n"  +
                "}";
    }
}
