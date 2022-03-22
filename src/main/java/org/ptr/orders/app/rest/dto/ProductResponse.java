package org.ptr.orders.app.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class ProductResponse implements Serializable {

    private Long id;
    private Long customerId;
    private String title;
    private Boolean isDeleted;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
