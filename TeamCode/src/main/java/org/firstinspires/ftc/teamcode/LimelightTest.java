package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

//@TeleOp(name = "LimelightTest", group = "Limelight")
public class LimelightTest extends OpMode {

    private Limelight3A limelight;
    private IMU imu;
    private double distance;

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        // This sets how often we ask Limelight for data (100 times per second)

        // ⚠ Choose ONE pipeline (last call wins)
        limelight.pipelineSwitch(5);

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot revHubOrientationOnRobot =
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                        RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                );

        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
    }

    @Override
    public void start() {
        //limelight.setPollRateHz(100);
        limelight.start();

    }

    @Override
    public void loop() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw(AngleUnit.RADIANS));

        LLResult llResult = limelight.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            Pose3D botPose = llResult.getBotpose();
            telemetry.addData("Calculated Distance", distance);
            telemetry.addData("Target X", llResult.getTx());
            telemetry.addData("Target Y", llResult.getTy());
            telemetry.addData("Target Area", llResult.getTa());
            /*
            telemetry.addData("X", botPose.getPosition().x);
            telemetry.addData("Y", botPose.getPosition().y);
            */
            telemetry.addData("Bot Pose", botPose.toString());
            telemetry.addData("Heading", botPose.getOrientation().getYaw());
        } else {
            telemetry.addLine("No valid Limelight data");
        }

        telemetry.update();
    }
}

