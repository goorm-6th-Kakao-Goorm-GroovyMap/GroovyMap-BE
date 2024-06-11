package aespa.groovymap.place.performance.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import lombok.Data;

@Data
public class PerformancePlacePostResponseDto {
    private Long id;
    private String name;
    private Category part;
    private Coordinate coordinate;
    private String region;
    private String address;
    private String phoneNumber;
    private String rentalFee;
    private String capacity;
    private String performanceHours;
    private String description;
}
