package org.firstinspires.ftc.teamcode.sampleTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "sampleOpMode")
public class sampleOpMode extends OpMode {

    private DcMotor frontLeft;

    @Override
    public void init() {

        // Hardware mapping
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");

        // Motor direction
        frontLeft.setDirection(DcMotor.Direction.FORWARD);

        // Brake when power is 0
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {

        if (gamepad1.a) {
            frontLeft.setPower(0.5);   // Move motor at 50% power
        } else {
            frontLeft.setPower(0);     // Stop motor
        }

        telemetry.addData("A Button", gamepad1.a);
        telemetry.addData("Motor Power", frontLeft.getPower());
        telemetry.update();
    }
}