package com.zz.passtool.shell.cmds;

import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Diff extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        return null;
    }

    @Override
    public String shortHelp() {
        return "diff <file> : 显示某个文件的修改";
    }
}
