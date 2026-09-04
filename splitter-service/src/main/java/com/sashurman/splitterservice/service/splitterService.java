package com.sashurman.splitterservice.service;

import com.sashurman.splitterservice.DTO.VideoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class splitterService {
    @Value("${splitter.service}")
    private Long MAX_TASKS;

    public void SplitterProducer(VideoRequest request){

    }
}
