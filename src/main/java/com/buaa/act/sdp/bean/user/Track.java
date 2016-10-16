package com.buaa.act.sdp.bean.user;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public class Track {
    @SerializedName("Development")
    private Development development;
    @SerializedName("Test Suites")
    private Development testSuites;
    @SerializedName("Assembly")
    private Development assembly;
    @SerializedName("Test Scenarios")
    private Development testScenarios;
    @SerializedName("Design")
    private Development design;
    @SerializedName("Specification")
    private Development specification;
    @SerializedName("Architecture")
    private Development architecture;
    @SerializedName("Content Creation")
    private Development contentCreation;
    @SerializedName("UI Prototypes")
    private Development uiPrototypes;
    @SerializedName("Conceptualization")
    private Development conceptualization;
    @SerializedName("First2Finish")
    private Development first2finish;
    @SerializedName("Copilot Posting")
    private Development copilotPosting;
    @SerializedName("Bug Hunt")
    private Development bugHunt;
    @SerializedName("RIA Build")
    private Development riaBuild;
    @SerializedName("Code")
    private Development code;

    public Development getDevelopment() {
        return development;
    }

    public void setDevelopment(Development development) {
        this.development = development;
    }

    public Development getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(Development testSuites) {
        this.testSuites = testSuites;
    }

    public Development getAssembly() {
        return assembly;
    }

    public void setAssembly(Development assembly) {
        this.assembly = assembly;
    }

    public Development getTestScenarios() {
        return testScenarios;
    }

    public void setTestScenarios(Development testScenarios) {
        this.testScenarios = testScenarios;
    }

    public Development getDesign() {
        return design;
    }

    public void setDesign(Development design) {
        this.design = design;
    }

    public Development getSpecification() {
        return specification;
    }

    public void setSpecification(Development specification) {
        this.specification = specification;
    }

    public Development getArchitecture() {
        return architecture;
    }

    public void setArchitecture(Development architecture) {
        this.architecture = architecture;
    }

    public Development getContentCreation() {
        return contentCreation;
    }

    public void setContentCreation(Development contentCreation) {
        this.contentCreation = contentCreation;
    }

    public Development getUiPrototypes() {
        return uiPrototypes;
    }

    public void setUiPrototypes(Development uiPrototypes) {
        this.uiPrototypes = uiPrototypes;
    }

    public Development getConceptualization() {
        return conceptualization;
    }

    public void setConceptualization(Development conceptualization) {
        this.conceptualization = conceptualization;
    }

    public Development getFirst2finish() {
        return first2finish;
    }

    public void setFirst2finish(Development first2finish) {
        this.first2finish = first2finish;
    }

    public Development getCopilotPosting() {
        return copilotPosting;
    }

    public void setCopilotPosting(Development copilotPosting) {
        this.copilotPosting = copilotPosting;
    }

    public Development getBugHunt() {
        return bugHunt;
    }

    public void setBugHunt(Development bugHunt) {
        this.bugHunt = bugHunt;
    }

    public Development getRiaBuild() {
        return riaBuild;
    }

    public void setRiaBuild(Development riaBuild) {
        this.riaBuild = riaBuild;
    }

    public Development getCode() {
        return code;
    }

    public void setCode(Development code) {
        this.code = code;
    }

    public List<Development> getAllTypeDevelopments(String userName){
        List<Development>list=new ArrayList<>();
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
