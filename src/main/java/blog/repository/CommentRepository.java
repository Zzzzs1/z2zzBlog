package blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.domain.Comment;

/**
 * Comment Repository 接口.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年6月6日
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
