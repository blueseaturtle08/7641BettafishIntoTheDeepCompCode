package org.firstinspires.ftc.teamcode.mechanisms;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    private Servo clawLeftServo;
    private Servo clawRightServo;
    private Servo clawServo;

    public Claw(HardwareMap HWMap) {
        clawLeftServo = HWMap.get(Servo.class, "clawLeftServo");
        clawRightServo = HWMap.get(Servo.class, "clawLeftServo");
        clawServo = HWMap.get(Servo.class, "clawServo");
    }

    public class Flip implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            clawLeftServo.setPosition(1);
            clawRightServo.setPosition(1);
            return false;
        }
    }

    public Action flip() {
        return new Flip();
    }

    public class Flop implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            clawLeftServo.setPosition(0);
            clawRightServo.setPosition(0);
            return false;
        }
    }

    public Action flop() {
        return new Flop();
    }

    public class Close implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            clawServo.setPosition(1);
            return false;
        }
    }

    public Action close() {
        return new Close();
    }

    public class Open implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            clawServo.setPosition(1);
            return false;
        }
    }

    public Action open() {
        return new Open();
    }
}


//    public void flip() {
//        clawLeftServo.setPosition(1);
//        clawRightServo.setPosition(1);
//    }
//
//    public void flop() {
//        clawLeftServo.setPosition(0);
//        clawRightServo.setPosition(0);
//    }
//
//    public void close() {
//        clawServo.setPosition(1);
//    }
//
//    public void open() {
//        clawServo.setPosition(0);
//    }
