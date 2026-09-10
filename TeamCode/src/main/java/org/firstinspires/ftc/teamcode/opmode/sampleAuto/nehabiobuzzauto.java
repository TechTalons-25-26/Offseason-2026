package org.firstinspires.ftc.teamcode.opmode.sampleAuto; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.behaviors.BlockedBehavior;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.behaviors.InterruptedBehavior;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static com.pedropathing.ivy.Scheduler.*;
import static com.pedropathing.ivy.pedro.PedroCommands.*;
import static com.pedropathing.ivy.groups.Groups.*;

import org.firstinspires.ftc.teamcode.config.pedroPathing.Constants;

@Autonomous(name = "neha biobuzz auto", group = "Examples")
public class nehabiobuzzauto extends LinearOpMode {

    private Follower follower;

    //defining our PathChains
    private PathChain mainPath1, mainPath2, mainPath3;

    private DcMotor stage1;
    private DcMotor outtake;

    double power = 0;
    public void buildPaths() {

        mainPath1 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(80.962, 8.186),
                                new Pose(80.570, 46.679),
                                new Pose(105.538, 44.080)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        mainPath2 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(105.538, 44.080),
                                new Pose(139.902, 41.915),
                                new Pose(143.255, 95.565),
                                new Pose(89.294, 63.426),
                                new Pose(53.968, 81.633),
                                new Pose(101.713, 111.959),
                                new Pose(134.228, 98.330),
                                new Pose(130.581, 91.163),
                                new Pose(138.039, 139.343),
                                new Pose(80.353, 125.940)
                        )
                ).setTangentHeadingInterpolation()
                .build();
        mainPath3 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(80.353, 125.940),
                                new Pose(63.276, 125.000),
                                new Pose(74.789, 8.424)
                        )
                ).setTangentHeadingInterpolation()
                .build();

    }
    private Command raiseArm;
    private Command spin;

    public Command autoRoutine() {
        return sequential(
                follow(follower, mainPath1),
                follow(follower, mainPath2, true),
                follow(follower, mainPath3, true)

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
                .requiring(stage1)
                .setInterruptedBehavior(InterruptedBehavior.END)
                .setBlockedBehavior(BlockedBehavior.CANCEL)
                .setConflictBehavior(ConflictBehavior.CANCEL)
                .setPriority(1);

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