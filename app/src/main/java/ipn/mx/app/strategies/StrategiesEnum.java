package ipn.mx.app.strategies;


import ipn.mx.app.Index;

public enum StrategiesEnum {

    INDEX(0, Index.class),
    STRENGHT(1, StrategyStrength.class),
    BREATHING(2, StrategyBreathing.class);

    private final int settingsPosition;
    private final Class resId;

    StrategiesEnum(int settingsPosition, Class resId) {
        this.settingsPosition = settingsPosition;
        this.resId = resId;
    }

    public Class getResId() {
        return resId;
    }

}
