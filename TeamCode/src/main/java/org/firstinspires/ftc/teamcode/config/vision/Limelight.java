package org.firstinspires.ftc.teamcode.config.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Limelight {

    private Limelight3A limelight3A;
    private Telemetry telemetry;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        telemetry.setMsTransmissionInterval(16);

        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.setPollRateHz(100);
        limelight3A.pipelineSwitch(0);
        limelight3A.start();
    }

    public void stop() {
        limelight3A.stop();
    }

    public double getTx() {
        LLResult llResult = limelight3A.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            return llResult.getTx();
        } else {
            return -999;
        }
    }

    public double getTy() {
        LLResult llResult = limelight3A.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            return llResult.getTy();
        } else {
            return -999;
        }
    }

    public double getTa() {
        LLResult llResult = limelight3A.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            return llResult.getTa();
        } else {
            return -999;
        }
    }


    public void displayTelemetry() {
        LLResult llResult = limelight3A.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            telemetry.addLine("Detected!");
            telemetry.addData("Tx", "%.2f", llResult.getTx());
            telemetry.addData("Ty", "%.2f", llResult.getTy());
            telemetry.addData("Ta", "%.2f", llResult.getTa());
            telemetry.update();
        } else {
            telemetry.addLine("Not Detected!");
        }
    }
}