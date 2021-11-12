package com.ssos.learn.io.file;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName: FileIoDemo
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/11/11 10:18
 * @Vsersion: 1.0
 */

public class FileIoDemo {

    private static File file = new File("D:\\data\\123.txt");
    private static String content = "abcd123\r\n";

    public static void main(String[] args) {
//        bufferIo();
        randomAccessIo();
    }

    public static void basicIo() {

        try {
            final OutputStream outputStream = new FileOutputStream(file);
            while (true) {
                outputStream.write(content.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bufferIo() {
        final OutputStream outputStream;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            while (true) {
                outputStream.write(content.getBytes());
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void randomAccessIo() {
        try {
            final RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            accessFile.write(content.getBytes());

            accessFile.seek(2);
            accessFile.write("xxxx".getBytes());
            final FileChannel channel = accessFile.getChannel();
            final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
