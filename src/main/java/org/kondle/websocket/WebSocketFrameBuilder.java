package org.kondle.websocket;

import static java.lang.System.arraycopy;

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

    private byte[][][] dataArr;
    private int dataArr1dimCounter;
    private int dataArr2dimCounter;
    private int dataArr3dimCounter;

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
        if (this.dataArr3dimCounter == 0)
        {
            this.dataArr[this.dataArr1dimCounter - 1][this.dataArr2dimCounter - 1] = new byte[data.length];
            this.dataArr[this.dataArr1dimCounter - 1][this.dataArr2dimCounter - 1] = data;
            dataArr3dimCounter = data.length;
            return this;
        }

        int GLOBAL_ARRAY_MAX_VALUE = Integer.MAX_VALUE;

        if ( ((long) dataArr3dimCounter + data.length) > GLOBAL_ARRAY_MAX_VALUE)
        {
            int insertionToLastExistingArrayCount = GLOBAL_ARRAY_MAX_VALUE - dataArr3dimCounter;
            int newArrayLength = data.length - insertionToLastExistingArrayCount;

            byte[] newLastExistingArrayState = new byte[GLOBAL_ARRAY_MAX_VALUE];
            byte[] newLastArray = new byte[data.length - insertionToLastExistingArrayCount];

            arraycopy(
                    this.dataArr[dataArr1dimCounter - 1][this.dataArr2dimCounter - 1],
                    0,
                    newLastExistingArrayState,
                    0,
                    this.dataArr[dataArr1dimCounter - 1][this.dataArr2dimCounter - 1].length
                    );
            arraycopy(
                    data,
                    0,
                    newLastExistingArrayState,
                    this.dataArr[dataArr1dimCounter - 1][this.dataArr2dimCounter - 1].length,
                    insertionToLastExistingArrayCount
            );

            arraycopy(
                    data,
                    insertionToLastExistingArrayCount,
                    newLastArray,
                    0,
                    newLastArray.length
            );

            byte[][][] newDataArr;


            newDataArr = new byte[this.dataArr1dimCounter][this.dataArr2dimCounter + 1][];

            for (int i = 0; i < this.dataArr.length; i++)
            {
                for (int j = 0; j < this.dataArr[i].length; j++)
                {
                    newDataArr[i][j] = new byte[this.dataArr[i][j].length];
                    arraycopy(
                            this.dataArr[i][j],
                            0,
                            newDataArr[i][j],
                            0,
                            this.dataArr[i][j].length
                    );
                }
            }

            newDataArr[this.dataArr1dimCounter - 1][this.dataArr2dimCounter - 1] = newLastExistingArrayState;
            newDataArr[this.dataArr1dimCounter - 1][this.dataArr2dimCounter] = newLastArray;
            dataArr2dimCounter++;
            dataArr3dimCounter = newLastArray.length;
            this.dataArr = newDataArr;
        }
        else
        {
            byte[] newLastExistingArrayState = new byte[data.length + dataArr3dimCounter];
            arraycopy(
                    this.dataArr[this.dataArr1dimCounter - 1][this.dataArr2dimCounter - 1],
                    0,
                    newLastExistingArrayState,
                    0,
                    dataArr3dimCounter
                    );
            arraycopy(
                    data,
                    0,
                    newLastExistingArrayState,
                    dataArr3dimCounter,
                    data.length
            );

            this.dataArr[this.dataArr1dimCounter - 1][this.dataArr2dimCounter - 1] = newLastExistingArrayState;
            dataArr3dimCounter += data.length;
        }

        return this;
    }

    public WebSocketFrameBuilder()
    {
        this.dataArr1dimCounter = 1;
        this.dataArr2dimCounter = 1;
        this.dataArr3dimCounter = 0;
        this.dataArr = new byte[1][1][];
    }

    public WebSocketFrame build()
    {
        return new WebSocketFrame(
                this.isFin,
                this.rsv1,
                this.rsv2,
                this.rsv3,
                this.opcode,
                this.isMasked,
                this.mask,
                this.dataArr
                );
    }
}
