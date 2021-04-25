package com.ssos.learn.io.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName: BioModel
 * @Description: bio 堵塞模型
 * @Author: xwl
 * @Date: 2021/4/23 10:26
 * @Vsersion: 1.0
 */

public class BioModel {

    public static void main(String[] args) {
        byte[] bytes = new byte[200];
        try {
            //传统socket 堵塞模型 在连接期间会堵塞，同样在接受数据的时候也会堵塞
            ServerSocket serverSocket = new ServerSocket(9000);
            while (true) {
                Socket accept = serverSocket.accept(); //堵塞
                InputStream inputStream = accept.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                int len;
                while (-1 != (len = (inputStream.read(bytes, 0, bytes.length)))) {
                    stringBuilder.append(new String(bytes, 0, len));
                }
                System.out.println(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
