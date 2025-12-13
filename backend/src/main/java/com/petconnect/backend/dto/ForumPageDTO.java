package com.petconnect.backend.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class ForumPageDTO {
    private List<ForumDTO> content;
    private PageInfo page;

    public ForumPageDTO() {}

    public ForumPageDTO(Page<ForumDTO> forumPage) {
        this.content = forumPage.getContent();
        this.page = new PageInfo(forumPage);
    }

    public List<ForumDTO> getContent() {
        return content;
    }

    public void setContent(List<ForumDTO> content) {
        this.content = content;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }

    public static class PageInfo {
        private int size;
        private int number;
        private int totalPages;
        private long totalElements;

        public PageInfo() {}

        public PageInfo(Page<?> page) {
            this.size = page.getSize();
            this.number = page.getNumber();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }
    }
}
