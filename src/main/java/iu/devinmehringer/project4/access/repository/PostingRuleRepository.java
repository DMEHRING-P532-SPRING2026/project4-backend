package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.ledger.PostingRule;
import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostingRuleRepository extends JpaRepository<PostingRule, Long> {
    List<PostingRule> findByTriggerAccount(Account account);
}