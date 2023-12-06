package emu.lunarcore.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    public String label() default "";
    
    public String[] aliases() default "";

    public String desc() default "";
    
    public String permission() default "";
    
    public boolean requireTarget() default false;
}
