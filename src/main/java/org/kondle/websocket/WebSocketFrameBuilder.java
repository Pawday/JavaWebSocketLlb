package org.kondle.websocket;

public class WebSocketFrameBuilder
{
    private boolean isFin = true;
    private boolean rsv1 = false;
    private boolean rsv2 = false;
    private boolean rsv3 = false;

    private boolean[] opcode = new boolean[] {false, false, false, false};

    private boolean isMasked;

    private boolean[] length;

    private byte[] mask;

    private byte[][] dataPart;

    private int insertDataCounter = 0;

    private int dataArrayIncrementValue = 5;

    public WebSocketFrameBuilder setFin(boolean isFin)
    {
        this.isFin = isFin;
        return this;
    }

    public WebSocketFrameBuilder setRsv1(boolean rsv1)
    {
        this.rsv1 = rsv1;
        return this;
    }

    public WebSocketFrameBuilder setRsv2(boolean rsv2)
    {
        this.rsv2 = rsv2;
        return this;
    }

    public WebSocketFrameBuilder setRsv3(boolean rsv3)
    {
        this.rsv3 = rsv3;
        return this;
    }

    public WebSocketFrameBuilder setOpcode(boolean[] opcode)
    {
        this.opcode = opcode;
        return this;
    }

    public WebSocketFrameBuilder setMasked(boolean masked)
    {
        isMasked = masked;
        return this;
    }

    public WebSocketFrameBuilder setMask(byte[] mask)
    {
        this.mask = mask;
        return this;
    }

    public WebSocketFrameBuilder putData(byte[] data)
    {



        if (this.insertDataCounter <= (this.dataPart.length - 1))
            this.dataPart[this.insertDataCounter] = data;
        else
        {
            byte[][] newDataState = new byte[this.dataPart.length + this.dataArrayIncrementValue][];
            System.arraycopy(this.dataPart, 0, newDataState, 0, this.dataPart.length);
            newDataState[this.dataPart.length] = data;
            this.dataPart = newDataState;
        }

        this.insertDataCounter++;
        return this;
    }

    public WebSocketFrameBuilder()
    {
        this.dataPart = new byte[1][];
    }

    public WebSocketFrameBuilder(int dataArrayLength)
    {
        this.dataPart = new byte[dataArrayLength][];
    }

    public WebSocketFrameBuilder(int dataArrayLength,int dataArrayIncrementValue)
    {
        this.insertDataCounter = dataArrayIncrementValue;
        this.dataPart = new byte[dataArrayLength][];
    }

}
