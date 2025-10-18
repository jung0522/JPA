package io.goorm.jpa.dto.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PageResponse<T> {

    private final List<T> content;
    private final PageInfo pageInfo;

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                PageInfo.of(page)
        );
    }

    @Getter
    @RequiredArgsConstructor
    public static class PageInfo {
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean first;
        private final boolean last;

        public static PageInfo of(Page<?> page) {
            return new PageInfo(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast()
            );
        }
    }
}
