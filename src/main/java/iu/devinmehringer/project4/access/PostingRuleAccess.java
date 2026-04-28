package iu.devinmehringer.project4.access;

import iu.devinmehringer.project4.access.repository.PostingRuleRepository;
import iu.devinmehringer.project4.model.ledger.PostingRule;
import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostingRuleAccess {

    private final PostingRuleRepository postingRuleRepository;

    public PostingRuleAccess(PostingRuleRepository postingRuleRepository) {
        this.postingRuleRepository = postingRuleRepository;
    }

    public List<PostingRule> findByTriggerAccount(Account account) {
        return postingRuleRepository.findByTriggerAccount(account);
    }

    public PostingRule save(PostingRule rule) {
        return postingRuleRepository.save(rule);
    }
}