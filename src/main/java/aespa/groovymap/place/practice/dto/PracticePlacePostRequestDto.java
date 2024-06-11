package aespa.groovymap.place.practice.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import lombok.Data;

@Data
public class PracticePlacePostRequestDto {
    private String name;
    private Category part;
    private Coordinate coordinate;
    private String region;
    private String address;
    private String phoneNumber;
    private String rentalFee;
    private String capacity;
    private String practiceHours;
    private String description;
}
