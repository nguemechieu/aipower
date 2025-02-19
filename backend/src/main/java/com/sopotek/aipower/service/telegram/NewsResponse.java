package com.sopotek.aipower.service.telegram;

import com.sopotek.aipower.domain.News;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewsResponse {
    private List<News> articles;

}
