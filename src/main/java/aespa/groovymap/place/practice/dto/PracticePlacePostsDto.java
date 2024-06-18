package aespa.groovymap.place.practice.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PracticePlacePostsDto {
    private List<PracticePlacePostResponseDto> practicePlacePosts = new ArrayList<>();
}
