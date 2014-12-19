package com.breakmc.eroc.zeus.annotations;

import java.lang.annotation.*;
import javax.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Command {
    @Nonnull
    String name();
    
    String[] aliases() default {};
    
    String desc() default "Default Description";
    
    String usage() default "/<command>";
    
    String permission() default "";
    
    String permissionMsg() default "§cNo Permissions.";
    
    int minArgs() default 0;
    
    int maxArgs() default -1;
}
