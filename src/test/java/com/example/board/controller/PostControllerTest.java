package com.example.board.controller;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.post.PostService;
import com.example.board.request.PostSaveRequest;
import com.example.board.response.PostResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

//    @Autowired
//    private WebApplicationContext wac;

//    @BeforeEach
//    void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//    }

    @Test
    @DisplayName("/post 를 post로 요청 시 게시글을 저장한다.")
    void save() throws Exception {
        // given
        PostSaveRequest request = PostSaveRequest.builder()
                .title("제목입니다.")
                .content("내용입니다")
                .writer("작성자입니다")
                .build();

//        when(postService.register(Mockito.any())).thenReturn(Long.valueOf(1));

        // when
        mockMvc.perform(post("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());
        //then
//        assertEquals(1L, postRepository.count());
    }

    @Test
    @DisplayName("/ 를 get으로 요청 시 게시글을 전체 조회한다.")
    void listAll() throws Exception {
        // given
        PostSaveRequest request1 = PostSaveRequest.builder()
                .title("제목입니다.")
                .content("내용입니다")
                .writer("작성자입니다")
                .build();
        PostSaveRequest request2 = PostSaveRequest.builder()
                .title("제목2.")
                .content("내용2")
                .writer("작성자입니다")
                .build();

        postRepository.save(request1.toEntity());
        postRepository.save(request2.toEntity());

        mockMvc.perform(get("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/{id} 를 get으로 요청 시 게시글을 한 개 조회한다.")
    void findById() throws Exception {
        // given
        PostSaveRequest request = PostSaveRequest.builder()
                .title("제목입니다.")
                .content("내용입니다")
                .writer("작성자입니다")
                .build();

        Post post = postRepository.save(request.toEntity());
        PostResponse response = new PostResponse(post);

        mockMvc.perform(get("/api/v1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(response)))
                .andDo(print());
    }

    @Test
    @DisplayName("/{id} 를 patch로 요청 시 게시글을 수정한다.")
    void update() throws Exception {
        // given
        PostSaveRequest request = PostSaveRequest.builder()
                .title("제목입니다.")
                .content("내용입니다")
                .writer("작성자입니다")
                .build();

        postRepository.save(request.toEntity());

        // when
        Map<String, String> input = new HashMap<>();
        input.put("title","변경한제목");
        input.put("content", "변경한내용");
        input.put("writer", "작성자입니다.");

        // then
        mockMvc.perform(patch("/api/v1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 등록 시 제목은 필수다.")
    void verify() throws Exception {
        // given
        PostSaveRequest request = PostSaveRequest.builder()
                .title("")
                .content("내용입니다")
                .writer("작성자입니다")
                .build();

        mockMvc.perform(post("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request))
                )
//                .andExpect(jsonPath("$.title").value("제목을 입력해주세요."))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print());
    }
}