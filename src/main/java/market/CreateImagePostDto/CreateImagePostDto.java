package market.CreateImagePostDto;
import lombok.Data;

@Data
public class CreateImagePostDto {
    private String imageUrl;
    private String description;
    private Double price;
}