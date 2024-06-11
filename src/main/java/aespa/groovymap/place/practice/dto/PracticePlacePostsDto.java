package aespa.groovymap.place.practice.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PracticePlacePostsDto {
    private List<PracticePlacePostDto> practicePlacePosts = new ArrayList<>();
}
