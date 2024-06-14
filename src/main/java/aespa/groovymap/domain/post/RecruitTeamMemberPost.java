package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
public class RecruitTeamMemberPost extends Post {
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50) // 필요한 길이로 조정
    private Category category;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Embedded
    private Coordinate coordinate;

    private String region;
    private Integer recruitNum;

    private String status;
}
