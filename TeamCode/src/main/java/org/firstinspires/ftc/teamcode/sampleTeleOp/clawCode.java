package org.firstinspires.ftc.teamcode.sampleTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "clawCode v1")
public class clawCode extends LinearOpMode {

    private Servo claw;

    @Override
    public void runOpMode() {

        claw = hardwareMap.get(Servo.class, "claw");
        claw.setDirection(Servo.Direction.FORWARD);


        telemetry.addData("Status", "Initialized");
        telemetry.update();


        waitForStart();

//
      //  position should increment as the power of the trigger increases
        double servoPosition = 0.5;

        while (opModeIsActive()) {


            if (gamepad1.a) {
                servoPosition += 0.02;
            } else if (gamepad1.b) {
                servoPosition -= 0.02;
            }

            claw.setPosition(servoPosition);


            telemetry.addData("A Button", gamepad1.a);
            telemetry.addData("B Button", gamepad1.b);

            telemetry.addData("Claw Position", claw.getPosition());
            telemetry.update();
        }
    }
}