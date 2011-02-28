package com.jbooktrader.strategy;

import com.jbooktrader.indicator.velocity.*;
import com.jbooktrader.platform.indicator.*;
import com.jbooktrader.platform.model.*;
import com.jbooktrader.platform.optimizer.*;
import com.jbooktrader.strategy.base.*;

/**
 *
 */
public class ActiveDefender4 extends StrategyES {

    // Technical indicators
    private Indicator tensionInd;

    // Strategy parameters names
    private static final String FAST_PERIOD = "Fast Period";
    private static final String SLOW_PERIOD = "Slow Period";
    private static final String SCALE_FACTOR = "Scale Factor";
    private static final String ENTRY = "Entry";
    private static final String EXIT = "Exit";


    // Strategy parameters values
    private final int entry, exit;

    public ActiveDefender4(StrategyParams optimizationParams) throws JBookTraderException {
        super(optimizationParams);
        entry = getParam(ENTRY);
        exit = getParam(EXIT);
    }


    @Override
    public void setIndicators() {
        tensionInd = addIndicator(new Tension(getParam(FAST_PERIOD), getParam(SLOW_PERIOD), getParam(SCALE_FACTOR)));
    }


    /**
     * Adds parameters to strategy. Each parameter must have 5 values:
     * name: identifier
     * min, max, step: range for optimizer
     * value: used in backtesting and trading
     */
    @Override
    public void setParams() {
        addParam(FAST_PERIOD, 1, 12, 1, 7);
        addParam(SLOW_PERIOD, 200, 3500, 100, 818);
        addParam(SCALE_FACTOR, 10, 60, 100, 48);
        addParam(ENTRY, 15, 35, 1, 28);
        addParam(EXIT, 10, 20, 1, 15);
    }

    /**
     * Framework invokes this method when a new snapshot of the limit order book is taken
     * and the technical indicators are recalculated. This is where the strategy itself
     * (i.e., its entry and exit conditions) should be defined.
     */
    @Override
    public void onBookSnapshot() {
        double tension = tensionInd.getValue();
        if (tension >= entry) {
            setPosition(1);
        } else if (tension <= -entry) {
            setPosition(-1);
        } else {
            int currentPosition = getPositionManager().getCurrentPosition();
            if (tension >= exit && currentPosition < 0) {
                setPosition(0);
            }
            if (tension <= -exit && currentPosition > 0) {
                setPosition(0);
            }
        }
    }
}