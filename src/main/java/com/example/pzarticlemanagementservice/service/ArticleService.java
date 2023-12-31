package com.example.pzarticlemanagementservice.service;

import com.example.pzarticlemanagementservice.model.Article;
import com.example.pzarticlemanagementservice.repository.ArticleRepository;
import com.example.pzarticlemanagementservice.repository.TopicRepository;
import com.example.pzarticlemanagementservice.web.dto.CommentResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    private TopicRepository topicRepository;
    private UserActivityService userActivityService;
    private ObjectMapper objectMapper;

    private JmsTemplate jmsTemplate;

    public Article save(Article article){
        article.setCreatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    @SneakyThrows
    public void sendToQueue(Article article){
        jmsTemplate.convertAndSend("ArticleMB", objectMapper.writeValueAsString(article));
    }

    public Article update (UUID id, Article newArticle){
        return articleRepository.findById(id)
                .map(article -> {
                    article.setAuthor(newArticle.getAuthor());
                    article.setTitle(newArticle.getTitle());
                    article.setTags(newArticle.getTags());
                    article.setCreatedAt(newArticle.getCreatedAt());
                    article.setContent(newArticle.getContent());
                    article.setImages(newArticle.getImages());
                    if(newArticle.getTopic()==null){
                        article.setTopic(null);
                    } else {
                        article.setTopic(topicRepository.findById(newArticle.getTopic().getId()).orElse(null));
                    }
                    return articleRepository.save(article);
                })
                .orElse(null);
    }

    public void delete (UUID uuid){
        List<CommentResponseDto> comm = userActivityService.getAllComments(uuid);
        comm.forEach(c -> userActivityService.deleteComment(c.getUuid()));
        articleRepository.deleteById(uuid);
    }

    public List<Article> getAll(Specification<Article> spec, int page, int size){
        return articleRepository.findAll(spec, PageRequest.of(page, size)).getContent();
    }

    public List<Article> getAllForUser(long id){
        return articleRepository.findAllByAuthor(id);
    }

    public Article getById(UUID id){
        return articleRepository.findById(id).orElse(null);
    }

}
