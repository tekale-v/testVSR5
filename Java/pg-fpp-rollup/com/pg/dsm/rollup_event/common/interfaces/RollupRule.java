package com.pg.dsm.rollup_event.common.interfaces;

import com.matrixone.apps.domain.util.FrameworkException;

public interface RollupRule {
    boolean isChildrenAllowed();

    boolean isSubstituteAllowed() throws FrameworkException;
}
