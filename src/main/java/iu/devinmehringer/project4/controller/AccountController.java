package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.dto.account.AccountResponse;
import iu.devinmehringer.project4.controller.dto.account.EntryResponse;
import iu.devinmehringer.project4.manager.ActionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final ActionManager actionManager;

    public AccountController(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @GetMapping
    public List<AccountResponse> listAccounts() {
        return actionManager.getAccountsWithBalances();
    }

    @GetMapping("/{id}/entries")
    public ResponseEntity<List<EntryResponse>> getEntries(@PathVariable Long id) {
        return ResponseEntity.ok(
                actionManager.getEntriesForAccount(id).stream()
                        .map(EntryResponse::from)
                        .toList());
    }
}