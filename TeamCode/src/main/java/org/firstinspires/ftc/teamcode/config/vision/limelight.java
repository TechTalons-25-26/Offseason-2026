package org.firstinspires.ftc.teamcode.config.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class limelight extends OpMode {

    private Limelight3A limelight3A;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(limelight3A.getClass(), "limelight");
        limelight3A.pipelineSwitch(1);
    }

    @Override
    public void start() {
        limelight3A.start();
    }

    @Override
    public void stop() {
        limelight3A.stop();
    }

    @Override
    public void loop() {
        LLResult llResult = limelight3A.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            telemetry.addData("X Offset", llResult.getTx());
            telemetry.addData("Y Offset", llResult.getTy());
            telemetry.addData("Area Offset", llResult.getTa());
        }
    }
}
