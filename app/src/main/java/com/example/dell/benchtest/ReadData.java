package com.example.dell.benchtest;

import java.util.List;

/**
 * Created by dell on 2018/9/18.
 */

public class ReadData {

    public String getDevID() {
        return DevID;
    }

    public String getDevName() {
        return DevName;
    }

    public int getDevStatus() {
        return DevStatus;
    }

    public String getTestName() {
        return TestName;
    }

    public int getTestTime() {
        return TestTime;
    }

    public String getTestDescription() {
        return TestDescription;
    }

    public int getFaultID() {
        return FaultID;
    }

    public String getFaultDescription() {
        return FaultDescription;
    }

    public String getReserved1() {
        return Reserved1;
    }

    public String getReserved2() {
        return Reserved2;
    }

    public void setDevID(String devID) {
        DevID = devID;
    }

    public void setDevName(String devName) {
        DevName = devName;
    }

    public void setDevStatus(int devStatus) {
        DevStatus = devStatus;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public void setTestTime(int testTime) {
        TestTime = testTime;
    }

    public void setTestDescription(String testDescription) {
        TestDescription = testDescription;
    }

    public void setFaultID(int faultID) {
        FaultID = faultID;
    }

    public void setFaultDescription(String faultDescription) {
        FaultDescription = faultDescription;
    }

    public void setReserved1(String reserved1) {
        Reserved1 = reserved1;
    }

    public void setReserved2(String reserved2) {
        Reserved2 = reserved2;
    }

    private String DevID;
    private String DevName;
    private int DevStatus;
    private String TestName;
    private int TestTime;
    private String TestDescription;
    private int FaultID;
    private String FaultDescription;
    private String Reserved1;

    public void setDataArray(List<DataArrayBean> dataArray) {
        DataArray = dataArray;
    }

    private String Reserved2;

    public List<DataArrayBean> getDataArray() {
        return DataArray;
    }

    private List<DataArrayBean> DataArray;
    public static class DataArrayBean
    {
        public String getSensorName() {
            return SensorName;
        }

        public String getSensorValue() {
            return SensorValue;
        }

        public String getUnit() {
            return Unit;
        }

        public void setSensorName(String sensorName) {
            SensorName = sensorName;
        }

        public void setSensorValue(String sensorValue) {
            SensorValue = sensorValue;
        }

        public void setUnit(String unit) {
            Unit = unit;
        }

        private String SensorName;
        private String SensorValue;
        private String Unit;
    }
}

