package iu.devinmehringer.project4;

import iu.devinmehringer.project4.access.AuditLogAccess;
import iu.devinmehringer.project4.access.ResourceAllocationAccess;
import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.manager.AssetLedgerEntryGenerator;
import iu.devinmehringer.project4.manager.ConsumableLedgerEntryGenerator;
import iu.devinmehringer.project4.manager.engine.*;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;
import iu.devinmehringer.project4.model.ledger.Transaction;
import iu.devinmehringer.project4.model.plan.*;
import iu.devinmehringer.project4.model.resource.*;
import iu.devinmehringer.project4.statemachine.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectTests {

    private ProposedAction actionWithState(ActionStateEnum state) {
        ProposedAction action = new ProposedAction();
        action.setState(state);
        return action;
    }

    private ActionContext ctx(ProposedAction action) {
        return new ActionContext(action, null);
    }

    private ResourceAllocation consumableAllocation(BigDecimal qty) {
        ResourceType rt = new ResourceType();
        rt.setKind(ResourceTypeKind.CONSUMABLE);
        rt.setName("Test Resource");
        rt.setUnitCost(BigDecimal.valueOf(10));

        Account pool = new Account();
        pool.setAccountKind(AccountKind.POOL);
        pool.setName("Test Pool");
        rt.setPoolAccount(pool);

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(rt);
        alloc.setQuantity(qty);
        alloc.setKind(ResourceAllocationKind.GENERAL);
        return alloc;
    }

    private ResourceAllocation assetAllocation() {
        ResourceType rt = new ResourceType();
        rt.setKind(ResourceTypeKind.ASSET);
        rt.setName("Asset Resource");
        rt.setUnitCost(BigDecimal.valueOf(100));

        Account pool = new Account();
        pool.setAccountKind(AccountKind.POOL);
        pool.setName("Asset Pool");
        rt.setPoolAccount(pool);

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(rt);
        alloc.setQuantity(BigDecimal.ONE);
        alloc.setKind(ResourceAllocationKind.GENERAL);
        return alloc;
    }

    private ResourceAllocation specificAssetAllocation(Instant start, Instant end) {
        ResourceType rt = new ResourceType();
        rt.setKind(ResourceTypeKind.ASSET);
        rt.setName("MRI Machine");
        rt.setUnitCost(BigDecimal.valueOf(100));

        Account pool = new Account();
        pool.setAccountKind(AccountKind.POOL);
        pool.setName("MRI Pool");
        rt.setPoolAccount(pool);

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(rt);
        alloc.setQuantity(BigDecimal.ONE);
        alloc.setKind(ResourceAllocationKind.SPECIFIC);
        alloc.setAssetId("MRI-001");
        alloc.setTimePeriodStart(start);
        alloc.setTimePeriodEnd(end);
        return alloc;
    }

    private ImplementedAction implementedAction(ProposedAction proposed) {
        ImplementedAction impl = new ImplementedAction();
        impl.setProposedAction(proposed);
        impl.setActualStart(Instant.now());

        Account usage = new Account();
        usage.setAccountKind(AccountKind.USAGE);
        usage.setName("Usage");
        impl.setUsageAccount(usage);

        return impl;
    }

    @Test
    void submitForApproval_fromProposed_transitionsToPendingApproval() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState state = new ProposedState();

        // Act
        state.submitForApproval(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.PENDING_APPROVAL, action.getState());
    }

    @Test
    void suspend_fromProposed_transitionsToSuspended() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState state = new ProposedState();

        // Act
        state.suspend(ctx(action), "waiting for supplies");

        // Assert
        assertEquals(ActionStateEnum.SUSPENDED, action.getState());
        assertEquals(1, action.getSuspensions().size());
        assertEquals("waiting for supplies",
                action.getSuspensions().get(0).getReason());
    }

    @Test
    void abandon_fromProposed_transitionsToAbandoned() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState state = new ProposedState();

        // Act
        state.abandon(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.ABANDONED, action.getState());
    }

    @Test
    void resume_fromProposed_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState state = new ProposedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.resume(ctx(action)));
    }

    @Test
    void complete_fromProposed_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState state = new ProposedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.complete(ctx(action)));
    }

    @Test
    void approve_fromPendingApproval_transitionsToInProgress() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PENDING_APPROVAL);
        PendingApprovalState state = new PendingApprovalState();

        // Act
        state.approve(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.IN_PROGRESS, action.getState());
    }

    @Test
    void reject_fromPendingApproval_transitionsToProposed() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PENDING_APPROVAL);
        PendingApprovalState state = new PendingApprovalState();

        // Act
        state.reject(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.PROPOSED, action.getState());
    }

    @Test
    void suspend_fromPendingApproval_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PENDING_APPROVAL);
        PendingApprovalState state = new PendingApprovalState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.suspend(ctx(action), "reason"));
    }

    @Test
    void implement_fromPendingApproval_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PENDING_APPROVAL);
        PendingApprovalState state = new PendingApprovalState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx(action)));
    }

    @Test
    void abandon_fromPendingApproval_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PENDING_APPROVAL);
        PendingApprovalState state = new PendingApprovalState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.abandon(ctx(action)));
    }

    @Test
    void resume_fromSuspended_transitionsBackToPreviousState() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState proposedState = new ProposedState();
        proposedState.suspend(ctx(action), "reason");
        action.setState(ActionStateEnum.SUSPENDED);
        SuspendedState state = new SuspendedState();

        // Act
        state.resume(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.PROPOSED, action.getState());
        assertNotNull(action.getSuspensions().get(0).getEndDate());
    }

    @Test
    void abandon_fromSuspended_transitionsToAbandoned() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.SUSPENDED);
        SuspendedState state = new SuspendedState();

        // Act
        state.abandon(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.ABANDONED, action.getState());
    }

    @Test
    void implement_fromSuspended_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.SUSPENDED);
        SuspendedState state = new SuspendedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx(action)));
    }

    @Test
    void suspend_fromSuspended_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.SUSPENDED);
        SuspendedState state = new SuspendedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.suspend(ctx(action), "reason"));
    }

    @Test
    void complete_fromInProgress_transitionsToCompleted() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        InProgressState state = new InProgressState();

        // Act
        state.complete(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.COMPLETED, action.getState());
    }

    @Test
    void suspend_fromInProgress_transitionsToSuspended() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        InProgressState state = new InProgressState();

        // Act
        state.suspend(ctx(action), "equipment failure");

        // Assert
        assertEquals(ActionStateEnum.SUSPENDED, action.getState());
    }

    @Test
    void abandon_fromInProgress_transitionsToAbandoned() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        InProgressState state = new InProgressState();

        // Act
        state.abandon(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.ABANDONED, action.getState());
    }

    @Test
    void reopen_fromCompleted_transitionsToReopened() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.COMPLETED);
        CompletedState state = new CompletedState();

        // Act
        state.reopen(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.REOPENED, action.getState());
    }

    @Test
    void complete_fromCompleted_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.COMPLETED);
        CompletedState state = new CompletedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.complete(ctx(action)));
    }

    @Test
    void abandon_fromCompleted_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.COMPLETED);
        CompletedState state = new CompletedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.abandon(ctx(action)));
    }

    @Test
    void complete_fromReopened_transitionsToCompleted() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.REOPENED);
        ReopenedState state = new ReopenedState();

        // Act
        state.complete(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.COMPLETED, action.getState());
    }

    @Test
    void abandon_fromReopened_transitionsToAbandoned() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.REOPENED);
        ReopenedState state = new ReopenedState();

        // Act
        state.abandon(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.ABANDONED, action.getState());
    }

    @Test
    void implement_fromReopened_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.REOPENED);
        ReopenedState state = new ReopenedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx(action)));
    }

    @Test
    void submitForApproval_fromReopened_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.REOPENED);
        ReopenedState state = new ReopenedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.submitForApproval(ctx(action)));
    }

    @Test
    void implement_fromAbandoned_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.ABANDONED);
        AbandonedState state = new AbandonedState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx(action)));
    }

    @Test
    void planStatus_allChildrenCompleted_returnsCompleted() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));

        // Act / Assert
        assertEquals(ActionStateEnum.COMPLETED, plan.getStatus());
    }

    @Test
    void planStatus_anyChildInProgress_returnsInProgress() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));
        plan.addChild(actionWithState(ActionStateEnum.IN_PROGRESS));

        // Act / Assert
        assertEquals(ActionStateEnum.IN_PROGRESS, plan.getStatus());
    }

    @Test
    void planStatus_anyChildSuspendedNoneInProgress_returnsSuspended() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.PROPOSED));
        plan.addChild(actionWithState(ActionStateEnum.SUSPENDED));

        // Act / Assert
        assertEquals(ActionStateEnum.SUSPENDED, plan.getStatus());
    }

    @Test
    void planStatus_allChildrenAbandoned_returnsAbandoned() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.ABANDONED));
        plan.addChild(actionWithState(ActionStateEnum.ABANDONED));

        // Act / Assert
        assertEquals(ActionStateEnum.ABANDONED, plan.getStatus());
    }

    @Test
    void planStatus_noChildren_returnsProposed() {
        // Arrange
        Plan plan = new Plan();

        // Act / Assert
        assertEquals(ActionStateEnum.PROPOSED, plan.getStatus());
    }

    @Test
    void iterator_depthFirstOrder_visitsChildrenBeforeSiblings() {
        // Arrange
        Plan root = new Plan();
        root.setName("Root");
        Plan subPlan = new Plan();
        subPlan.setName("SubPlan");
        ProposedAction child1 = new ProposedAction();
        child1.setName("Child1");
        ProposedAction child2 = new ProposedAction();
        child2.setName("Child2");
        ProposedAction sibling = new ProposedAction();
        sibling.setName("Sibling");
        subPlan.addChild(child1);
        subPlan.addChild(child2);
        root.addChild(subPlan);
        root.addChild(sibling);

        // Act
        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);
        List<String> order = new ArrayList<>();
        while (iterator.hasNext()) order.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Root", "SubPlan", "Child1", "Child2", "Sibling"),
                order);
    }

    @Test
    void iterator_singleNode_returnsJustThatNode() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setName("Solo");

        // Act
        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(action);
        List<String> order = new ArrayList<>();
        while (iterator.hasNext()) order.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Solo"), order);
    }

    @Test
    void iterator_emptyPlan_returnsOnlyRoot() {
        // Arrange
        Plan plan = new Plan();
        plan.setName("Empty");

        // Act
        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(plan);
        List<String> order = new ArrayList<>();
        while (iterator.hasNext()) order.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Empty"), order);
    }

    @Test
    void filteredIterator_onlyCompletedNodes_skipsOthers() {
        // Arrange
        Plan root = new Plan();
        root.setName("Root");
        ProposedAction completed = actionWithState(ActionStateEnum.COMPLETED);
        completed.setName("Completed Action");
        ProposedAction proposed = actionWithState(ActionStateEnum.PROPOSED);
        proposed.setName("Proposed Action");
        root.addChild(completed);
        root.addChild(proposed);

        // Act
        FilteredPlanIterator iterator = new FilteredPlanIterator(
                new DepthFirstPlanIterator(root),
                node -> node.getStatus() == ActionStateEnum.COMPLETED);
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) names.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Completed Action"), names);
    }

    @Test
    void filteredIterator_noMatchingNodes_returnsEmpty() {
        // Arrange
        Plan root = new Plan();
        ProposedAction proposed = actionWithState(ActionStateEnum.PROPOSED);
        proposed.setName("Proposed");
        root.addChild(proposed);

        // Act
        FilteredPlanIterator iterator = new FilteredPlanIterator(
                new DepthFirstPlanIterator(root),
                node -> node.getStatus() == ActionStateEnum.COMPLETED);

        // Assert
        assertFalse(iterator.hasNext());
    }

    @Test
    void filteredIterator_allMatch_returnsAll() {
        // Arrange
        Plan root = new Plan();
        root.setName("Root");
        ProposedAction a1 = actionWithState(ActionStateEnum.PROPOSED);
        a1.setName("A1");
        ProposedAction a2 = actionWithState(ActionStateEnum.PROPOSED);
        a2.setName("A2");
        root.addChild(a1);
        root.addChild(a2);

        // Act
        FilteredPlanIterator iterator = new FilteredPlanIterator(
                new DepthFirstPlanIterator(root), node -> true);
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) names.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Root", "A1", "A2"), names);
    }

    @Test
    void lazyIterator_depthZero_returnsOnlyRoot() {
        // Arrange
        Plan root = new Plan();
        root.setName("Root");
        Plan subPlan = new Plan();
        subPlan.setName("SubPlan");
        ProposedAction child = new ProposedAction();
        child.setName("Child");
        subPlan.addChild(child);
        root.addChild(subPlan);

        // Act
        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 0);
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) names.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Root"), names);
    }

    @Test
    void lazyIterator_depthOne_yieldsSubPlansNotTheirChildren() {
        // Arrange
        Plan root = new Plan();
        root.setName("Root");
        Plan subPlan = new Plan();
        subPlan.setName("SubPlan");
        ProposedAction child = new ProposedAction();
        child.setName("Child");
        subPlan.addChild(child);
        ProposedAction sibling = new ProposedAction();
        sibling.setName("Sibling");
        root.addChild(subPlan);
        root.addChild(sibling);

        // Act
        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 1);
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) names.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Root", "SubPlan", "Sibling"), names);
    }

    @Test
    void lazyIterator_depthTwo_descendsIntoSubPlans() {
        // Arrange
        Plan root = new Plan();
        root.setName("Root");
        Plan subPlan = new Plan();
        subPlan.setName("SubPlan");
        ProposedAction child = new ProposedAction();
        child.setName("Child");
        subPlan.addChild(child);
        root.addChild(subPlan);

        // Act
        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 2);
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) names.add(iterator.next().getName());

        // Assert
        assertEquals(List.of("Root", "SubPlan", "Child"), names);
    }

    @Test
    void consumableGenerator_selectsOnlyConsumableAllocations() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        action.getAllocations().add(consumableAllocation(BigDecimal.TEN));
        action.getAllocations().add(assetAllocation());
        ImplementedAction impl = implementedAction(action);
        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null, null);

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNotNull(tx);
        assertEquals(2, tx.getEntries().size());
    }

    @Test
    void consumableGenerator_noConsumableAllocations_returnsNull() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        action.getAllocations().add(assetAllocation());
        ImplementedAction impl = implementedAction(action);
        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null, null);

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNull(tx);
    }

    @Test
    void consumableGenerator_negativeQuantity_throwsIllegalState() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        action.getAllocations().add(consumableAllocation(BigDecimal.valueOf(-5)));
        ImplementedAction impl = implementedAction(action);
        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null, null);

        // Act / Assert
        assertThrows(IllegalStateException.class,
                () -> generator.generateEntries(impl));
    }

    @Test
    void consumableGenerator_entriesAreBalanced_withdrawalAndDeposit() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        action.getAllocations().add(consumableAllocation(BigDecimal.valueOf(100)));
        ImplementedAction impl = implementedAction(action);
        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null, null);

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNotNull(tx);
        assertTrue(tx.isBalanced());
        assertEquals(2, tx.getEntries().size());
        assertEquals(BigDecimal.valueOf(-100),
                tx.getEntries().get(0).getAmount());
        assertEquals(BigDecimal.valueOf(100),
                tx.getEntries().get(1).getAmount());
    }

    @Test
    void completionRatioVisitor_allCompleted_returnsOne() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(2, visitor.getTotalLeaves());
        assertEquals(2, visitor.getCompletedLeaves());
        assertEquals(1.0, visitor.getRatio());
    }

    @Test
    void completionRatioVisitor_noneCompleted_returnsZero() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.PROPOSED));
        plan.addChild(actionWithState(ActionStateEnum.IN_PROGRESS));
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(2, visitor.getTotalLeaves());
        assertEquals(0, visitor.getCompletedLeaves());
        assertEquals(0.0, visitor.getRatio());
    }

    @Test
    void completionRatioVisitor_emptyPlan_returnsZero() {
        // Arrange
        Plan plan = new Plan();
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(0, visitor.getTotalLeaves());
        assertEquals(0.0, visitor.getRatio());
    }

    @Test
    void resourceCostVisitor_sumsCostAcrossLeaves() {
        // Arrange
        Plan plan = new Plan();
        ProposedAction a1 = actionWithState(ActionStateEnum.PROPOSED);
        a1.getAllocations().add(consumableAllocation(BigDecimal.valueOf(5)));
        ProposedAction a2 = actionWithState(ActionStateEnum.PROPOSED);
        a2.getAllocations().add(consumableAllocation(BigDecimal.valueOf(3)));
        plan.addChild(a1);
        plan.addChild(a2);
        ResourceAllocationAccess mockAccess = mock(ResourceAllocationAccess.class);
        when(mockAccess.findByProposedActionId(any()))
                .thenReturn(a1.getAllocations())
                .thenReturn(a2.getAllocations());
        ResourceCostVisitor visitor = new ResourceCostVisitor(
                mockAccess::findByProposedActionId);

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(BigDecimal.valueOf(80), visitor.getTotalCost());
    }

    @Test
    void resourceCostVisitor_noAllocations_returnsZero() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.PROPOSED));
        ResourceAllocationAccess mockAccess = mock(ResourceAllocationAccess.class);
        when(mockAccess.findByProposedActionId(any())).thenReturn(List.of());
        ResourceCostVisitor visitor = new ResourceCostVisitor(
                mockAccess::findByProposedActionId);

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(BigDecimal.ZERO, visitor.getTotalCost());
    }

    @Test
    void riskScoreVisitor_suspendedAndAbandonedLeaves_countsBoth() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.SUSPENDED));
        plan.addChild(actionWithState(ActionStateEnum.ABANDONED));
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));
        RiskScoreVisitor visitor = new RiskScoreVisitor();

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(2, visitor.getScore());
    }

    @Test
    void riskScoreVisitor_noRiskyLeaves_returnsZero() {
        // Arrange
        Plan plan = new Plan();
        plan.addChild(actionWithState(ActionStateEnum.COMPLETED));
        plan.addChild(actionWithState(ActionStateEnum.IN_PROGRESS));
        RiskScoreVisitor visitor = new RiskScoreVisitor();

        // Act
        plan.accept(visitor);

        // Assert
        assertEquals(0, visitor.getScore());
    }
}