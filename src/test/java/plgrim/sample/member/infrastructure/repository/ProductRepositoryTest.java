package plgrim.sample.member.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import plgrim.sample.common.enums.Color;
import plgrim.sample.member.domain.model.entity.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductRepository 테스트")
@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    Product product;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .name("monty")
                .price(1000L)
                .img("src/src/src")
                .discountRate(100L)
                .colorType(Color.GREEN)
                .build();
    }
    
    @DisplayName("상품 목록 조회")
    @Test
    void findAllProduct() {
        // 테스트용 상품 저장
        productRepository.save(product);
        // 목록 조회
        List<Product> products = productRepository.findAll();

        assertThat(products.size()).isSameAs(1);
    }
}