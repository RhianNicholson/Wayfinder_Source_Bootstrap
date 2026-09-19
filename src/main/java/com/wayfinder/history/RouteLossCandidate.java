package com.wayfinder.history;
import com.wayfinder.core.id.NodeId;
public record RouteLossCandidate(NodeId directionNodeId, NodeId destinationNodeId,
 double vulnerability, String reason) {}
