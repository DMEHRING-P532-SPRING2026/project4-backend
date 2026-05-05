package iu.devinmehringer.project4.manager.engine;

import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LazySubtreeIterator implements Iterator<PlanNode> {

    private final Deque<NodeWithDepth> stack = new ArrayDeque<>();
    private final int depthLimit;

    private record NodeWithDepth(PlanNode node, int depth) {}

    public LazySubtreeIterator(PlanNode root, int depthLimit) {
        this.depthLimit = depthLimit;
        stack.push(new NodeWithDepth(root, 0));
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public PlanNode next() {
        if (stack.isEmpty()) throw new NoSuchElementException();

        NodeWithDepth current = stack.pop();
        PlanNode node = current.node();
        int depth = current.depth();

        if (node instanceof Plan plan && depth < depthLimit) {
            List<PlanNode> children = plan.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(new NodeWithDepth(children.get(i), depth + 1));
            }
        }

        return node;
    }
}