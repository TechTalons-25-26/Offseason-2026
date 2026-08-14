package org.firstinspires.ftc.teamcode.opmode.sampleAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

public class kanishkIvySampleAuto extends OpMode {

    private Follower follower;
    private Paths path;

    @Override
    public void init() {
        //too lazy to set up allat hardware map stuff

        path = new Paths(follower);
    }

    @Override
    public void start() {
        schedule(
                sequential(
                    peakLaziness(path.Path1),
                    peakLaziness(path.Path2)
                )
        );
    }

    //useless 4 auto
    public void loop() {

    }


    public static class Paths {
        public PathChain Path1,Path2;

        public Paths(Follower follower) {
            Path1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(56.000, 8.000),

                                    new Pose(72.500, 72.500)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(130))

                    .build();
            Path2 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(72.500, 72.500),

                                    new Pose(56.000, 8.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(130), Math.toRadians(90))

                    .build();
        }
    }

    public Command peakLaziness(PathChain path) {
        return follow(follower, path);
    }

}
