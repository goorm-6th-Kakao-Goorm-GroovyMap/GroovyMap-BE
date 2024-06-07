package aespa.groovymap.place.performance.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PerformancePlacePostsDto {

    private List<PerformancePlacePostDto> performancePlacePosts = new ArrayList<>();
}
