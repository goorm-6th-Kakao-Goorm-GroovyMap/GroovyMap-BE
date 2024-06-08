package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.MediaFile;
import aespa.groovymap.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    private String title;
    private String content;
    private Integer likesCount;
    private Integer savesCount;
    private Integer viewCount;

    @OneToMany(mappedBy = "commentPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    private ZonedDateTime timestamp;

    // 이미지 파일 관련 필드 추가 및 매핑 설정 추가
    // 각 Post는 여러 개의 MediaFile을 가질 수 있습니다. MediaFile에서 linkedPost 필드로 매핑됩니다.
    @OneToMany(mappedBy = "linkedPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<MediaFile> imageSet = new HashSet<>();

    // 이미지 파일 추가 메서드
    public void addImage(String uuid, String fileName, String filePath, String fileType) {
        MediaFile mediaFile = MediaFile.builder()
                .fileName(uuid + "_" + fileName) // 파일 이름 설정
                .filePath(filePath) // 파일 경로 설정
                .fileType(fileType) // 파일 타입 설정
                .linkedPost(this) // 이 Post와 연결 설정
                .ord(imageSet.size()) // 현재 이미지 파일 개수를 이용하여 순서 설정
                .build();
        imageSet.add(mediaFile);
    }

    // 이미지 파일 삭제 메서드
    public void clearImage() {
        imageSet.forEach(mediaFile -> mediaFile.changeBoard(null));
        this.imageSet.clear();
    }


}
