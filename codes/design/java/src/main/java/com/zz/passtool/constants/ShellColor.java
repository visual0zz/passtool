package com.zz.passtool.constants;

public final class ShellColor {
    public final static String clear="\033[0m";
    public final static String hightlight="\033[01m";
    public final static String underline="\033[04m";
    public final static String blinking="\033[05m";
    public final static String reverse="\033[07m";
    public final static String fading="\033[08m";
    public final static String cls="\033[2Jm";
    public final static String hide_cursor="\033[?25l";
    public final static String show_cursor="\033[?25h";

    public final static String black="\033[30m";
    public final static String red="\033[31m";
    public final static String green="\033[32m";
    public final static String yellow="\033[33m";
    public final static String blue="\033[34m";
    public final static String purple="\033[35m";
    public final static String sky ="\033[36m";
    public final static String white="\033[37m";

    public final static String black_background="\033[40m";
    public final static String red_background="\033[41m";
    public final static String green_background="\033[42m";
    public final static String yellow_background="\033[43m";
    public final static String blue_background="\033[44m";
    public final static String purple_background="\033[45m";
    public final static String deep_green_background="\033[46m";
    public final static String white_background="\033[47m";

    public static String getColorCode(String name){
        switch (name){
            case "red":return red+hightlight;
            case "black":return black+hightlight;
            case "green":return green+hightlight;
            case "yellow":return yellow+hightlight;
            case "blue":return blue+hightlight;
            case "purple":return purple+hightlight;
            case "white":return white+hightlight;
            case "sky":return sky +hightlight;
        }
        return "";
    }
}
