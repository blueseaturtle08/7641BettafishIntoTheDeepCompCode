package org.firstinspires.ftc.teamcode.mechanisms;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    private Servo bucketLeftServo;
    private Servo bucketRightServo;
    private Servo clawLeftServo;
    private Servo clawRightServo;

    public Claw(HardwareMap HWMap) {
        bucketLeftServo = HWMap.get(Servo.class, "bucketLeftServo");
//        bucketRightServo = HWMap.get(Servo.class, "bucketLeftServo");
//        clawLeftServo = HWMap.get(Servo.class, "clawLeftServo");
//        clawRightServo = HWMap.get(Servo.class, "clawLeftServo");
    }

    public double getBucketPos() {
        return bucketLeftServo.getPosition();
    }

    public class Flip implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
                bucketLeftServo.setPosition(1);
//            bucketRightServo.setPosition(1);
            return false;
        }
    }

    public Action flip() {
        return new Flip();
    }

    public class Flop implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
                bucketLeftServo.setPosition(0);
//            bucketRightServo.setPosition(0);
            return false;
        }
    }

    public Action flop() {
        return new Flop();
    }

    public class Close implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            clawLeftServo.setPosition(1);
//            clawRightServo.setPosition(1);
            return false;
        }
    }

    public Action close() {
        return new Close();
    }

    public class Open implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            clawLeftServo.setPosition(0);
//            clawRightServo.setPosition(0);
            return false;
        }
    }

    public Action open() {
        return new Open();
    }

    public class Up implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            bucketLeftServo.setPosition(0.5);
//            bucketRightServo.setPosition(0.5);
            return false;
        }
    }

    public Action up() {
        return new Up();
    }
}

