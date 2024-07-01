package aespa.groovymap.mypage.service;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.LikedPost;
import aespa.groovymap.domain.post.Post;
import aespa.groovymap.repository.CommentRepository;
import aespa.groovymap.repository.LikedPostRepository;
import aespa.groovymap.repository.MemberRepository;
import aespa.groovymap.repository.PostRepository;
import java.time.ZonedDateTime;
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
public class MyPagePhotoOtherService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikedPostRepository likedPostRepository;

    private Boolean isLikedPost(MemberContent memberContent, Long myPagePostId) {
        return memberContent.getLikedPosts().stream()
                .anyMatch(likedPost -> likedPost.getLikedPost().getId().equals(myPagePostId));
    }

    public void writeMyPagePhotoComment(Long memberId, Long postId, String text) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        Comment comment = makeComment(text, member, post);
        post.getComments().add(comment);
    }

    private Comment makeComment(String text, Member member, Post post) {
        Comment comment = new Comment();
        comment.setCommentAuthor(member);
        comment.setContent(text);
        comment.setCommentPost(post);
        comment.setTimestamp(ZonedDateTime.now());

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

    public Boolean likeMyPagePhoto(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        Boolean isLikedPost = isLikedPost(member.getMemberContent(), postId);

        if (!isLikedPost) {
            likedPostLogic(member, post);
            return true;
        } else {
            return false;
        }
    }

    private void likedPostLogic(Member member, Post post) {
        LikedPost likedPost = makeLikedPost(member, post);
        member.getMemberContent().getLikedPosts().add(likedPost);
        post.increaseLikesCount();

        likedPostRepository.save(likedPost);
    }

    private LikedPost makeLikedPost(Member member, Post post) {
        LikedPost likedPost = new LikedPost();
        likedPost.setLikedMemberContent(member.getMemberContent());
        likedPost.setLikedPost(post);
        return likedPost;
    }


    public Boolean unlikeMyPagePhoto(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Member Id"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Wrong Post Id"));

        MemberContent memberContent = member.getMemberContent();
        List<LikedPost> likedPosts = memberContent.getLikedPosts();

        boolean removed = likedPosts.removeIf(likedPost -> {
            if (likedPost.getLikedPost().getId().equals(postId)) {
                likedPostRepository.delete(likedPost);
                post.decreaseLikesCount();
                return true;
            } else {
                return false;
            }
        });

        return removed;
    }

}
