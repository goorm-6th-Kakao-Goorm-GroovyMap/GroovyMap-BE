package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.MemberContent;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MyPagePerformancePost extends Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Coordinate coordinate;

    @ManyToOne
    @JoinColumn(name = "my_page_performance_member_content_id")
    private MemberContent myPagePerformanceMemberContent;
}
