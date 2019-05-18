package com.diplom.filestreamer.additional.fragment;

import com.diplom.filestreamer.additional.fragment.entity.FragmentView;
import com.diplom.filestreamer.additional.fragment.entity.FragmentViewRepository;
import com.diplom.filestreamer.collector.DownloadCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FragmentDownloadCollector implements DownloadCollector {

    private final FragmentViewRepository fragmentViewRepository;

    @Override
    public void collect(String id, long begin, long end) {
        fragmentViewRepository.save(FragmentView.builder()
                .begin(begin)
                .end(end)
                .fileId(id)
                .build());
    }
}
