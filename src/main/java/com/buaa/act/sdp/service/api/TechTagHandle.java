package com.buaa.act.sdp.service.api;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by YLT on 2017/4/20.
 */
@Component
public class TechTagHandle {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    public void test() {
        HashSet teches = new HashSet();
        for (int i = 0; i < Constant.TECHNOLOGIES.length; i++) {
            teches.add(Constant.TECHNOLOGIES[i].toLowerCase());
        }
        for (int i = 0; i < Constant.PLATFORMS.length; i++) {
            teches.add(Constant.PLATFORMS[i].toLowerCase());
        }
        System.out.println(teches.size());
        Iterator iter = teches.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
        System.out.println(Constant.TECHNOLOGIES.length + Constant.PLATFORMS.length);
        System.out.println(Constant.TECH.length);
    }

    public void getSkillsFromDatabase() {
        //32
        String[] strs1 = {"AWS",
                "Android",
                "Beanstalk",
                "Box",
                "Brivo Labs",
                "Cisco",
                "Cloud Foundry",
                "CloudFactor",
                "DocuSign",
                "EC2",
                "Facebook",
                "FinancialForce",
                "Force.com",
                "Gaming",
                "Google",
                "HP Haven",
                "HPE Haven OnDemand",
                "HTML",
                "Heroku",
                "IBM Bluemix",
                "Linux",
                "MESH01",
                "Microsoft Azure",
                "Mobile",
                "NodeJS",
                "Other",
                "Predix",
                "Salesforce.com",
                "Smartsheet",
                "Twilio",
                "Wordpress",
                "iOS"};
        //175
        String[] strs2 = {
                ".NET",
                ".NET System.Addins",
                "ADO.NET",
                "AI",
                "AJAX",
                "API",
                "ASP.NET",
                "ASP.NET AJAX",
                "ASP.NET Web Parts",
                "ActionScript",
                "Active Directory",
                "Android",
                "Angular.js",
                "Ant",
                "Apache Derby",
                "Apex",
                "Applet",
                "Backbone.js",
                "BizTalk",
                "Blackberry SDK",
                "Bootstrap",
                "C",
                "C#",
                "C++",
                "COBOL",
                "COM",
                "COM+",
                "CSS",
                "Castor",
                "Chatter",
                "Cisco",
                "ClickOnce",
                "CoffeeScript",
                "Cognitive",
                "Data Science",
                "Databasedotcom",
                "Docker",
                "Dojo",
                "Drools",
                "EJB",
                "Eclipse Plugin",
                "Elasticsearch",
                "Express",
                "Flash",
                "Flex",
                "Force.com Sites",
                "Fortran",
                "Go",
                "Google API",
                "Google App Engine",
                "Gradle",
                "Grommet",
                "Groovy",
                "HPE Haven OnDemand",
                "HTML",
                "HTML5",
                "HTTP",
                "Hibernate",
                "IBM AIX",
                "IBM COGNOS",
                "IBM Cognitive",
                "IBM Content Manager",
                "IBM DB2",
                "IBM Lotus Domino",
                "IBM Lotus Notes",
                "IBM PL/1",
                "IBM REXX",
                "IBM Rational Application Developer",
                "IBM Rational Data Architect",
                "IBM Rational Data Studio",
                "IBM Rational Software Architect",
                "IBM Rational Team Concert",
                "IBM Watson",
                "IBM WebSphere Application Server",
                "IBM WebSphere DataPower",
                "IBM WebSphere DataStage",
                "IBM WebSphere MQ",
                "IBM WebSphere Message Broker",
                "IBM WebSphere Portal",
                "IIS",
                "Illustrator",
                "Ionic",
                "J2EE",
                "J2ME",
                "JBoss Seam",
                "JDBC",
                "JFace",
                "JMS",
                "JPA",
                "JQuery",
                "JSF",
                "JSON",
                "JSP",
                "JUnit",
                "Jabber",
                "Java",
                "Java Application",
                "JavaBean",
                "JavaScript",
                "LDAP",
                "Lightning",
                "MIDP 2.0",
                "MSMQ",
                "Matlab",
                "Maven",
                "Microsoft SilverLight",
                "MongoDB",
                "MySQL",
                "NoSQL",
                "Node.js",
                "OSX",
                "Objective C",
                "Oracle 10g",
                "Oracle 9i",
                "Other",
                "PHP",
                "PL/SQL",
                "Perl",
                "PhoneGap",
                "Photoshop",
                "Play! Framework",
                "PostgreSQL",
                "Predix",
                "Python",
                "R",
                "REST",
                "ReactJS",
                "Redis",
                "Ruby",
                "Ruby on Rails",
                "SAP",
                "SFDC Mobile",
                "SQL",
                "SQL Server",
                "SSIS",
                "SWT",
                "Salesforce",
                "Sencha Touch 2",
                "Servlet",
                "Sharepoint 3.0",
                "Siebel",
                "Spark",
                "Spring",
                "Struts",
                "Swift",
                "Swing",
                "Titanium",
                "Twitter Bootstrap",
                "UML",
                "VB",
                "VB.NET",
                "Vertica",
                "Visualforce",
                "WPF",
                "Web Application",
                "Web Services",
                "WinForms Controls",
                "Windows Communication Foundation",
                "Windows Server",
                "Windows Workflow Foundation",
                "Word/Rich Text",
                "XAML",
                "XML",
                "XMPP",
                "XSL",
                "XUL",
                "Xcode",
                "iBATIS/MyBatis",
                "iOS",
                "iOS 5.0",
                "iOS 6.0",
                "iOS 8.0",
                "jQuery",
                "jQuery Mobile",
                "tvOS"};

        String[] tech = challengeItemDao.getAllTechnology();
        String[] plat = challengeItemDao.getAllPlatforms();
        HashSet techesDatabase = new HashSet();

        for (int i = 0; i < tech.length; i++) {
            if (tech[i] == null) {
                continue;
            }
            String[] strs = tech[i].split(",");
            for (int j = 0; j < strs.length; j++) {
                if (!strs[j].equals("")) {
                    techesDatabase.add(strs[j].toLowerCase());
                }
            }
        }
        for (int i = 0; i < plat.length; i++) {
            if (plat[i] == null) {
                continue;
            }
            String[] strs = plat[i].split(",");
            for (int j = 0; j < strs.length; j++) {
                if (!strs[j].equals("")) {
                    techesDatabase.add(strs[j].toLowerCase());
                }
            }
        }

        HashSet techesApi = new HashSet();
        for (int i = 0; i < strs1.length; i++) {
            techesApi.add(strs1[i].toLowerCase());
        }
        for (int i = 0; i < strs2.length; i++) {
            techesApi.add(strs2[i].toLowerCase());
        }

        //在api和数据库中同时出现的技能标签有181个
        //api中的技能标签有199个
        //数据库中的技能标签有200个
        /*int u =0;
        Iterator iter = techesDatabase.iterator();
        while (iter.hasNext()){
            String temp = (String) iter.next();
            if (techesApi.contains(temp)) {
                System.out.println(temp);
                u++;
            }
        }
        System.out.println(u);*/

        HashSet allTags = new HashSet();
        Iterator iter1 = techesDatabase.iterator();
        while (iter1.hasNext()) {
            allTags.add(iter1.next().toString());
        }
        iter1 = techesApi.iterator();
        while (iter1.hasNext()) {
            allTags.add(iter1.next().toString());
        }
        System.out.println(allTags.size());
        iter1 = allTags.iterator();
        ArrayList<String> result = new ArrayList<String>();
        while (iter1.hasNext()) {
            result.add(iter1.next().toString());
            //System.out.println(iter1.next().toString()+"\",");
        }
        Collections.sort(result);
        for (int i = 0; i < result.size(); i++) {
            System.out.print(result.get(i) + "\",\n\"");
        }
    }
}
