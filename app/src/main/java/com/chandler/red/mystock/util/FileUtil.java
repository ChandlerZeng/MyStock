package com.chandler.red.mystock.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

    public static String loadFromSDFile(String fname) {
        fname="/"+fname;
        String result=null;
        FileInputStream fin = null;
        BufferedReader bufferedReader = null;
        try {
            String path = Environment.getExternalStorageDirectory().getPath()+fname;
            Log.i("FileUtil","file path:"+path);
            File f=new File(path);
            fin=new FileInputStream(f);
            bufferedReader = new BufferedReader(new InputStreamReader(fin));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }

            result=stringBuilder.toString();
            Log.i("FileUtil","file result:"+result);
        }catch (IOException e){
            e.printStackTrace();
            Log.e("IOException FileUtil","没有找到指定文件");
        }finally {
            try{
                if(bufferedReader!=null)
                    bufferedReader.close();
                if(fin!=null)
                    fin.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    /*
     * 定义文件保存的方法，写入到文件中，所以是输出流
     * */
    public static void save(String fileName, String content) {
        FileOutputStream fos = null;
        try {

            /* 判断sd的外部设置状态是否可以读写 */
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                File file = new File(Environment.getExternalStorageDirectory(), fileName + ".txt");

                // 先清空内容再写入
                fos = new FileOutputStream(file);

                byte[] buffer = content.getBytes();
                fos.write(buffer);
                fos.close();
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void appendFile(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(Environment.getExternalStorageDirectory()+"/"+fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String stringReplace(String str) {
        //去掉" "号
        if(str!=null)
        str= str.replaceAll("\"", "");
        Log.i("FileUtil","file result after replace:"+str);
        return str ;

    }

}
