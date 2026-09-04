package org.firstinspires.ftc.teamcode.opmode.intake;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Outtake PIDF Tuner (With Intake)")
public class intakeTuner extends OpMode {

    // ---------------- Hardware ----------------
    private DcMotorEx outtake;
    private DcMotorEx intake;
    private DcMotorEx stage;

    // ---------------- Constants ----------------
    private static final double TICKS_PER_REV = 28.0;

    private double idleRPM = 1100;
    private double minRPM = 5500;
    private double targetRPM = 6000;
    private double currentTargetRPM = idleRPM;

    // PIDF
    private double P = 0.0;
    private double F = 0;

    // Step sizes
    private final double[] steps = {10, 1, 0.1, 0.01, 0.001};
    private int stepIndex = 1;

    @Override
    public void init() {
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        stage = hardwareMap.get(DcMotorEx.class, "stage");

        outtake.setDirection(DcMotorEx.Direction.REVERSE);
        outtake.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        outtake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        intake.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        stage.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        applyPIDF();
        setVelocityRPM(idleRPM);
    }

    @Override
    public void loop() {

        // ---------------- Shooter RPM selection ----------------
        if (gamepad1.aWasPressed()) currentTargetRPM = idleRPM;
        if (gamepad1.yWasPressed()) currentTargetRPM = targetRPM;

        setVelocityRPM(currentTargetRPM);

        // ---------------- PIDF tuning ----------------
        if (gamepad1.rightBumperWasPressed()) stepIndex = (stepIndex + 1) % steps.length;
        if (gamepad1.leftBumperWasPressed()) stepIndex = (stepIndex - 1 + steps.length) % steps.length;

        if (gamepad1.dpadUpWasPressed()) P += steps[stepIndex];
        if (gamepad1.dpadDownWasPressed()) P -= steps[stepIndex];
        if (gamepad1.dpadRightWasPressed()) F += steps[stepIndex];
        if (gamepad1.dpadLeftWasPressed()) F -= steps[stepIndex];

        applyPIDF();

        // ---------------- Intake / Stage control ----------------
        double intakePower = 0.0;
        double stagePower = 0.0;

        if (gamepad1.right_trigger > 0.05) {
            // Full feed
            intakePower = 1.0;
            stagePower = 1.0;
        }
        else if (gamepad1.left_trigger > 0.05) {
            // Stage only (single ring feed)
            intakePower = 0.0;
            stagePower = 1.0;
        }

        intake.setPower(intakePower);
        stage.setPower(stagePower);

        // ---------------- Telemetry ----------------
        double currentRPM = outtake.getVelocity() * 60.0 / TICKS_PER_REV;
        double error = currentTargetRPM - currentRPM;

        telemetry.addData("Target RPM", currentTargetRPM);
        telemetry.addData("Current RPM", "%.1f", currentRPM);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addLine("-----------------------");
        telemetry.addData("P", "%.5f", P);
        telemetry.addData("F", "%.5f", F);
        telemetry.addData("Step", steps[stepIndex]);
        telemetry.addLine("-----------------------");
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Stage Power", stagePower);
        telemetry.update();
    }

    // ---------------- Helpers ----------------
    private void setVelocityRPM(double rpm) {
        double ticksPerSecond = rpm * TICKS_PER_REV / 60.0;
        outtake.setVelocity(ticksPerSecond);
    }

    private void applyPIDF() {
        outtake.setPIDFCoefficients(
                DcMotorEx.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(P, 0, 0, F)
        );
    }
}