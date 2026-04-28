package iu.devinmehringer.project4;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.manager.ConsumableLedgerEntryGenerator;
import iu.devinmehringer.project4.manager.engine.DepthFirstPlanIterator;
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

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(rt);
        alloc.setQuantity(BigDecimal.ONE);
        alloc.setKind(ResourceAllocationKind.GENERAL);
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
    void implement_fromProposed_transitionsToInProgress() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.PROPOSED);
        ProposedState state = new ProposedState();

        // Act
        state.implement(ctx(action));

        // Assert
        assertEquals(ActionStateEnum.IN_PROGRESS, action.getState());
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
        assertEquals("waiting for supplies", action.getSuspensions().get(0).getReason());
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
    void implement_fromInProgress_throwsIllegalStateTransition() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        InProgressState state = new InProgressState();

        // Act / Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx(action)));
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
        ProposedAction a1 = actionWithState(ActionStateEnum.COMPLETED);
        ProposedAction a2 = actionWithState(ActionStateEnum.COMPLETED);
        plan.addChild(a1);
        plan.addChild(a2);

        // Act / Assert
        assertEquals(ActionStateEnum.COMPLETED, plan.getStatus());
    }

    @Test
    void planStatus_anyChildInProgress_returnsInProgress() {
        // Arrange
        Plan plan = new Plan();
        ProposedAction a1 = actionWithState(ActionStateEnum.COMPLETED);
        ProposedAction a2 = actionWithState(ActionStateEnum.IN_PROGRESS);
        plan.addChild(a1);
        plan.addChild(a2);

        // Act / Assert
        assertEquals(ActionStateEnum.IN_PROGRESS, plan.getStatus());
    }

    @Test
    void planStatus_anyChildSuspendedNoneInProgress_returnsSuspended() {
        // Arrange
        Plan plan = new Plan();
        ProposedAction a1 = actionWithState(ActionStateEnum.PROPOSED);
        ProposedAction a2 = actionWithState(ActionStateEnum.SUSPENDED);
        plan.addChild(a1);
        plan.addChild(a2);

        // Act / Assert
        assertEquals(ActionStateEnum.SUSPENDED, plan.getStatus());
    }

    @Test
    void planStatus_allChildrenAbandoned_returnsAbandoned() {
        // Arrange
        Plan plan = new Plan();
        ProposedAction a1 = actionWithState(ActionStateEnum.ABANDONED);
        ProposedAction a2 = actionWithState(ActionStateEnum.ABANDONED);
        plan.addChild(a1);
        plan.addChild(a2);

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
        while (iterator.hasNext()) {
            order.add(iterator.next().getName());
        }

        // Assert
        assertEquals(List.of("Root", "SubPlan", "Child1", "Child2", "Sibling"), order);
    }

    @Test
    void iterator_singleNode_returnsJustThatNode() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setName("Solo");

        // Act
        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(action);
        List<String> order = new ArrayList<>();
        while (iterator.hasNext()) {
            order.add(iterator.next().getName());
        }

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
        while (iterator.hasNext()) {
            order.add(iterator.next().getName());
        }

        // Assert
        assertEquals(List.of("Empty"), order);
    }

    @Test
    void consumableGenerator_selectsOnlyConsumableAllocations() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        ResourceAllocation consumable = consumableAllocation(BigDecimal.TEN);
        ResourceAllocation asset = assetAllocation();
        action.getAllocations().add(consumable);
        action.getAllocations().add(asset);

        ImplementedAction impl = implementedAction(action);

        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null);

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNotNull(tx);
        assertEquals(2, tx.getEntries().size()); // one withdrawal + one deposit
    }

    @Test
    void consumableGenerator_noConsumableAllocations_returnsNull() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        ResourceAllocation asset = assetAllocation();
        action.getAllocations().add(asset);

        ImplementedAction impl = implementedAction(action);

        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null);

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNull(tx);
    }

    @Test
    void consumableGenerator_negativeQuantity_throwsIllegalState() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        ResourceAllocation bad = consumableAllocation(BigDecimal.valueOf(-5));
        action.getAllocations().add(bad);

        ImplementedAction impl = implementedAction(action);

        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null);

        // Act / Assert
        assertThrows(IllegalStateException.class,
                () -> generator.generateEntries(impl));
    }

    @Test
    void consumableGenerator_entriesAreBalanced_withdrawalAndDeposit() {
        // Arrange
        ProposedAction action = actionWithState(ActionStateEnum.IN_PROGRESS);
        ResourceAllocation alloc = consumableAllocation(BigDecimal.valueOf(100));
        action.getAllocations().add(alloc);

        ImplementedAction impl = implementedAction(action);

        ConsumableLedgerEntryGenerator generator =
                new ConsumableLedgerEntryGenerator(null);

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNotNull(tx);
        assertTrue(tx.isBalanced());
        assertEquals(2, tx.getEntries().size());
        assertEquals(
                BigDecimal.valueOf(-100),
                tx.getEntries().get(0).getAmount()
        );
        assertEquals(
                BigDecimal.valueOf(100),
                tx.getEntries().get(1).getAmount()
        );
    }
}