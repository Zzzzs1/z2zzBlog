package blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.domain.Comment;
import blog.repository.CommentRepository;

/**
 * Comment Service接口实现.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年6月6日
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }

}
