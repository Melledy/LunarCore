package emu.lunarcore.data.config;

public class DynamicFloat {
    private boolean IsDynamic;
    private double FixedValue;
    
    public double getValue() {
        if (!IsDynamic) {
            return FixedValue;
        }
        return 15;
    }
}
