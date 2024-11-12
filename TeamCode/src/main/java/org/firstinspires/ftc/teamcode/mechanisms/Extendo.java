package org.firstinspires.ftc.teamcode.mechanisms;



import android.util.Log;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.PIDFController;

@Config
public class Extendo {
    public DcMotor extendoMotor;

    public static double KP = 0.05;
    public static double KI = 0;
    public static double KD = 0.01;

    private PIDFController.PIDCoefficients extendoMotorCoeffs = new PIDFController.PIDCoefficients(KP, KI, KD);
    public PIDFController extendoMotorPID = new PIDFController(extendoMotorCoeffs);

    private double target = 0;

    public Extendo(HardwareMap HWMap){
        extendoMotor = HWMap.get(DcMotor.class, "extendoMotor");
        extendoMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        extendoMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        extendoMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public class Retract implements Action {
        private boolean init = false;

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            if (!init) {
                target = 0;
//                extendoMotor.setTargetPosition(target);
//                extendoMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                extendoMotor.setPower(1);
                extendoMotorPID.setTargetPosition(target);
                init = true;
            }

            updateMotor();

            if (Math.abs(extendoMotorPID.getTargetPosition() - getPos()) < 2) {
                extendoMotor.setPower(0);
                return false;
            }
            return true;
        }
    }
    public Action retract() {
        return new Retract();
    }

    public class Extend implements Action {
        private boolean init = false;

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            if (!init) {
                target = -90;
//                extendoMotor.setTargetPosition(target);
//                extendoMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                extendoMotor.setPower(1);
                extendoMotorPID.setTargetPosition(target);
                init = true;
            }

            updateMotor();

            if (Math.abs(extendoMotorPID.getTargetPosition() - getPos()) < 2) {
                return false;
            }
            return true;
        }
    }
    public Action extend() {
        return new Extend();
    }


    public double getPos() {
        return (extendoMotor.getCurrentPosition());
    }

    public void updateMotor() {
        extendoMotor.setPower(extendoMotorPID.update(extendoMotor.getCurrentPosition()));
    }

    public void changetarget(double change){
        target += change;
        extendoMotorPID.setTargetPosition(target);
    }

//    public void setValues(double kP, double kI, double kD) {
//        this.kP = kP;
//        this.kI = kI;
//        this.kD = kD;
//    }

}


