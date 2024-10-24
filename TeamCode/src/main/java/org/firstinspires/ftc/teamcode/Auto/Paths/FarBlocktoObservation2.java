package org.firstinspires.ftc.teamcode.Auto.Paths;



// RR-specific imports
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

// Non-RR imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous
public class FarBlocktoObservation2 extends LinearOpMode {

    @Override
    public void runOpMode() {
        Pose2d StartPose1 = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap,StartPose1);

        //Pose2d StartPose1 = new Pose2d(-40, -60, 0);
        //drive.setPoseEstimate(StartPose1);

        TrajectoryActionBuilder basket = drive.actionBuilder(StartPose1)
                .strafeToLinearHeading(new Vector2d(-29.46,-12.24), Math.toRadians(0))
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(-12.42,41.71), Math.toRadians(178))
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(-17.55,34.21), Math.toRadians(-3))
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(-18.42,48.71), Math.toRadians(178))
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(-17.55,34.21), Math.toRadians(-3))
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(-30.81,38.9), Math.toRadians(89))
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(-17.55,34.21), Math.toRadians(-3))
                .waitSeconds(4)
                .strafeToLinearHeading(new Vector2d(0,34.21), Math.toRadians(180));






        Action path = basket.build();
        waitForStart();
        Actions.runBlocking(path);

    }
}
