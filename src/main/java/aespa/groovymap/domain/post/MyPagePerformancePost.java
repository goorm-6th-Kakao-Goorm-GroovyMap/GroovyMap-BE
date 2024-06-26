package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.Type;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MyPagePerformancePost extends Post {
    @Embedded
    private Coordinate coordinate;

    @Embedded
    private Category category;

    @Embedded
    private Type type;

    private String address;
    private String region;
    private String date;

    @ManyToOne
    @JoinColumn(name = "my_page_performance_member_content_id")
    private MemberContent myPagePerformanceMemberContent;
}
