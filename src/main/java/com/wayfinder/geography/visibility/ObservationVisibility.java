package com.wayfinder.geography.visibility;

import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;

public record ObservationVisibility(
        ObservationSite site,
        Landmark target,
        SightlineResult sightline,
        double recognizability
) {}
