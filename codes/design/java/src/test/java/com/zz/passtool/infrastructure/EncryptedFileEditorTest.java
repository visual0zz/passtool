package com.zz.passtool.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedFileEditorTest {
    @Test
    void test1() {
        List<String> from=new ArrayList<>();
        from.add("山外青山楼外楼");
        from.add("西湖歌舞几时休");
        from.add("桃花潭水深千尺");
        from.add("雕梁画栋");
        EncryptedFileEditor editor=new EncryptedFileEditor("456","123");
        editor.write(new File("tmp/123.json"),from);
        List<String>to=editor.read(new File("tmp/123.json"));
        Assertions.assertEquals(from,to);
    }
    @Test
    void test2() {
        List<String> from=new ArrayList<>();
        from.add("山外青山楼外楼");
        from.add("西湖歌舞几时休");
        from.add("桃花潭水深千尺");
        from.add("雕梁画栋");
        EncryptedFileEditor editor1=new EncryptedFileEditor("123","456");
        editor1.write(new File("tmp/1234.json"),from);

        EncryptedFileEditor editor2=new EncryptedFileEditor("456","fdsafg","789","123");
        List<String>to=editor2.read(new File("tmp/1234.json"));
        Assertions.assertEquals(from,to);
    }
    @Test
    void test3() {
        List<String> from=new ArrayList<>();
        EncryptedFileEditor editor1=new EncryptedFileEditor("123","456");
        editor1.write(new File("tmp/1234.json"),from);

        EncryptedFileEditor editor2=new EncryptedFileEditor("456","fdsafg","789","123");
        List<String>to=editor2.read(new File("tmp/1234.json"));
        Assertions.assertEquals(from,to);
    }
}