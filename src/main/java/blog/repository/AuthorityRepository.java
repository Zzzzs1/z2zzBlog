package blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.domain.Authority;

/**
 * Authority 仓库.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年5月30日
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
