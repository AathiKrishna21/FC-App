package com.joker.fcapp1.Model;

public class SFlag {
    static int  sflag=0;

    public SFlag() {
    }

    public static int getSflag() {
        return sflag;
    }

    public static void setSflag(int sflag) {
        SFlag.sflag = sflag;
    }
}
