package org.ptr.orders.app.rest.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CustomersTotalResponse {

    private List<CustomerResponse> customersList;
    private Integer currentPage;
    private Long totalItems;
    private Long totalPages;

}
