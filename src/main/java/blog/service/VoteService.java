package blog.service;

import java.util.Optional;

import blog.domain.Vote;

/**
 * Vote 服务接口.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年4月9日
 */
public interface VoteService {
    /**
     * 根据id获取 Vote
     *
     * @param id
     * @return
     */
    Optional<Vote> getVoteById(Long id);

    /**
     * 删除Vote
     *
     * @param id
     * @return
     */
    void removeVote(Long id);
}
