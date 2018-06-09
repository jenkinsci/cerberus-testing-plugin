/**
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.launchcampaign.checkcampaign;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent object was return by cerberus to send status of campaign's
 * execution
 *
 * @author ndeblock
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultCIDto {

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private String result; // OK / KO

    @JsonProperty("ExecutionStart")
    // ex date format 1970-01-01 01:00:00.0
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT")
    private Date executionStart;

    @JsonProperty("ExecutionEnd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT")
    private Date executionEnd;

    @JsonProperty("messageType")
    private String messageType; // OK / KO

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("NonOK_prio1_nbOfExecution")
    private long nonOkPrio1;
    @JsonProperty("NonOK_prio2_nbOfExecution")
    private long nonOkPrio2;
    @JsonProperty("NonOK_prio3_nbOfExecution")
    private long nonOkPrio3;
    @JsonProperty("NonOK_prio4_nbOfExecution")
    private long nonOkPrio4;

    @JsonProperty("CI_OK_prio1")
    private double ciOkPrio1;
    @JsonProperty("CI_OK_prio2")
    private double ciOkPrio2;
    @JsonProperty("CI_OK_prio3")
    private double ciOkPrio3;
    @JsonProperty("CI_OK_prio4")
    private double ciOkPrio4;
    @JsonProperty("CI_finalResult")
    private double ciFinalResult;

    @JsonProperty("status_OK_nbOfExecution")
    private long statusOK;
    @JsonProperty("status_KO_nbOfExecution")
    private long statusKO;
    @JsonProperty("status_FA_nbOfExecution")
    private long statusFA;
    @JsonProperty("status_NA_nbOfExecution")
    private long statusNA;
    @JsonProperty("status_NE_nbOfExecution")
    private long statusNE;
    @JsonProperty("status_WE_nbOfExecution")
    private long statusWE;
    @JsonProperty("status_PE_nbOfExecution")
    private long statusPE;
    @JsonProperty("status_QU_nbOfExecution")
    private long statusQU;
    @JsonProperty("status_QE_nbOfExecution")
    private long statusQE;
    @JsonProperty("status_CA_nbOfExecution")
    private long statusCA;

    @JsonProperty("TOTAL_nbOfExecution")
    private long total;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getExecutionStart() {
        return (Date) executionStart.clone();
    }

    public Date getExecutionEnd() {
        return (Date) executionEnd.clone();
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getNonOkPrio1() {
        return nonOkPrio1;
    }

    public void setNonOkPrio1(long nonOkPrio1) {
        this.nonOkPrio1 = nonOkPrio1;
    }

    public long getNonOkPrio2() {
        return nonOkPrio2;
    }

    public void setNonOkPrio2(long nonOkPrio2) {
        this.nonOkPrio2 = nonOkPrio2;
    }

    public long getNonOkPrio3() {
        return nonOkPrio3;
    }

    public void setNonOkPrio3(long nonOkPrio3) {
        this.nonOkPrio3 = nonOkPrio3;
    }

    public long getNonOkPrio4() {
        return nonOkPrio4;
    }

    public void setNonOkPrio4(long nonOkPrio4) {
        this.nonOkPrio4 = nonOkPrio4;
    }

    public double getCiOkPrio1() {
        return ciOkPrio1;
    }

    public void setCiOkPrio1(double ciOkPrio1) {
        this.ciOkPrio1 = ciOkPrio1;
    }

    public double getCiOkPrio2() {
        return ciOkPrio2;
    }

    public void setCiOkPrio2(double ciOkPrio2) {
        this.ciOkPrio2 = ciOkPrio2;
    }

    public double getCiOkPrio3() {
        return ciOkPrio3;
    }

    public void setCiOkPrio3(double ciOkPrio3) {
        this.ciOkPrio3 = ciOkPrio3;
    }

    public double getCiOkPrio4() {
        return ciOkPrio4;
    }

    public void setCiOkPrio4(double ciOkPrio4) {
        this.ciOkPrio4 = ciOkPrio4;
    }

    public double getCiFinalResult() {
        return ciFinalResult;
    }

    public void setCiFinalResult(double ciFinalResult) {
        this.ciFinalResult = ciFinalResult;
    }

    public long getStatusCA() {
        return statusCA;
    }

    public void setStatusCA(long statusCA) {
        this.statusCA = statusCA;
    }

    public long getStatusFA() {
        return statusFA;
    }

    public void setStatusFA(long statusFA) {
        this.statusFA = statusFA;
    }

    public long getStatusPE() {
        return statusPE;
    }

    public void setStatusPE(long statusPE) {
        this.statusPE = statusPE;
    }

    public long getStatusNA() {
        return statusNA;
    }

    public void setStatusNA(long statusNA) {
        this.statusNA = statusNA;
    }

    public long getStatusOK() {
        return statusOK;
    }

    public void setStatusOK(long statusOK) {
        this.statusOK = statusOK;
    }

    public long getStatusKO() {
        return statusKO;
    }

    public void setStatusKO(long statusKO) {
        this.statusKO = statusKO;
    }

    public long getStatusNE() {
        return statusNE;
    }

    public void setStatusNE(long statusNE) {
        this.statusNE = statusNE;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getStatusQU() {
        return statusQU;
    }

    public void setStatusQU(long statusQU) {
        this.statusQU = statusQU;
    }

    public long getStatusWE() {
        return statusWE;
    }

    public void setStatusWE(long statusWE) {
        this.statusWE = statusWE;
    }

    public long getStatusQE() {
        return statusQE;
    }

    public void setStatusQE(long statusQE) {
        this.statusQE = statusQE;
    }

    // *******************   some calculated field  *****************************
    /**
     * @return The total of executed test (OK + CA + FA + NA + KO)
     */
    public long getTotalTestExecuted() {
        return this.getStatusOK() + this.getStatusKO() + this.getStatusFA() + this.getStatusNA() + this.getStatusNE() + this.getStatusQE() + this.getStatusCA();
    }

    /**
     * @return Total test waiting to execute (PE + NE)
     */
    public long getTestToExecute() {
        return this.getStatusWE() + this.getStatusPE() + this.getStatusQU();
    }

    /**
     * @return Percent of test already executed by cerberus
     */
    public int getPercentOfTestExecuted() {
        if (this.getTotal() == 0) {
            return 100;
        }

        return (int) ((this.getTotalTestExecuted() / (double) this.getTotal()) * 100d);
    }

    public String logDetailExecution() {
        return "Details : "
                + "OK " + this.getStatusOK()
                + " | KO " + this.getStatusKO()
                + " | FA " + this.getStatusFA()
                + " | NA " + this.getStatusNA()
                + " | NE " + this.getStatusNE()
                + " | WE " + this.getStatusWE()
                + " | PE " + this.getStatusPE()
                + " | QU " + this.getStatusQU()
                + " | QE " + this.getStatusQE()
                + " | CA " + this.getStatusCA();
    }

}
