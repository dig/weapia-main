package net.sunken.common.command;

import net.sunken.common.player.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Command {

    String[] aliases() default {};
    String desc() default "No description for this command.";
    String usage() default "None";

    Rank rank() default Rank.PLAYER;

    int min() default 0;
    int max() default 0;

    String errorPermission() default "You don't have permission to execute this command.";
    String errorArguments() default "You have supplied the wrong amount of arguments for this command.";
    String errorCooldown() default "Please wait before trying this command again.";

    long cooldown() default 300L;

}
