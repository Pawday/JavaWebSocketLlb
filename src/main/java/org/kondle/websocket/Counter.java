package org.kondle.websocket;

public class Counter
{
    public static void incrementBoolArr(boolean[] arr)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (i == 0)
            {
                arr[0] = !arr[0];
                continue;
            }
            if (!arr[i - 1])
            {
                boolean preState = true;
                for (int j = i - 1; j >= 0; j--)
                    preState = preState && !arr[j];
                if (preState) arr[i] = !arr[i];
            } else return;
        }
    }
    public static void decrementBoolArr(boolean[] arr)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (i == 0)
            {
                arr[0] = !arr[0];
                continue;
            }
            if (arr[i - 1])
            {
                boolean preState = true;
                for (int j = i - 1; j >= 0; j--)
                    preState = preState && arr[j];
                if (preState) arr[i] = !arr[i];
            } else return;
        }
    }
}
