package com.buaa.act.sdp.topcoder.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yang on 2016/11/24.
 */
public class Constant {

    /**
     * ESEM 开发者的角色
     */
    public static final int QUITTER = 0;
    public static final int SUBMITTER = 1;
    public static final int WINNER = 2;

    public static final Set TASK_TYPE = new HashSet<String>() {
        {
            add("Code");
            add("First2Finish");
            add("Assembly Competition");
        }
    };

    public static final String[] TECHNOLOGIES = {
            ".NET",
            ".NET System.Addins",
            "ADO.NET",
            "AJAX",
            "API",
            "ASP.NET",
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
            "CSS",
            "Castor",
            "Chatter",
            "Cisco",
            "ClickOnce",
            "CoffeeScript",
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
            "RMI",
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
            "jQuery",
            "tvOS"};

    public static final String[] LANGUAGES = {
            "Java", "C", "C#", "C++", "Python", "Go", "HTML", "HTML5", "R", "Ruby", "Perl", "PHP", "JavaScript", "Swift", "VB", "Objective C",
            "SQL", "Matlab", "Fortran", "ActionScript"
    };

    public static final String[] PLATFORMS = {"AWS",
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
            "Predix",
            "Salesforce.com",
            "Smartsheet",
            "Twilio",
            "Wordpress",
            "iOS"};

    /**
     * team 推荐启发式算法迭代次数
     */
    public static final int ITERATIONS = 1000;

    public static final int PAGE_SIZE = 50;

    public static final int RETRY_TIMES = 10;

    public static final double TASK_SIMILARITY = 0.8;

    public static final int HTTP_SUCCESS = 200;

    public static final int YEAR = 366;

    public static final int MAX_TASK_ID = 80000000;

    /**
     * 返回码及描述
     */
    public static final int TC_SUCCESS = 200;
    public static final int TC_MSG_MISS = 300;
    public static final int TC_NOT_SUPPORT = 400;
    public static final int TC_NOT_FOUND = 404;
    public static final int TC_INNER_ERROR = 500;

    public static final String TC_SUCCESS_DES = "请求成功";
    public static final String TC_MSG_MISS_DES = "任务信息不完整";
    public static final String TC_NOT_SUPPORT_DES = "不支持该任务类型";
    public static final String TC_NOT_FOUND_DES = "请求资源不存在";
    public static final String TC_INNER_ERROR_DES = "服务内部错误";
}
