package com.sashurman.splitterservice.task;

import com.sashurman.splitterservice.DTO.VideoRequest;
import com.sashurman.splitterservice.enums.ProcessStatus;

import java.util.concurrent.Callable;

public class VideoProcessingTask implements Callable<ProcessStatus> {
    //s3client
    private final VideoRequest request;
    public VideoProcessingTask(VideoRequest request){
        this.request = request;
    }
    @Override
    public ProcessStatus call(){
        //s3.download

        //new ffmpegProducer
        //ffmpeg.split()
        //s3.uploud()
        //uploadService проверит что лежит в "папке" обработанных чанков запишет всё в бд и вернёт пользователю
        return ProcessStatus.COMPLETE;
    }
}
