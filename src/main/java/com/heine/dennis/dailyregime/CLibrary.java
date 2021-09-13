package com.heine.dennis.dailyregime;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;



public interface CLibrary extends Library {

    CLibrary INSTANCE = (CLibrary)
            Native.load((Platform.isWindows() ? "shellapi" : "h"),
                    CLibrary.class);

    int SHAppBarMessage(long dwMessage, Structure pData);


}