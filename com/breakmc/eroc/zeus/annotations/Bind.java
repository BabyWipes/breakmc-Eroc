package com.breakmc.eroc.zeus.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
public @interface Bind {
    Class<?> value();
}
