package com.zz.passtool.shell.cmds;

import com.zz.passtool.Main;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Seed extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        Main.seed=args[0];
        return null;
    }
    public String shortHelp(){
        return "seed <seed> : 设定生成密码时使用的种子";
    }
}
