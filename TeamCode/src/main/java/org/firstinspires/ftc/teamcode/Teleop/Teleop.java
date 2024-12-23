/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.Teleop;

import android.graphics.Color;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.Claw;
import org.firstinspires.ftc.teamcode.mechanisms.Control;
import org.firstinspires.ftc.teamcode.mechanisms.Extendo;
import org.firstinspires.ftc.teamcode.mechanisms.Intaker;
import org.firstinspires.ftc.teamcode.mechanisms.SlidesV2;

import java.util.ArrayList;
import java.util.List;


@TeleOp
public class Teleop extends LinearOpMode {

    private FtcDashboard dash = FtcDashboard.getInstance();
    private List<Action> runningActions = new ArrayList<>();

    private enum LiftState {LIFTSTART, LIFTDEPOSIT, LIFTWALL, LIFTTOPBAR, LIFTBOTTOMBAR}
    private LiftState liftState = LiftState.LIFTSTART;

    private enum ExtendoState {EXTENDOSTART, EXTENDOEXTEND, EXTENDORETRACT}
    private ExtendoState extendoState = ExtendoState.EXTENDOSTART;

    private boolean intakeUp = false;
    private boolean turning = false;
    private enum HangState {HANGSTART, HANGUP}
    private HangState hangState = HangState.HANGSTART;


    @Override
    public void runOpMode() {

        Pose2d StartPose1 = new Pose2d(0,0, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, StartPose1);

        Action basketSub1 = drive.actionBuilder(drive.pose)
                .turnTo(Math.toRadians(15))
                .strafeToLinearHeading(new Vector2d(40, -10), Math.toRadians(15))
                .turnTo(Math.toRadians(-45))
                .build();
        Action basketSub2 = drive.actionBuilder(drive.pose)
                .turnTo(Math.toRadians(-15))
                .strafeToLinearHeading(new Vector2d(30, -20), Math.toRadians(15))
                .turnTo(Math.toRadians(45))
                .build();
        Action observationSub1 = drive.actionBuilder(drive.pose)
                .strafeToLinearHeading(new Vector2d(30, -20), Math.toRadians(-135))
                .turnTo(Math.toRadians(45))
                .build();
        NormalizedColorSensor colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");

        final float[] hsvValues = new float[3];

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }

        NormalizedRGBA colors;



        String intakeColor;
//
//        DcMotor FL = hardwareMap.get(DcMotor.class, "FL");
//        DcMotor BL = hardwareMap.get(DcMotor.class, "BL");
//        DcMotor FR = hardwareMap.get(DcMotor.class, "FR");
//        DcMotor BR = hardwareMap.get(DcMotor.class, "BR");
//
//        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        FR.setDirection(DcMotorSimple.Direction.REVERSE);
//        BR.setDirection(DcMotorSimple.Direction.REVERSE);

        IMU imu = hardwareMap.get(IMU.class, "imu");


        Gamepad currentGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();

        Gamepad previousGamepad1 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();

        // ExtendoV2 extendo = new ExtendoV2(hardwareMap);
        Extendo extendo = new Extendo(hardwareMap);
        Intaker intake = new Intaker(hardwareMap);
        SlidesV2 slides = new SlidesV2(hardwareMap, true);
        Claw claw = new Claw(hardwareMap);
        Control extendocontrol = new Control();
        Control slidescontrol = new Control();


        runningActions.add(new SequentialAction(
                intake.flop(),
                claw.flop(),
                claw.open(),
                extendo.retract(),
                slides.retract()
        ));



        waitForStart();

        while (opModeIsActive()) {
            TelemetryPacket packet = new TelemetryPacket();

            colorSensor.setGain(50);

            colors = colorSensor.getNormalizedColors();

            Color.colorToHSV(colors.toColor(), hsvValues);

//            if (colors.red > 0.2 && colors.green > 0.28 && colors.blue < 0.42) {
//                intakeColor = "yellow";
//            } else if (colors.red > 0.15 && colors.green < 0.4 && colors.blue < 0.3) {
//                intakeColor = "red";
//            } else if (colors.red < 0.3 && colors.green < 0.4 && colors.blue > 0.15) {
//                intakeColor = "blue";
//            } else {
//                intakeColor = "none";
//            }

            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);

            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            double lefty1 = currentGamepad1.left_stick_y;
            double leftx1 = -currentGamepad1.left_stick_x;
            double rightx1 = -currentGamepad1.right_stick_x;
            double lefty2 = currentGamepad2.left_stick_y;

