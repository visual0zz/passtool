package com.zz.passtool.shell.cmds;

import com.zz.passtool.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class CatTest {

    static Method rp;
    static {
        try {
            rp=Cat.class.getDeclaredMethod("replacePassword",File.class,String.class);
            rp.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void replacePassword() throws InvocationTargetException, IllegalAccessException {
        Main.seed="ffff";
        File file=new File(Main.DATA_FOLDER+"/1234.json");
        Assertions.assertTrue(((String)rp.invoke(null,file,"<aaaa>")).matches("[a-z]{4}"));
        Assertions.assertTrue(((String)rp.invoke(null,file,"<ABCD>")).matches("[A-Z]{4}"));
        Assertions.assertTrue(((String)rp.invoke(null,file,"<99990>")).matches("[0-9]{5}"));
    }
}