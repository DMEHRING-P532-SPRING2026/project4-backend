package iu.devinmehringer.project4.manager.engine;

import iu.devinmehringer.project4.model.plan.PlanNode;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FilteredPlanIterator implements Iterator<PlanNode> {

    private final DepthFirstPlanIterator inner;
    private final Predicate<PlanNode> predicate;
    private PlanNode next;

    public FilteredPlanIterator(DepthFirstPlanIterator inner,
                                Predicate<PlanNode> predicate) {
        this.inner = inner;
        this.predicate = predicate;
        advance();
    }

    private void advance() {
        next = null;
        while (inner.hasNext()) {
            PlanNode candidate = inner.next();
            if (predicate.test(candidate)) {
                next = candidate;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public PlanNode next() {
        if (next == null) throw new NoSuchElementException();
        PlanNode result = next;
        advance();
        return result;
    }
}