package aespa.groovymap.domain;

import aespa.groovymap.domain.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFile implements Comparable<MediaFile> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "linked_post_id")
    private Post linkedPost;

    private String filePath;
    private String fileType;

//    private List<MultipartFile> mediaFiles;

    private String fileName; // 파일명

    private int ord; // 첨부파일 순서

    @Override
    public int compareTo(MediaFile other) {
        return this.ord - other.ord;
    }

    public void changeBoard(Post linkedPost) {
        this.linkedPost = linkedPost;
    }

}
