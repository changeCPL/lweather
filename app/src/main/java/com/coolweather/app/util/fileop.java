package com.coolweather.app.util;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by cpl on 12/20/17.
 */

public class fileop {
    String filename;
    public fileop(String path){
        this.filename=path;
    }
    public void write(String content,Context context) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileOutputStream fileOutputStream =context.openFileOutput(filename, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(content);
            bw.close();
            osw.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
