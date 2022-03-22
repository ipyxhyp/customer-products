package org.ptr.orders.app.model;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "Customers")
public class Customer implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime  modifiedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Product> customerProducts;


}

