package org.firstinspires.ftc.teamcode.opmode.sampleAuto; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static com.pedropathing.ivy.Scheduler.*;
import static com.pedropathing.ivy.pedro.PedroCommands.*;
import static com.pedropathing.ivy.groups.Groups.*;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.config.pedroPathing.Constants;

@Autonomous(name = "Shruthi Example Auto", group = "Examples")
public class ShruthiIvyAuto extends LinearOpMode {

    private Follower follower;

    //defining our PathChains
    private PathChain mainPath1, mainPath2;

    private DcMotor stage1;

    double power = 0;
    public void buildPaths() {

        mainPath1 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(56.000, 8.000),
                                new Pose(52.183, 31.891),
                                new Pose(35.209, 35.394)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                .build();

        mainPath2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(35.209, 35.394),
                                new Pose(13.906, 35.896)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
    private Command raiseArm;
    public Command autoRoutine() {
        return parallel(
                follow(follower, mainPath1),
                //follow(follower, mainPath2, true),
                raiseArm
        );
    }

        @Override
        public void runOpMode() {
            //These will run when the OpMode is initiated

            Scheduler.reset();
            follower = Constants.createFollower(hardwareMap);
            buildPaths();
            follower.setStartingPose(new Pose(56.000, 8.000, Math.toRadians(90)));
            stage1 = hardwareMap.get(DcMotor.class, "stage1");
            stage1.setDirection(DcMotor.Direction.FORWARD);

            raiseArm = Command.build()
                    .setExecute(() -> stage1.setPower(0.7))
                    .setDone(() -> false)
                    .setEnd(endCondition -> stage1.setPower(0))
                    .requiring(stage1);

            waitForStart();
            //We schedule all our commands when we start the OpMode
            schedule(autoRoutine());
            while (opModeIsActive()) {
                //Update the follower and execute the scheduler every loop
                follower.update();
                Scheduler.execute();

                // Feedback to Driver Hub for debugging
                telemetry.addData("x", follower.getPose().getX());
                telemetry.addData("y", follower.getPose().getY());
                telemetry.addData("heading", follower.getPose().getHeading());
                telemetry.update();
            }
        }



}