package org.firstinspires.ftc.teamcode.opmode.intake; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static com.pedropathing.ivy.Scheduler.*;
import static com.pedropathing.ivy.pedro.PedroCommands.*;
import static com.pedropathing.ivy.groups.Groups.*;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.config.pedroPathing.Constants;

@Autonomous(name = "testing intake command yay...", group = "Examples")
public class testingIntake extends LinearOpMode {

    //private Follower follower;

    //defining our PathChains
    //private PathChain mainPath1, mainPath2;

    private DcMotor stage1;

    double power = 0;

    private Command raiseArm;

    public Command autoRoutine() {
        return sequential(
                raiseArm
        );
    }



    @Override
    public void runOpMode() {

        //SCHEDULER
        Scheduler.reset();
        stage1 = hardwareMap.get(DcMotor.class, "stage1");
        stage1.setDirection(DcMotor.Direction.FORWARD);

        stage1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        stage1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        stage1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        stage1.setPower(0);


         raiseArm = Command.build()
                .setExecute(() -> stage1.setPower(0.7))
                .setDone(() -> false)
                .setEnd(endCondition -> stage1.setPower(0))
                .requiring(stage1);




        waitForStart();
        //We schedule all our commands when we start the OpMode
       //
        //stage1.setPower(0.3);

        //SCHEDULER
       schedule(autoRoutine());
        while (opModeIsActive()) {
            Scheduler.execute();

            telemetry.addData("Motor Power", stage1.getPower());
            telemetry.addData("Encoder Position", stage1.getCurrentPosition());
            telemetry.update();
        }
        //stage1.setPower(0);
    }



}