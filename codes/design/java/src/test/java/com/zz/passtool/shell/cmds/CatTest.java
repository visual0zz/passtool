package com.zz.passtool.shell.cmds;

import com.zz.passtool.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CatTest {

    @Test
    void replacePassword() {
        Main.seed="ffff";
        File file=new File("/Users/zhaozhi/IdeaProjects/passtool/codes/design/java/tmp/1234.json");
        Assertions.assertTrue(Cat.replacePassword(file,"<aaaa>").matches("[a-z]{4}"));
        Assertions.assertTrue(Cat.replacePassword(file,"<ABCD>").matches("[A-Z]{4}"));
        Assertions.assertTrue(Cat.replacePassword(file,"<99990>").matches("[0-9]{5}"));
    }
}