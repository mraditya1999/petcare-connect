package com.petconnect.backend.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumPageDTO {
    private List<ForumDTO> content;
    private PageInfo page;

    // Convenience constructor
    public ForumPageDTO(Page<ForumDTO> forumPage) {
        this.content = forumPage.getContent();
        this.page = new PageInfo(forumPage);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int size;
        private int number;
        private int totalPages;
        private long totalElements;

        // Convenience constructor
        public PageInfo(Page<?> page) {
            this.size = page.getSize();
            this.number = page.getNumber();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
        }
    }
}
