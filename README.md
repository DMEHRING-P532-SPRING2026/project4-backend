[![CI/CD](https://github.com/DMEHRING-P532-SPRING2026/project4-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/DMEHRING-P532-SPRING2026/project4-backend/actions/workflows/ci.yml)

## Running
```bash
docker build -t planner:latest .
docker run -p 8080:8080 -v ${PWD}/data:/app/data planner:latest
```
**Live:** [https://project4-latest.onrender.com/](https://project4-backend-latest.onrender.com)

---

## Design Patterns

### State
**Files:** `ActionStateMachine`, `ActionState`, `ActionContext`, `ProposedState`, `PendingApprovalState`, `SuspendedState`, `InProgressState`, `CompletedState`, `ReopenedState`, `AbandonedState`
**Reasoning:** The state pattern allows us to easily change and add new states to a state machine.

### Composite
**Files:** `PlanNode`, `Plan`, `ProposedAction`, `DepthFirstPlanIterator`
**Reasoning:** The composite pattern makes it easy to iterate through our plan.

### Template Method
**Files:** `AbstractLedgerEntryGenerator`, `ConsumableLedgerEntryGenerator`, `AssetLedgerEntryGenerator`
**Reasoning:** The template pattern allows us to easily add new types of ledgers such as our consumable or asset by overridding what changes between them.

### Iterator
**Files:** `DepthFirstPlanIterator`, `FilteredPlanIterator`, `LazySubtreeIterator`
**Reasoning:** The Composite works in hand with our Iterator pattern to iterate though complex plans and even apply differnt methods of iteration.
