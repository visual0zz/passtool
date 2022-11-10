package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

public class Ls extends Command {
    @Override
    public Object run(String... args) throws Exception{
        ParamCheckUtil.assertTrue(args.length==0,"ls没有参数");
        System.out.println(FolderService.ls());
        return null;
    }
    @Override
    public String shortHelp() {
        return "ls: 列出文件";
    }
}
