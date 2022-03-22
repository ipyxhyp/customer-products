package org.ptr.orders.app.rest.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse implements Serializable {

    private Long id;
    private String title;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
