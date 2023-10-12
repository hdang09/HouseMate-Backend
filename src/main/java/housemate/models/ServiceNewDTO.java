package housemate.models;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import housemate.constants.Enum.GroupType;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.UnitOfMeasure;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceNewDTO {

	@Schema(description = "Title name")
	@NotEmpty
	@Size(min = 5, max = 255)
	private String titleName;

	@Positive(message = "Price must be greater than 0")
	@Schema(description = "Original price")
	private int originalPrice;

	@PositiveOrZero(message = "Sale Price must be greater than 0")
	@Schema(description = "Sale price")
	private int salePrice;

	@NotEmpty
	@Schema(description = "The Unit of measure in one of these type: KG, HOUR, TIME, COMBO."
					   + " With package - unit measure default = COMBO")
	private UnitOfMeasure unitOfMeasure;

	@NotBlank(message = "Filling the description of the service")
	@Schema(description = "Description of your service")
	private String description;

	@Schema(description = "Default: AVAILABLE If salePrice Exist -> ONSALE")
	@JsonInclude(value = Include.NON_NULL)
	private SaleStatus saleStatus;

	@Schema(description = "Images Of Service")
	private List<String> images;

	@NotEmpty(message = "The group type must not be empty")
	@Schema(description = "The Group Type in one of these type: "
						+ "CLEANING_SERVICE, RETURN_SERVICE, DELIVERY_SERVICE, OTHER")
	private GroupType groupType;

	@Hidden
	@Schema(description = "Is package: true ? false")
	private boolean isPackage;

	//TODO: FE constraint to pop up only for creating single service
	@Schema(example = "[\r\n" + "\"type 1\",\r\n" + "\"type 2\"\r\n" + "]",
			description = "how many types this service has")
	@JsonInclude(value = Include.NON_NULL)
	Set<String> typeNameList; 
	

	//TODO: FE constraint to pop up only for creating single service
	@Schema(example = "{\r\n" + "\"1\": 0,\r\n" + "\"2\": 0,\r\n" + "\"3\": 0\r\n" + "}",
			description = "Choose single services from single service list and set the quantity")
	@JsonInclude(value = Include.NON_NULL)
	Map<Integer, Integer> serviceChildList; // one for id - one for quantity
	

}
