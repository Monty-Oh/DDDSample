package plgrim.sample.member.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.domain.model.aggregates.QUser;
import plgrim.sample.member.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 전화번호로 회원정보 조회
     * 고유 데이터
     */
    Optional<User> findByMobileNo(String phoneNumber) {
        QUser qUser = QUser.user;
        List<User> users = jpaQueryFactory.selectFrom(qUser)
                .where(qUser.mobileNo.eq(phoneNumber))
                .fetch();

        checkIsSingleData(users, ErrorCode.EXIST_DUPLICATE_MOBILE_NO_DB);

        return Optional.of(users.get(0));
    }

    /**
     * 로그인 ID 로 회원정보 조회
     * 고유 데이터
     */

    public List<User> findByEmail(final String email) {
        QUser qUser = QUser.user;
        return jpaQueryFactory.selectFrom(qUser)
                .where(qUser.email.eq(email))
                .fetch();
    }

    //  중복된 데이터가 존재하면 안될 때 에러
    private void checkIsSingleData(List<?> data, ErrorCode errorCode) {
        if (data.size() > 1) throw new UserException(errorCode);
    }
}
