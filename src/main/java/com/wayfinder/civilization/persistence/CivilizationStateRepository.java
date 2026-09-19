package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.state.CivilizationState;

public interface CivilizationStateRepository {
    CivilizationState load();
    void save(CivilizationState state);
}
