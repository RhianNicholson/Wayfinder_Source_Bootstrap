package com.wayfinder.geography.visibility;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;

public interface VisibilityAnalyzer {
    SightlineResult analyze(WorldPosition observer, Landmark target);
}
