package org.firstinspires.ftc.teamcode.opmode.sampleAuto; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import static com.pedropathing.ivy.Scheduler.*;
import static com.pedropathing.ivy.pedro.PedroCommands.*;

import org.firstinspires.ftc.teamcode.config.pedroPathing.Constants;

@Autonomous(name = "Example Auto", group = "Examples")
public class CalebIvyAuto extends LinearOpMode {

    private Follower follower;
    private Paths paths;

    // Class containing path builders
    public static class Paths {
        public PathChain MainChain;

        public Paths(Follower follower) {
            MainChain = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(96.000, 72.000),
                                    new Pose(48.000, 72.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .addPath(
                            new BezierLine(
                                    new Pose(48.000, 72.000),
                                    new Pose(72.000, 36.000)
                            )
                    )
                    .setTangentHeadingInterpolation()
                    .addPath(
                            new BezierLine(
                                    new Pose(72.000, 36.000),
                                    new Pose(96.000, 72.000)
                            )
                    )
                    .setTangentHeadingInterpolation()
                    .build();
        }
    }

    public Command autoRoutine() {
        return follow(follower, paths.MainChain);
    }

    @Override
    public void runOpMode() {
        // Run during initiation
        Scheduler.reset();
        follower = Constants.createFollower(hardwareMap);

        // Build path chain and set initial pose to (96, 72, 90°)
        paths = new Paths(follower);
        follower.setStartingPose(new Pose(96.000, 72.000, Math.toRadians(90)));

        waitForStart();

        // Schedule autonomous command
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            // Telemetry debug feedback
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.update();
        }
    }
}