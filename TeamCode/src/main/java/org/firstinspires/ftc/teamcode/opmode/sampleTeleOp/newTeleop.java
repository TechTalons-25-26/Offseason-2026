package org.firstinspires.ftc.teamcode.opmode.sampleTeleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.config.pedroPathing.Constants;

@TeleOp(name = "Blue TeleOp")
public class newTeleop extends LinearOpMode {

    //Pedro Constants
    private Follower follower;
    private PathChain automaticPath;

    //Motors
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private DcMotor intake;
    private DcMotorEx outtake;

    //Outtake PIDF Values (To tune later)
    private double outtakeTargetVelocity = 0;

    private double kP = 0;
    private double kI = 0;
    private double kD = 0;
    private double kF = 0;


    @Override
    public void runOpMode() {

        // Hardware Maps

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        intake = hardwareMap.get(DcMotor.class, "intake");
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");



        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);



        follower = Constants.createFollower(hardwareMap);

        follower.setStartingPose(
                new Pose(0, 0, 0)
        );


        // Auto Button

        automaticPath = follower.pathBuilder()

                .addPath(
                        new Path(
                                new BezierLine(
                                        follower::getPose,

                                        // CHANGE THIS
                                        // to the coordinate you want
                                        new Pose(
                                                -39,
                                                -14,
                                                Math.toRadians(-43)
                                        )
                                )
                        )
                )

                .setLinearHeadingInterpolation(
                        0,
                        Math.toRadians(-43)
                )

                .build();




        outtake.setVelocityPIDFCoefficients(
                kP,
                kI,
                kD,
                kF
        );

        //Telemetry
        telemetry.addLine("Blue TeleOp Initialized");
        telemetry.addLine("A = Automatic Position");
        telemetry.addLine("B = Intake");
        telemetry.addLine("RT = Outtake");
        telemetry.update();


        waitForStart();




        while (opModeIsActive()) {


            follower.update();


            if (!follower.isBusy()) {

                double y = -gamepad1.left_stick_y;
                double x = gamepad1.left_stick_x;
                double rx = gamepad1.right_stick_x;


                // Mecanum calculations
                double frontLeftPower = y + x + rx;
                double backLeftPower = y - x + rx;
                double frontRightPower = y - x - rx;
                double backRightPower = y + x - rx;


                // Normalize
                double max = Math.max(
                        1.0,
                        Math.max(
                                Math.abs(frontLeftPower),
                                Math.max(
                                        Math.abs(backLeftPower),
                                        Math.max(
                                                Math.abs(frontRightPower),
                                                Math.abs(backRightPower)
                                        )
                                )
                        )
                );


                frontLeft.setPower(frontLeftPower / max);
                backLeft.setPower(backLeftPower / max);
                frontRight.setPower(frontRightPower / max);
                backRight.setPower(backRightPower / max);

            }



            if (gamepad2.a) {

                intake.setPower(1.0);

            } else {

                intake.setPower(0);
            }



            if (gamepad2.right_trigger > 0.05) {

                // CHANGE THIS TARGET VELOCITY
                outtakeTargetVelocity = 2000;

                outtake.setVelocity(
                        outtakeTargetVelocity
                );

            } else {

                outtakeTargetVelocity = 0;

                outtake.setVelocity(0);
            }


            if (gamepad1.aWasPressed()) {

                follower.followPath(
                        automaticPath
                );
            }


            // Telemetry

            telemetry.addData(
                    "X",
                    follower.getPose().getX()
            );

            telemetry.addData(
                    "Y",
                    follower.getPose().getY()
            );

            telemetry.addData(
                    "Heading",
                    Math.toDegrees(
                            follower.getPose().getHeading()
                    )
            );

            telemetry.addData(
                    "Path Busy",
                    follower.isBusy()
            );

            telemetry.addData(
                    "Outtake Velocity",
                    outtake.getVelocity()
            );

            telemetry.addData(
                    "Outtake Target",
                    outtakeTargetVelocity
            );

            telemetry.update();
        }
    }
}