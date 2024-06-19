package com.pg.dsm.rollup_event.common.interfaces;

import java.util.Map;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;

public interface RollupEvent {
    void execute();

    void processProductParts(ProductPart masterPart);

    void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap) throws FrameworkException;
}
