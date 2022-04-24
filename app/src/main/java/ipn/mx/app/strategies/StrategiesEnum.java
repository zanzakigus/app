package ipn.mx.app.strategies;


import ipn.mx.app.Index;

public enum StrategiesEnum {

    BREATHING(0, StrategyBreathing.class),
    STRENGHT(1, StrategyStrength.class),
    MEDITATION(2, StrategyTemporal.class),
    PHRASES(3, StrategyPhrases.class);


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
