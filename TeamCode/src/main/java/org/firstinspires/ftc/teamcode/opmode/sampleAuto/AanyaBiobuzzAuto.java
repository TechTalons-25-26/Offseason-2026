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
public class AanyaBiobuzzAuto extends LinearOpMode {

    private Follower follower;

    //defining our PathChains
    private PathChain mainPath1, mainPath2, mainPath3, mainPath4, mainPath5, mainPath6, mainPath7;

    private DcMotor stage1;
    private DcMotor outtake;

    double power = 0;
    public void buildPaths() {

        mainPath1 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(106.235, 10.359),
                                new Pose(81.220, 34.636),
                                new Pose(106.220, 47.012)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(106.220, 47.012),
                                new Pose(130.012, 59.042),
                                new Pose(106.230, 69.988)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(106.230, 69.988),
                                new Pose(83.039, 83.144),
                                new Pose(106.133, 94.333)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(106.133, 94.333),
                                new Pose(129.689, 105.334),
                                new Pose(106.267, 129.405)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(106.267, 129.405),
                                new Pose(91.243, 135.366),
                                new Pose(82.104, 117.728)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierLine(
                                new Pose(82.104, 117.728),
                                new Pose(82.078, 23.225)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(82.078, 23.225),
                                new Pose(82.965, 2.230),
                                new Pose(106.505, 10.093)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
    private Command raiseArm;
    private Command spin;

    public Command autoRoutine() {
        return sequential(
                deadline(
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
