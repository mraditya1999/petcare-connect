//package com.petconnect.backend.entity;
//
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.Date;
//
//@Document(collection = "topics")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Topic {
//
//    @Id
//    private String topicID;
//
//    @Indexed
//    private String description;
//
//    @Indexed
//    private String title;
//
//    private String createdBy;
//
//    @Indexed
//    private Date createdAt;
//
//    private Date updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = new Date();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = new Date();
//    }
//}
