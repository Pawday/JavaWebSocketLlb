package org.kondle.websocket.structs;

public enum Opcode
{
    CONTINUATION((byte) 0x0),
    TEXT((byte)0x1),
    BINARY((byte)0x2),
    CLOSE((byte)0x8),
    PING((byte)0x9),
    PONG((byte)0xA),
    NOTDEFINE((byte)0xF);

    private final byte value;

    Opcode(byte value)
    {
        this.value = value;
    }

    public static Opcode getFromBoolArr(boolean[] arr)
    {
        byte val = 0;
        Opcode op = NOTDEFINE;
        if (arr[3]) val += 1 << 0;
        if (arr[2]) val += 1 << 1;
        if (arr[1]) val += 1 << 2;
        if (arr[0]) val += 1 << 3;
        for (int i = 0; i < Opcode.values().length; i++)
            if (val == Opcode.values()[i].value)
                op = Opcode.values()[i];

        return op;
    }
}
