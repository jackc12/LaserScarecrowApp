package com.example.jc.newlaserscarecrow.application;

/**
 * Created by Andrew on 3/5/18.
 */

public interface CommandInterface
{
    interface Commands
    {
        int NUM_COMMANDS = 14;
        String L_STEPPER_SPEED = "L101";
        String L_PITCH_MIN = "L131";
        String L_PITCH_RANGE = "L132";
        String L_ROT_ANGLE = "L111";
        String L_CYCLE_MODE = "L201";
        String L_LIGHT_THRES = "L221";
        String L_YEAR_MONTH_DAY = "L251";
        String L_HOUR_MIN_SEC = "L252";
        String L_WAKE_TIME = "L261";
        String L_SLEEP_TIME = "L262";
        String L_ROTATE_POS = "L121";
        String L_ROTATE_NEG = "L122";
        String L_ROT_STATE = "L11";
        String L_CURRENT_LIGHT = "L210";

        // Spaces already in String
        String S_STEPPER_SPEED = "S101 ";
        String S_PITCH_MIN = "S131 ";
        String S_PITCH_RANGE = "S132 ";
        String S_ROT_ANGLE = "S111 ";
        String S_CYCLE_MODE = "S201 ";
        String S_LIGHT_THRES = "S221 ";
        String S_YEAR_MONTH_DAY = "S251 ";
        String S_HOUR_MIN_SEC = "S252 ";
        String S_WAKE_TIME = "S261 ";
        String S_SLEEP_TIME = "S262 ";
        String S_ROTATE_POS = "S121";
        String S_ROTATE_NEG = "S122";
        String S_ROT_STATE = "S11";
    }
}
