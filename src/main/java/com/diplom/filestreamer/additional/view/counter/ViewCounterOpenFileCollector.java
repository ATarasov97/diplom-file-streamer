package com.diplom.filestreamer.additional.view.counter;

import com.diplom.filestreamer.additional.view.counter.entity.View;
import com.diplom.filestreamer.additional.view.counter.entity.ViewRepository;
import com.diplom.filestreamer.collector.OpenFileCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ViewCounterOpenFileCollector implements OpenFileCollector {

    private final ViewRepository viewRepository;

    @Override
    @Transactional
    public void collect(String id) {
        viewRepository.save(View.builder()
                .fileId(id)
                .build());
    }
}
