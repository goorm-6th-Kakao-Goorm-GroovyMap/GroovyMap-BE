package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.MyPagePost;
import aespa.groovymap.domain.post.Post;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPageOnePhotoDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoComment;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoWriteDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotosDto;
import aespa.groovymap.mypage.repository.MyPagePostRepository;
import aespa.groovymap.repository.CommentRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.repository.PostRepository;
import aespa.groovymap.uploadutil.util.FileUpload;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPagePhotoService {

    private final MyPagePostRepository myPagePostRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileUpload fileUpload;

    public MyPagePhotosDto getMyPagePhotos(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Wrong nickname"));
        MemberContent memberContent = member.getMemberContent();

        List<MyPagePost> myPagePosts = memberContent.getMyPagePosts();
        List<MyPagePhotoDto> myPagePhotoDtos = getMyPagePhotoDtos(myPagePosts);

        MyPagePhotosDto myPagePhotosDto = new MyPagePhotosDto();
        myPagePhotosDto.setMyPagePhotoDtos(myPagePhotoDtos);

        return myPagePhotosDto;
    }

    private List<MyPagePhotoDto> getMyPagePhotoDtos(List<MyPagePost> myPagePosts) {
        List<MyPagePhotoDto> myPagePhotoDtos = new ArrayList<>();

        for (MyPagePost myPagePost : myPagePosts) {
            MyPagePhotoDto myPagePhotoDto = new MyPagePhotoDto();

            myPagePhotoDto.setId(myPagePost.getId());
            myPagePhotoDto.setPhotoUrl(myPagePost.getPhotoUrl());

            myPagePhotoDtos.add(myPagePhotoDto);
        }
        return myPagePhotoDtos;
    }

    public void writeMyPagePhoto(MyPagePhotoWriteDto myPagePhotoWriteDto, Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));

        MyPagePost myPagePost = createMyPagePost(myPagePhotoWriteDto, member);
        member.getMemberContent().getMyPagePosts().add(myPagePost);

        myPagePostRepository.save(myPagePost);
    }

    private MyPagePost createMyPagePost(MyPagePhotoWriteDto myPagePhotoWriteDto, Member member) throws IOException {
        MyPagePost myPagePost = new MyPagePost();

        myPagePost.setAuthor(member);
        myPagePost.setMyPageMemberContent(member.getMemberContent());
        myPagePost.setLikesCount(0);
        myPagePost.setSavesCount(0);
        myPagePost.setComments(new ArrayList<>());
        myPagePost.setTimestamp(ZonedDateTime.now());
        myPagePost.setTitle("");
        myPagePost.setViewCount(0);
        myPagePost.setContent(myPagePhotoWriteDto.getText());
        myPagePost.setPhotoUrl(fileUpload.saveFile(myPagePhotoWriteDto.getImage()));

        return myPagePost;
    }

    public MyPageOnePhotoDto getMyPagePhoto(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        MyPageOnePhotoDto myPageOnePhotoDto = new MyPageOnePhotoDto();

        if (post instanceof MyPagePost) {
            convertPostToMyPageOntPhotoDto(post, myPageOnePhotoDto);
        } else {
            throw new NoSuchElementException("MyPagePost Id is required, this id is not MyPagePost id");
        }

        return myPageOnePhotoDto;
    }

    private void convertPostToMyPageOntPhotoDto(Post post, MyPageOnePhotoDto myPageOnePhotoDto) {
        myPageOnePhotoDto.setText(post.getContent());
        myPageOnePhotoDto.setImage(((MyPagePost) post).getPhotoUrl());
        myPageOnePhotoDto.setLikes(post.getLikesCount());
        myPageOnePhotoDto.setComments(makeComments(post.getComments()));
    }

    private List<MyPagePhotoComment> makeComments(List<Comment> comments) {
        List<MyPagePhotoComment> myPagePhotoComments = new ArrayList<>();

        for (Comment comment : comments) {
            MyPagePhotoComment myPagePhotoComment = new MyPagePhotoComment();
            convertComment(comment, myPagePhotoComment);
            myPagePhotoComments.add(myPagePhotoComment);
        }

        return myPagePhotoComments;
    }

    private void convertComment(Comment comment, MyPagePhotoComment myPagePhotoComment) {
        Member commentAuthor = comment.getCommentAuthor();

        myPagePhotoComment.setUserNickname(commentAuthor.getNickname());
        myPagePhotoComment.setUserProfileImage(commentAuthor.getMemberContent().getProfileImage());
        myPagePhotoComment.setId(comment.getId());
        myPagePhotoComment.setText(comment.getContent());
    }

    public void deleteMyPagePhoto(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        if (post.getAuthor().getId() == memberId) {
            deleteMyPagePhotoPost(post);
        } else {
            throw new SecurityException("cannot delete other user's post");
        }
    }

    private void deleteMyPagePhotoPost(Post post) {
        if (post instanceof MyPagePost) {
            myPagePostRepository.delete((MyPagePost) post);
        } else {
            throw new NoSuchElementException("Wrong post id, you need to send MyPagePhotoId");
        }
        postRepository.delete(post);
    }

    public void likeMyPagePhoto(Long memberId, Long postId) {

    }

    public void writeComment(Long memberId, Long postId, String text) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        Comment comment = makeComment(text, member, post);
        post.getComments().add(comment);
    }

    private Comment makeComment(String text, Member member, Post post) {
        Comment comment = new Comment();
        comment.setCommentAuthor(member);
        comment.setContent(text);
        comment.setCommentPost(post);

        commentRepository.save(comment);
        return comment;
    }

    public void deleteMyPagePhotoComment(Long memberId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Comment Id"));

        if (comment.getCommentAuthor().getId() == memberId) {
            commentRepository.delete(comment);
        } else {
            throw new SecurityException("cannot delete other user's post");
        }
    }
}
