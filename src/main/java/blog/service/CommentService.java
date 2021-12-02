package blog.service;

import java.util.Optional;

import blog.domain.Comment;

/**
 * Comment Service接口.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年6月6日
 */
public interface CommentService {

    /**
     * 根据id获取 Comment
     *
     * @param id
     * @return
     */
    Optional<Comment> getCommentById(Long id);

    /**
     * 删除评论
     *
     * @param id
     * @return
     */
    void removeComment(Long id);
}