            double denominator = Math.max(Math.abs(lefty1) + Math.abs(leftx1) + Math.abs(rightx1), 1);
            double frontLeftPower = (lefty1 + leftx1 + rightx1) / denominator;
            double backLeftPower = (lefty1 - leftx1 + rightx1) / denominator;
            double frontRightPower = (lefty1 - leftx1 - rightx1) / denominator;
            double backRightPower = (lefty1 + leftx1 - rightx1) / denominator;


            if (currentGamepad1.right_trigger > 0.9) {
                imu.resetYaw();
                turning = true;
            }

            if (turning) {
                double heading = imu.getRobotYawPitchRollAngles().getYaw();
                frontLeftPower += (180 - heading) / 180;
                backLeftPower += (180 - heading) / 180;
                frontRightPower -= (180 - heading) / 180;
                backRightPower -= (180 - heading) / 180;
                if (Math.abs(180 - heading) < 2) {
                    turning = false;
                }
            }

            if (currentGamepad1.left_trigger < 0.9) {
                drive.leftFront.setPower(frontLeftPower);
                drive.leftBack.setPower(backLeftPower);
                drive.rightFront.setPower(frontRightPower);
                drive.rightBack.setPower(backRightPower);
            } else {
                drive.leftFront.setPower(frontLeftPower * 0.6);
                drive.leftBack.setPower(backLeftPower * 0.6);
                drive.rightFront.setPower(frontRightPower * 0.6);
                drive.rightBack.setPower(backRightPower * 0.6);
            }

            if (currentGamepad1.options && !previousGamepad1.options) {
                drive.pose = new Pose2d(0, 0, 0);
            }

            if (currentGamepad1.x && !previousGamepad1.x) {
                runningActions.add(basketSub1);
            }
            if (currentGamepad1.a && !previousGamepad1.a) {
                runningActions.add(basketSub2);
            }
            if (currentGamepad1.b && !previousGamepad1.b) {
                runningActions.add(observationSub1);
            }

            switch (extendoState) {
                case EXTENDOSTART:
                    if (!extendocontrol.getBusy()) {
                        if (currentGamepad2.a && !previousGamepad2.a) {
                            runningActions.add(new SequentialAction(
                                    extendocontrol.start(),
                                    extendo.extend(),
                                    intake.flip(),
                                    intake.intake(),
                                    extendocontrol.done()
                            ));
                        }

                        if (currentGamepad2.dpad_left) {
                            intake.intakeMotor.setPower(0.55);
                        } else if (currentGamepad2.dpad_right) {
                            intake.intakeMotor.setPower(-0.7);
                        } else {
                            intake.intakeMotor.setPower(0);
                        }

                    }

                    if (extendocontrol.getFinished()) {
                        extendocontrol.resetFinished();
                        intakeUp = false;
                        extendoState = ExtendoState.EXTENDOEXTEND;
                    }
                    break;
                case EXTENDOEXTEND:
                    if (!extendocontrol.getBusy()) {
                        if ((currentGamepad2.a && !previousGamepad2.a)/* || intakeColor.equals("blue") || intakeColor.equals("yellow")*/) {
                            runningActions.add(new SequentialAction(
                                    extendocontrol.start(),
                                    intake.creep(),
                                    intake.flop(),
                                    extendo.retract(),
                                    extendocontrol.done()
                            ));
                        }



                        if (currentGamepad2.dpad_left && !previousGamepad2.dpad_left && currentGamepad2.left_trigger > 0.9) {
                            if (!intakeUp) {
                                runningActions.add(intake.middle());
                                intakeUp = true;
                            } else {
                                runningActions.add(intake.flip());
                                intakeUp = false;
                            }
                        } else if (currentGamepad2.dpad_left/* || intakeColor.equals("red")*/) {
                            runningActions.add(intake.extake());
                        } else {
                            runningActions.add(intake.intake());
                        }

                    }

                    if (extendocontrol.getFinished()) {
                        extendocontrol.resetFinished();
                        extendoState = ExtendoState.EXTENDORETRACT;
                    }
                    break;
                case EXTENDORETRACT:


                    if ((currentGamepad2.a && !previousGamepad2.a)) {
                        runningActions.add(intake.extake());
                    }
                    if (!currentGamepad2.a && previousGamepad2.a/* || intakeColor.equals("none")*/) {
                        runningActions.add(new SequentialAction(
                                intake.off(),
                                new SleepAction(0.2),
                                claw.up()
                        ));
                        extendoState = ExtendoState.EXTENDOSTART;
                    }
                    break;
                default:
                    extendoState = ExtendoState.EXTENDOSTART;
                    break;
            }




