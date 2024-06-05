package aespa.groovymap.place.performance.dto;

import aespa.groovymap.domain.post.PerformancePlacePost;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PerformancePlacePostsDto {

    private List<PerformancePlacePost> performancePlacePosts = new ArrayList<>();
}
