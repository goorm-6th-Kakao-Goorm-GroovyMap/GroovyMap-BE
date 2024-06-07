package aespa.groovymap.place.practice.dto;

import aespa.groovymap.domain.post.PracticePlacePost;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PracticePlacePostsDto {
    private List<PracticePlacePost> practicePlacePosts = new ArrayList<>();
}
