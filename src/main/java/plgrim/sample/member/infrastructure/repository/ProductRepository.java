package plgrim.sample.member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plgrim.sample.member.domain.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
