package org.firstinspires.ftc.teamcode.sampleTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "sampleLinearOpMode")
public class sampleLinearOpMode extends LinearOpMode {

    private DcMotor frontLeft;

    @Override
    public void runOpMode() {


        //Hardware mapping
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");


        //Motor directions
        //rightWheel.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);

        //brakes
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        telemetry.addData("Status", "Initialized");
        telemetry.update();


        waitForStart();


        while (opModeIsActive()) {
            if (gamepad1.a) {
                frontLeft.setPower(0.5);   //Move motor at 50% power
            } else {
                frontLeft.setPower(0);     //Stop motor
            }

            telemetry.addData("A Button", gamepad1.a);
            telemetry.addData("Motor Power", frontLeft.getPower());
            telemetry.update();
        }
    }
}