package iu.devinmehringer.project4.access.repository;

import iu.devinmehringer.project4.model.resource.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}