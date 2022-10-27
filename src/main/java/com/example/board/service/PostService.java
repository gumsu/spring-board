package com.example.board.service;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostEditor;
import com.example.board.exception.PostNotFound;
import com.example.board.repository.PostRepository;
import com.example.board.request.PostCreateRequest;
import com.example.board.request.PostSearchRequest;
import com.example.board.request.PostUpdateRequest;
import com.example.board.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    // 게시글 등록
    public Long create(PostCreateRequest request) {
        return postRepository.save(request.toEntity()).getId();
    }

    // 게시글 여러 개 조회
    public List<PostResponse> getAll(PostSearchRequest request) {
        return postRepository.getList(request).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    // 게시글 한 개 조회
    public Optional<PostResponse> findOne(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFound::new);
        return Optional.of(new PostResponse(post));
    }

    // 게시글 수정
    public Long update(Long id, PostUpdateRequest request) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFound::new);
        PostEditor.PostEditorBuilder editorBuilder = post.toEditor(); // 기존 데이터
        PostEditor postEditor = editorBuilder // 새로운 데이터
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        post.toEdit(postEditor);
        return post.getId();
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFound::new);
        postRepository.delete(post);
    }
}
