package com.wayfinder.discovery;
import java.util.List;
public record DiscoverySequenceAssessment(List<DiscoveryBeat> beats, boolean coherent, String summary) {
 public DiscoverySequenceAssessment { beats = List.copyOf(beats); }
}
