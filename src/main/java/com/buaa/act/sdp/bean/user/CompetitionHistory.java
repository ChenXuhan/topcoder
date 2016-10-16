package com.buaa.act.sdp.bean.user;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public class CompetitionHistory {
    @SerializedName("Development")
    private DevelopmentHistory development;
    @SerializedName("Test Suites")
    private DevelopmentHistory testSuites;
    @SerializedName("Assembly")
    private DevelopmentHistory assembly;
    @SerializedName("Test Scenarios")
    private DevelopmentHistory testScenarios;
    @SerializedName("Conceptualization")
    private DevelopmentHistory conceptualization;
    @SerializedName("First2Finish")
    private DevelopmentHistory first2finish;
    @SerializedName("Copilot Posting")
    private DevelopmentHistory copilotPosting;
    @SerializedName("Design")
    private DevelopmentHistory design;
    @SerializedName("Architecture")
    private DevelopmentHistory architecture;
    @SerializedName("UI Prototypes")
    private DevelopmentHistory uiPrototypes;
    @SerializedName("Bug Hunt")
    private DevelopmentHistory bugHunt;
    @SerializedName("Specification")
    private DevelopmentHistory specification;
    @SerializedName("Content Creation")
    private DevelopmentHistory contentCreation;
    @SerializedName("RIA Build")
    private DevelopmentHistory riaBuild;
    @SerializedName("Code")
    private DevelopmentHistory code;

    public DevelopmentHistory getDevelopment() {
        return development;
    }

    public void setDevelopment(DevelopmentHistory development) {
        this.development = development;
    }

    public DevelopmentHistory getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(DevelopmentHistory testSuites) {
        this.testSuites = testSuites;
    }

    public DevelopmentHistory getAssembly() {
        return assembly;
    }

    public void setAssembly(DevelopmentHistory assembly) {
        this.assembly = assembly;
    }

    public DevelopmentHistory getTestScenarios() {
        return testScenarios;
    }

    public void setTestScenarios(DevelopmentHistory testScenarios) {
        this.testScenarios = testScenarios;
    }

    public DevelopmentHistory getConceptualization() {
        return conceptualization;
    }

    public void setConceptualization(DevelopmentHistory conceptualization) {
        this.conceptualization = conceptualization;
    }

    public DevelopmentHistory getFirst2finish() {
        return first2finish;
    }

    public void setFirst2finish(DevelopmentHistory first2finish) {
        this.first2finish = first2finish;
    }

    public DevelopmentHistory getCopilotPosting() {
        return copilotPosting;
    }

    public void setCopilotPosting(DevelopmentHistory copilotPosting) {
        this.copilotPosting = copilotPosting;
    }

    public DevelopmentHistory getDesign() {
        return design;
    }

    public void setDesign(DevelopmentHistory design) {
        this.design = design;
    }

    public DevelopmentHistory getArchitecture() {
        return architecture;
    }

    public void setArchitecture(DevelopmentHistory architecture) {
        this.architecture = architecture;
    }

    public DevelopmentHistory getUiPrototypes() {
        return uiPrototypes;
    }

    public void setUiPrototypes(DevelopmentHistory uiPrototypes) {
        this.uiPrototypes = uiPrototypes;
    }

    public DevelopmentHistory getBugHunt() {
        return bugHunt;
    }

    public void setBugHunt(DevelopmentHistory bugHunt) {
        this.bugHunt = bugHunt;
    }

    public DevelopmentHistory getSpecification() {
        return specification;
    }

    public void setSpecification(DevelopmentHistory specification) {
        this.specification = specification;
    }

    public DevelopmentHistory getContentCreation() {
        return contentCreation;
    }

    public void setContentCreation(DevelopmentHistory contentCreation) {
        this.contentCreation = contentCreation;
    }

    public DevelopmentHistory getRiaBuild() {
        return riaBuild;
    }

    public void setRiaBuild(DevelopmentHistory riaBuild) {
        this.riaBuild = riaBuild;
    }

    public DevelopmentHistory getCode() {
        return code;
    }

    public void setCode(DevelopmentHistory code) {
        this.code = code;
    }

    public List<DevelopmentHistory> getAllDevelopmentHistory(String userName){
        List<DevelopmentHistory> list=new ArrayList<>();
        if(development!=null){
            development.setHandle(userName);
            development.setDevelopType("development");
            list.add(development);
        }
        if(testSuites!=null){
            testSuites.setHandle(userName);
            testSuites.setDevelopType("test suites");
            list.add(testSuites);
        }
        if(assembly!=null){
            assembly.setHandle(userName);
            assembly.setDevelopType("assembly");
            list.add(assembly);
        }
        if(testScenarios!=null){
            testScenarios.setHandle(userName);
            testScenarios.setDevelopType("test scenarios");
            list.add(testScenarios);
        }
        if(design!=null){
            design.setHandle(userName);
            design.setDevelopType("design");
            list.add(design);
        }
        if(specification!=null){
            specification.setHandle(userName);
            specification.setDevelopType("specification");
            list.add(specification);
        }
        if(architecture!=null){
            architecture.setHandle(userName);
            architecture.setDevelopType("architecture");
            list.add(architecture);
        }
        if(contentCreation!=null){
            contentCreation.setHandle(userName);
            contentCreation.setDevelopType("content creation");
            list.add(contentCreation);
        }
        if(uiPrototypes!=null){
            uiPrototypes.setHandle(userName);
            uiPrototypes.setDevelopType("ui prototypes");
            list.add(uiPrototypes);
        }
        if(conceptualization!=null){
            conceptualization.setHandle(userName);
            conceptualization.setDevelopType("conceptualization");
            list.add(conceptualization);
        }
        if(first2finish!=null){
            first2finish.setHandle(userName);
            first2finish.setDevelopType("first2finish");
            list.add(first2finish);
        }
        if(copilotPosting!=null){
            copilotPosting.setHandle(userName);
            copilotPosting.setDevelopType("copilot posting");
            list.add(copilotPosting);
        }
        if(bugHunt!=null){
            bugHunt.setHandle(userName);
            bugHunt.setDevelopType("bug hunt");
            list.add(bugHunt);
        }
        if(riaBuild!=null){
            riaBuild.setHandle(userName);
            riaBuild.setDevelopType("ria build");
            list.add(riaBuild);
        }
        if(code!=null){
            code.setHandle(userName);
            code.setDevelopType("code");
            list.add(code);
        }
        return list;
    }
}

