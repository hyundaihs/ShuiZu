package com.android.kevin.shuizu.utils;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/9/6/006.
 */
public class CharUtil {

    /**
     * 把字符串去空格后转换成byte数组。如"37   5a"转成[0x37][0x5A]
     * @param s
     * @return
     */
    public static byte[] string2bytes(String s){
        String ss = s.replace(" ", "");
        int string_len = ss.length();
        int len = string_len/2;
        if(string_len%2 ==1){
            ss = "0"+ss;
            string_len++;
            len++;
        }
        byte[] a = new byte[len];
        for(int i=0;i<len;i++){
            a[i] = (byte)Integer.parseInt(ss.substring(2*i,2*i+2), 16);
        }
        return a;
    }

    /**
     * 16进制数组转化成字符串(大写字母)，比如[0x03][0x3f]转化成"33F"
     * @param b
     * @return
     */
    public static String hex2HexString(byte[] b) {
        int len = b.length;
        int[] x = new int[len];
        String[] y = new String[len];
        StringBuilder str = new StringBuilder();
        // 转换成Int数组,然后转换成String数组
        for (int j = 0; j < len; j++) {
            x[j] = b[j] & 0xff;
            y[j] = Integer.toHexString(x[j]);
            while (y[j].length() < 2) {
                y[j] = "0" + y[j];
            }
            str.append(y[j]);
        }
        //如果是以"0"开头，则弃掉"0"
        while(str.indexOf("0")==0){
            str = str.delete(0, 1);
        }
        return new String(str).toUpperCase();//toUpperCase()方法  转化成大写
    }

    /**
     * 16进制数组转化成调试用字符串(大写字母)，比如[0x03][0x3f]转化成"03 3F"
     * @param b
     * @return
     */
    public static String hex2DebugHexString(byte[] b) {
        int len = b.length;
        int[] x = new int[len];
        String[] y = new String[len];
        StringBuilder str = new StringBuilder();
        // 转换成Int数组,然后转换成String数组
        int j = 0;
        for (; j < len; j++) {
            x[j] = b[j] & 0xff;
            y[j] = Integer.toHexString(x[j]);
            while (y[j].length() < 2) {
                y[j] = "0" + y[j];
            }
            str.append(y[j]);
            str.append(" ");
        }
        return new String(str).toUpperCase();//toUpperCase()方法  转化成大写
    }
}
