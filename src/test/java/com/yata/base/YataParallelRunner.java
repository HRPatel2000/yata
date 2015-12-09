package com.yata.base;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Created by hpatel on 8/31/2015.
 */
public class YataParallelRunner extends BlockJUnit4ClassRunner {

    public YataParallelRunner(Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new YataParallelScheduler());
    }
}