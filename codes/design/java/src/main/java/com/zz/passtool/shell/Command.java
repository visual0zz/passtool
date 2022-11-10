package com.zz.passtool.shell;

public abstract class Command {
    /**
     * 执行指令
     * @param args 指令的参数
     */
    abstract public Object run(String...args) throws Exception;

    /**
     * 指令的功能介绍 --简短版本
     * @return 介绍文本
     */
    public String shortHelp(){
       return this.getClass().getSimpleName().toLowerCase();
    }
    /**
     * 指令的功能介绍 --详细版本
     * @return 介绍文本
     */
    public String longHelp(){
        return this.shortHelp();
    }

    /**
     * 指令的别名定义
     * @return 别名
     */
    public String[] alias(){
        return new String[]{};
    }
}
