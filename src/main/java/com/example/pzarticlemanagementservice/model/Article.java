package com.example.pzarticlemanagementservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID author;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Topic topic;
    private String title;
    @ElementCollection
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tag_value")
    private List<String> tags;
    private LocalDateTime createdAt;
    private String content;
    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "image_url")
    private List<URL> images;
}
