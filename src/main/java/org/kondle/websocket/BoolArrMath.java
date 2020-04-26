package org.kondle.websocket;

public class BoolArrMath
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

    public static boolean isNull(boolean[] arr)
    {
        boolean isNull = true;
        for (int i = 0; i < arr.length && isNull; i++)
            if (arr[i]) isNull = false;
        return isNull;
    }

    public static boolean isEquals(boolean[] arr1,boolean[] arr2)
    {
        if (arr1.length != arr2.length) throw
                new IllegalArgumentException("Is not possible to compare different length arrays");
        boolean isEquals = true;
        for(int i = 0; i < arr1.length && isEquals; i++)
            isEquals = (arr1[i] != arr2[i]);
        return isEquals;
    }
}
