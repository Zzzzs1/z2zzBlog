package blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.domain.Vote;
import blog.repository.VoteRepository;

/**
 * Vote 服务实现.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年6月6日
 */
@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Optional<Vote> getVoteById(Long id) {
        return voteRepository.findById(id);
    }

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }
}
