package com.zz.passtool.shell.cmds;

import com.zz.passtool.service.FileCacheService;
import com.zz.passtool.service.FolderService;
import com.zz.passtool.shell.Command;
import com.zz.passtool.utils.ParamCheckUtil;

import java.io.File;

public class Cat extends Command {
    @Override
    public Object run(String... args) throws Exception {
        ParamCheckUtil.assertTrue(args.length==1,"只能输入一个参数");
        ParamCheckUtil.assertTrue(!(args[0].contains("..")||args[0].contains("~")),"文件路径不能出现.. 和 ~");
        ParamCheckUtil.assertTrue(!args[0].matches("^/.*"),"此处只接受相对路径");
        System.out.println(FileCacheService.read(new File(FolderService.currentFolder,args[0]+".json")));
        return null;
    }
    @Override
    public String shortHelp() {
        return "cat <file> : 显示文件内容";
    }

}
