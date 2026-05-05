package iu.devinmehringer.project4.controller.dto.plan;

import java.math.BigDecimal;

public class PlanMetricsResponse {

    private Long nodeId;
    private String nodeName;
    private String nodeType;

    // CompletionRatioVisitor
    private int totalLeaves;
    private int completedLeaves;
    private double completionRatio;

    // ResourceCostVisitor
    private BigDecimal totalCost;

    // RiskScoreVisitor
    private int riskScore;

    public Long getNodeId() { return nodeId; }

    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    public String getNodeName() { return nodeName; }

    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getNodeType() { return nodeType; }

    public void setNodeType(String nodeType) { this.nodeType = nodeType; }

    public int getTotalLeaves() { return totalLeaves; }

    public void setTotalLeaves(int totalLeaves) { this.totalLeaves = totalLeaves; }

    public int getCompletedLeaves() { return completedLeaves; }

    public void setCompletedLeaves(int completedLeaves) {
        this.completedLeaves = completedLeaves;
    }

    public double getCompletionRatio() { return completionRatio; }

    public void setCompletionRatio(double completionRatio) {
        this.completionRatio = completionRatio;
    }

    public BigDecimal getTotalCost() { return totalCost; }

    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public int getRiskScore() { return riskScore; }

    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
}