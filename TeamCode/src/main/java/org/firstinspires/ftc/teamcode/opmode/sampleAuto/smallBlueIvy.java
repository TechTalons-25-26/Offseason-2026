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

import org.firstinspires.ftc.teamcode.config.pedroPathing.Constants;

@Autonomous(name = "small blue ivy", group = "Examples")
public class smallBlueIvy extends LinearOpMode {

    private Follower follower;

    //defining our PathChains
    private PathChain mainPath1, mainPath2, mainPath3, mainPath4;

    private DcMotor stage1;
    private DcMotor outtake;

    double power = 0;
    public void buildPaths() {

        mainPath1 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(56.000, 8.000),
                                new Pose(56.162, 35.506),
                                new Pose(45.268, 35.767)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        mainPath2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(45.268, 35.767),

                                new Pose(16.207, 35.953)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                .build();
        mainPath3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(16.207, 35.953),

                                new Pose(45.226, 35.898)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                .build();

        mainPath4 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(45.226, 35.898),
                                new Pose(71.753, 36.620),
                                new Pose(64.909, 86.147),
                                new Pose(31.863, 112.415)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
    private Command raiseArm;
    private Command spin;

    public Command autoRoutine() {
        return sequential(
                race(
                    sequential(
                            follow(follower, mainPath1),
                            follow(follower, mainPath2, true),
                            follow(follower, mainPath3, true)
                    ),
                    raiseArm
                    //follow(follower, mainPath2, true),

            ),
                sequential(
                        follow(follower, mainPath4),
                        spin

        )

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
        outtake = hardwareMap.get(DcMotor.class, "outtake");
        stage1.setDirection(DcMotor.Direction.FORWARD);

        raiseArm = Command.build()
                .setExecute(() -> stage1.setPower(0.7))
                .setDone(() -> stage1.getCurrentPosition() >1000)
                .setEnd(endCondition -> stage1.setPower(0))
                .requiring(stage1);
        spin = Command.build()
                .setExecute(() -> outtake.setPower(0.7))
                .setDone(() -> outtake.getCurrentPosition() >1000)
                .setEnd(endCondition -> outtake.setPower(0))
                .requiring(outtake);

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