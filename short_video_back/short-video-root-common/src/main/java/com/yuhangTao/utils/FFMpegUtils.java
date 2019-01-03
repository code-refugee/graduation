package com.yuhangTao.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*使用ffmpeg工具进行操作*/
public class FFMpegUtils {

    private String ffmpegEXE;

    public FFMpegUtils(String ffmpegEXE){
        this.ffmpegEXE=ffmpegEXE;
    }

    /**
     * 对音频和视频进行整合，生成一个带背景音乐的新的MP4视频
     * @param inputVideoPath
     * @param inputBgmPath
     * @param time
     * @param outputPath
     */
    public void doMerge(String inputVideoPath,String inputBgmPath,float time,String outputPath){
        List<String> command=new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(inputVideoPath);
        command.add("-i");
        command.add(inputBgmPath);
        command.add("-t");
        command.add(time+"");
        command.add("-y");
        command.add(outputPath);
        start(command);
    }

    /**
     * 对音频和视频进行整合，生成一个带背景音乐的新的MP4视频,并为视频添加水印
     * @param inputVideoPath
     * @param inputBgmPath
     * @param logoPath
     * @param time
     * @param outputPath
     */
    public void doMergeAndAddLogo(String inputVideoPath,String inputBgmPath,String logoPath,
                                  float time,String outputPath){
//        List<String> command=new ArrayList<>();
//        command.add(ffmpegEXE);
//        command.add("-i");
//        command.add(inputVideoPath);
//        command.add("-i");
//        command.add(inputBgmPath);
//        command.add("-i");
//        command.add(logoPath);
//        command.add("-t");
//        command.add(time+"");
//        command.add("-y");
//        command.add(outputPath);
//        ProcessBuilder builder=new ProcessBuilder(command);
//        releaseResource(builder);
    }

    //对MP3进行截取
    public void cutMusic(String inputPath,String startTime,String toTime,String outputPath){
        List<String> command=new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(inputPath);
        command.add("-ss");
        command.add(startTime);
        command.add("-to");
        command.add(toTime);
        command.add("-y");
        command.add(outputPath);
        start(command);
    }

    //对视频第一秒进行截图，将它作为封面
    /*截图命令 ffmpeg.exe -ss 00:00:01 -y -i inputVideoPath -vframes 1 outCoverPath*/
    public void createCover(String videoPath,String picPath){
        List<String> command=new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-ss");
        command.add("00:00:01");
        command.add("-y");
        command.add("-i");
        command.add(videoPath);
        command.add("-vframes");//这个命令是截取几帧的意思
        command.add("1");
        command.add(picPath);
        start(command);

    }

    private void start(List<String> command) {
        ProcessBuilder builder = new ProcessBuilder(command);
        releaseResource(builder);
    }

    //启动命令并释放资源
    private void releaseResource(ProcessBuilder builder) {
        try {
            Process process=builder.start();
            //得到输入流，并将他释放，不然会占用系统资源
            InputStream inputStream=process.getErrorStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String errMsg="";
            //读取信息就是相当于对资源的释放
            while ((errMsg=bufferedReader.readLine())!=null);
            //关闭流
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(inputStreamReader!=null){
                inputStreamReader.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
