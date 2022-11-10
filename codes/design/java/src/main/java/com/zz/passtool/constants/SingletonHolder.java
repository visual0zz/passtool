package com.zz.passtool.constants;

import java.io.File;

public class SingletonHolder {
    public static String PASSTOOL_DATA_ROOT_KEY="PASSTOOL_DATA_ROOT";
    public static File dataRootFolder=new File(System.getenv(PASSTOOL_DATA_ROOT_KEY));
}
