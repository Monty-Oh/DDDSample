package plgrim.sample.member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.service.UserRepository;


public interface UserJPARepository extends JpaRepository<User, Long>, UserRepository {
}
