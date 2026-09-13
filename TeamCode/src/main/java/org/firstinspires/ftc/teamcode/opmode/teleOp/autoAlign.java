package org.firstinspires.ftc.teamcode.opmode.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.config.subsystems.mecanum.MecanumDrive;
import org.firstinspires.ftc.teamcode.config.vision.Limelight;

@TeleOp(name="Auto Align Test")
public class autoAlign extends OpMode {

    private final Limelight limelight = new Limelight();
    private final MecanumDrive drive = new MecanumDrive();

    // --------------------------- PD Controller ---------------------------
    double kP = -0.0200;
    double error = 0;
    double lastError = 0;
    double angleTolerance = 0.2;
    double kD = -0.0005;
    double curTime = 0;
    double lastTime = 0;

    // --------------------------- Drive ---------------------------

    double forward, strafe, rotate;

    // --------------------------- controller based PD tuning ---------------------------

    double[] stepSizes = {0.1, 0.01,0.001, 0.0001};
    int stepIndex = 2;

    @Override
    public void init() {
        limelight.init(hardwareMap, telemetry);
        drive.init(hardwareMap);


        telemetry.addLine("Initialized");
    }

    public void start() {
        resetRuntime();
        curTime = getRuntime();
    }

    @Override
    public void loop() {
        // --------------------------- get mecanum drive inputs ---------------------------

        forward = gamepad1.left_stick_y;
        strafe = -gamepad1.left_stick_x;
        rotate = -gamepad1.right_stick_x;

        // --------------------------- get limelight input ---------------------------

        double Tx = limelight.getTx();

        // --------------------------- auto align rotation logic ---------------------------

        if (gamepad1.left_trigger > 0.3) {
            if (Tx != -999) {

                error = Tx;

                if (Math.abs(error) < angleTolerance) {
                    rotate = 0;
                } else {
                    double pTerm = error * kP;

                    curTime = getRuntime();
                    double dT = curTime - lastTime;

                    double dTerm = 0;

                    if (dT != 0) {
                        dTerm = ((error - lastError) / dT) * kD;
                    }

                    rotate = Range.clip(pTerm + dTerm, -0.4, 0.4);

                    lastError = error;
                    lastTime = curTime;
                }
            } else {
                lastTime = getRuntime();
                lastError = 0;

            }
        } else {
            lastTime = getRuntime();
            lastError = 0;
        }

        // drive motors
        drive.drive(forward,strafe,rotate);

        //update P and D on the fly
        // 'B' button cycles through different step sizes for tuning precision
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length; // Modulo wraps index to 0
        }

        if (gamepad1.dpadLeftWasPressed()) {
            kP -= stepSizes[stepIndex];
        }

        if (gamepad1.dpadRightWasPressed()) {
            kP += stepSizes[stepIndex];
        }

        if (gamepad1.dpadUpWasPressed()) {
            kD += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            kD -= stepSizes[stepIndex];
        }

        // --------------------------- telemetry ---------------------------
        if (Tx != -999) {
            if (gamepad1.left_trigger > 0.3) {
                telemetry.addLine("AUTO ALIGN");
            }
            telemetry.addData("Error", error);
        } else {
            telemetry.addLine("MANUAL Rotate Mode");
        }
        telemetry.addData("Tx",Tx);
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P","%.4f (D-Pad L/R)", kP);
        telemetry.addData("Tuning D","%.4f (D-Pad U/D)", kD);
        telemetry.addData("Step Sizes", "%.4f (B Button)", stepSizes[stepIndex]);
        telemetry.update();

    }
}
