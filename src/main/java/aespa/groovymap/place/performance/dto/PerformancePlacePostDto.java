package aespa.groovymap.place.performance.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import lombok.Data;

@Data
public class PerformancePlacePostDto {
    private String name;
    private Category part;
    private Coordinate coordinate;
    private String region;
    private String address;
    private String phoneNumber;
    private String rentalFree;
    private String capacity;
    private String performanceHours;
    private String description;
}
