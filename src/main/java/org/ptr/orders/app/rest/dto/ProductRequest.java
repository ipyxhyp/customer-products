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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest implements Serializable {

    private Long customerId;
    private String title;
    private Boolean isDeleted;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
