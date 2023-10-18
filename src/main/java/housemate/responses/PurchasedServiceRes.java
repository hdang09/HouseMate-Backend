package housemate.responses;

import housemate.constants.Enum.GroupType;
import housemate.entities.ServiceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class PurchasedServiceRes {
    private int serviceId;
    private String titleName;
    private GroupType groupType;
    private List<ServiceType> typeList;

    // Check uniqueness of serviceId for Set<PurchasedServiceRes>
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PurchasedServiceRes otherService = (PurchasedServiceRes) obj;
        return serviceId == otherService.serviceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}
