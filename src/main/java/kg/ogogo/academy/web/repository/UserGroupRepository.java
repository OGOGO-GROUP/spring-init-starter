package kg.ogogo.academy.web.repository;

import com.querydsl.core.types.Predicate;
import kg.ogogo.academy.web.domain.security.QUserGroup;
import kg.ogogo.academy.web.domain.security.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, String>,
        QuerydslPredicateExecutor<UserGroup>, QuerydslBinderCustomizer<QUserGroup>
{
    @Override
    default void customize(@NotNull QuerydslBindings bindings, @NotNull QUserGroup qUserGroup){

    }

    @EntityGraph(attributePaths = "privileges")
    List<UserGroup> findByCodeNotNull();

    @Override
    Page<UserGroup> findAll(Predicate predicate, Pageable pageable);
}
