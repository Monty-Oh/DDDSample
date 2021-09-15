package plgrim.sample.member.domain.model.aggregate;

import lombok.*;
import plgrim.sample.common.enums.Color;

import javax.persistence.*;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "PRDCT_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productNo;

    private String name;
    
    private Long price;
    
    private String img;
    
    // 할인률
    @Column(name = "DSCNT_RATE")
    private Long discountRate;
    
    // 색상 종류
    @Column(name = "CLOR_TYPE")
    private Color colorType;
}