            switch (liftState) {
                case LIFTSTART:
                    if (currentGamepad2.y && !previousGamepad2.y) {
                        if (currentGamepad2.left_trigger < 0.9) {
                            runningActions.add(slides.slideTopBasket());
                        } else {
                            runningActions.add(slides.slideBottomBasket());
                        }
                        liftState = LiftState.LIFTDEPOSIT;
                    }

                    if (currentGamepad2.x && !previousGamepad2.x) {
                        runningActions.add(new SequentialAction(
                                slides.slideWallLevel(),
                                claw.open()
                        ));
                        liftState = LiftState.LIFTWALL;
                    }
                    break;
                case LIFTDEPOSIT:
                    if (currentGamepad2.y && !previousGamepad2.y && !slidescontrol.getBusy()) {
                        runningActions.add(new SequentialAction(
                                slidescontrol.start(),
                                claw.flip(),
                                new SleepAction(1),
                                claw.flop(),
                                new SleepAction(1.5),
                                slides.retract(),
                                slidescontrol.done()
                        ));
                    }


                    if (slidescontrol.getFinished()) {
                        slidescontrol.resetFinished();
                        liftState = LiftState.LIFTSTART;
                    }

                    break;
                case LIFTWALL:
                    if (currentGamepad2.x && !previousGamepad2.x) {
                        if (currentGamepad2.left_trigger < 0.9) {
                            runningActions.add(new SequentialAction(
                                    claw.close(),
                                    new SleepAction(2),
                                    slides.slideTopBar()
                            ));
                            liftState = LiftState.LIFTTOPBAR;
                        } else {
                            runningActions.add(new SequentialAction(
                                    claw.close(),
                                    slides.slideBottomBar()
                            ));
                            liftState = LiftState.LIFTBOTTOMBAR;
                        }
                    }
                    break;
                case LIFTTOPBAR:
                    if (currentGamepad2.x && !previousGamepad2.x) {
                        runningActions.add(new SequentialAction(
                                slides.slideBottomBar(),
                                claw.open(),
                                new SleepAction(0.2),
                                slides.retract()
                        ));
                        liftState = LiftState.LIFTSTART;
                    }
                    break;
                case LIFTBOTTOMBAR:
                    if (currentGamepad2.x && !previousGamepad2.x) {
                        runningActions.add(slides.retract());
                        liftState = LiftState.LIFTSTART;
                    }
                    break;
                default:
                    liftState = LiftState.LIFTSTART;
                    break;
            }

            switch (hangState) {
                case HANGSTART:
                    if (currentGamepad1.y && !previousGamepad1.y) {
                        runningActions.add(slides.slideHang());
                        hangState = HangState.HANGUP;
                    }
                    break;
                case HANGUP:
                    if (currentGamepad1.y && !previousGamepad1.y) {
                        runningActions.add(slides.retract());
                        hangState = HangState.HANGSTART;
                    }
            }

            if (currentGamepad2.b && !previousGamepad2.b) {
                liftState = LiftState.LIFTSTART;
                extendoState = ExtendoState.EXTENDOSTART;

                runningActions.add(new SequentialAction(
                        intake.off(),
                        intake.flop(),
                        claw.flop(),
                        extendo.retract(),
                        slides.retract(),
                        claw.open()
                ));
            }

            
            if (currentGamepad2.dpad_up) {
                slides.changeTarget(-20);
            } else if (currentGamepad2.dpad_down) {
                slides.changeTarget(20);
            }

            slides.updateMotors();

            List<Action> newActions = new ArrayList<>();
            for (Action action : runningActions) {
                action.preview(packet.fieldOverlay());
                if (action.run(packet)) {
                    newActions.add(action);
                }
            }
            runningActions = newActions;
            dash.sendTelemetryPacket(packet);


            telemetry.addData("redv", colors.red);
            telemetry.addData("bluev", colors.blue);
            telemetry.addData("greenv", colors.green);
            telemetry.addData("slides left", slides.slidesLeftMotor.getCurrentPosition());
            telemetry.addData("slides right", slides.slidesRightMotor.getCurrentPosition());
//            telemetry.addData("color", intakeColor);
            telemetry.update();

        }
    }
}
