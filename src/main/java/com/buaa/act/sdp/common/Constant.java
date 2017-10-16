package com.buaa.act.sdp.common;

import java.util.HashSet;

/**
 * Created by yang on 2016/11/24.
 */
public class Constant {

    public static final int QUITTER=0;
    public static final int WINNER=2;
    public static final int SUBMITTER=1;

    public static final HashSet<String> PL;
    public static final String[] TECH = {
            ".net",
            ".net 2.0",
            ".net 3.0",
            ".net 3.5",
            ".net 4.0",
            ".net system.addins",
            "actionscript",
            "active directory",
            "activity diagrams (tcuml)",
            "ado.net",
            "ai",
            "ajax",
            "android",
            "android 2.0",
            "android 2.1",
            "android 2.2",
            "angular.js",
            "ant",
            "apache derby",
            "apex",
            "api",
            "applet",
            "asp.net",
            "asp.net ajax",
            "asp.net web parts",
            "aws",
            "backbone.js",
            "beanstalk",
            "biztalk",
            "blackberry sdk",
            "bootstrap",
            "box",
            "brivo labs",
            "c",
            "c#",
            "c++",
            "castor",
            "chatter",
            "cisco",
            "clickonce",
            "cloud foundry",
            "cloudfactor",
            "cobol",
            "coffeescript",
            "cognitive",
            "com",
            "com+",
            "commerce server 2009",
            "css",
            "custom tag",
            "data science",
            "databasedotcom",
            "docker",
            "docusign",
            "dojo",
            "drools",
            "ec2",
            "eclipse plugin",
            "ejb",
            "ejb 3",
            "elasticsearch",
            "express",
            "facebook",
            "financialforce",
            "flash",
            "flex",
            "force.com",
            "force.com sites",
            "fortran",
            "gaming",
            "go",
            "google",
            "google api",
            "google app engine",
            "gradle",
            "grommet",
            "groovy",
            "heroku",
            "hibernate",
            "hp haven",
            "hpe haven ondemand",
            "html",
            "html5",
            "http",
            "ibatis/mybatis",
            "ibm aix",
            "ibm bluemix",
            "ibm cognitive",
            "ibm cognos",
            "ibm content manager",
            "ibm db2",
            "ibm lotus domino",
            "ibm lotus notes",
            "ibm pl/1",
            "ibm rational application developer",
            "ibm rational data architect",
            "ibm rational data studio",
            "ibm rational software architect",
            "ibm rational team concert",
            "ibm rexx",
            "ibm watson",
            "ibm websphere application server",
            "ibm websphere datapower",
            "ibm websphere datastage",
            "ibm websphere message broker",
            "ibm websphere mq",
            "ibm websphere portal",
            "iis",
            "illustrator",
            "ionic",
            "ios",
            "ios 4.0",
            "ios 5.0",
            "ios 6.0",
            "ios 8.0",
            "j2ee",
            "j2me",
            "jabber",
            "java",
            "java application",
            "javabean",
            "javascript",
            "jboss seam",
            "jdbc",
            "jface",
            "jms",
            "jpa",
            "jquery",
            "jquery mobile",
            "jsf",
            "json",
            "jsp",
            "junit",
            "ldap",
            "lightning",
            "linux",
            "matlab",
            "maven",
            "mesh01",
            "microsoft azure",
            "microsoft silverlight",
            "midp 2.0",
            "mobile",
            "mongodb",
            "msmq",
            "mysql",
            "node.js",
            "nodejs",
            "nosql",
            "objective c",
            "oracle 10g",
            "oracle 9i",
            "osx",
            "other",
            "perl",
            "phonegap",
            "photoshop",
            "php",
            "pl/sql",
            "play! framework",
            "postgresql",
            "predix",
            "python",
            "r",
            "reactjs",
            "redis",
            "remoting",
            "rest",
            "rmi",
            "ruby",
            "ruby on rails",
            "salesforce",
            "salesforce.com",
            "sap",
            "sencha touch 2",
            "servlet",
            "sfdc mobile",
            "sharepoint 3.0",
            "siebel",
            "smartsheet",
            "spark",
            "spring",
            "sql",
            "sql server",
            "sql server 2000",
            "sql server 2008",
            "ssis",
            "struts",
            "swift",
            "swing",
            "swt",
            "titanium",
            "tvos",
            "twilio",
            "twitter bootstrap",
            "uml",
            "use case diagrams (tcuml)",
            "vb",
            "vb.net",
            "vertica",
            "visualforce",
            "web application",
            "web services",
            "windows communication foundation",
            "windows server",
            "windows server 2003",
            "windows workflow foundation",
            "winforms controls",
            "word/rich text",
            "wordpress",
            "wpf",
            "xaml",
            "xcode",
            "xml",
            "xmpp",
            "xsl",
            "xul"
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
    public static final String CLASSIFIER_DIRECTORY = "F:/arff/classifier/";
    public static final String CLUSTER_DIRECTORY = "F:/arff/cluster/";
    public static final String LOCAL_DIRECTORY = "F:/arff/local/";
    public static final String DCW_DS = "F:/arff/dcwds/";

    static {
        PL = new HashSet<String>();
        PL.add("actionscript");
        PL.add("c");
        PL.add("c#");
        PL.add("c++");
        PL.add("clojure");
        PL.add("coffeescript");
        PL.add("css");
        PL.add("go");
        PL.add("haskell");
        PL.add("html");
        PL.add("java");
        PL.add("javascript");
        PL.add("lua");
        PL.add("matlab");
        PL.add("objective-c");
        PL.add("perl");
        PL.add("php");
        PL.add("python");
        PL.add("r");
        PL.add("ruby");
        PL.add("scala");
        PL.add("shell");
        PL.add("swift");
        PL.add("tex");
        PL.add("vim script");
        PL.add("1c enterprise");
        PL.add("abap");
        PL.add("abnf");
        PL.add("ada");
        PL.add("agda");
        PL.add("ags script");
        PL.add("alloy");
        PL.add("alpine abuild");
        PL.add("ampl");
        PL.add("ant build system");
        PL.add("antlr");
        PL.add("apacheconf");
        PL.add("apex");
        PL.add("api blueprint");
        PL.add("apl");
        PL.add("apollo guidance computer");
        PL.add("applescript");
        PL.add("arc");
        PL.add("arduino");
        PL.add("asciidoc");
        PL.add("asn.1");
        PL.add("asp");
        PL.add("aspectj");
        PL.add("assembly");
        PL.add("ats");
        PL.add("augeas");
        PL.add("autohotkey");
        PL.add("autoit");
        PL.add("awk");
        PL.add("batchfile");
        PL.add("befunge");
        PL.add("bison");
        PL.add("bitbake");
        PL.add("blade");
        PL.add("blitzbasic");
        PL.add("blitzmax");
        PL.add("bluespec");
        PL.add("boo");
        PL.add("brainfuck");
        PL.add("brightscript");
        PL.add("bro");
        PL.add("c-objdump");
        PL.add("c2hs haskell");
        PL.add("cap&#39;n proto");
        PL.add("cartocss");
        PL.add("ceylon");
        PL.add("chapel");
        PL.add("charity");
        PL.add("chuck");
        PL.add("cirru");
        PL.add("clarion");
        PL.add("clean");
        PL.add("click");
        PL.add("clips");
        PL.add("closure templates");
        PL.add("cmake");
        PL.add("cobol");
        PL.add("coldfusion");
        PL.add("coldfusion cfc");
        PL.add("collada");
        PL.add("common lisp");
        PL.add("component pascal");
        PL.add("cool");
        PL.add("coq");
        PL.add("cpp-objdump");
        PL.add("creole");
        PL.add("crystal");
        PL.add("cson");
        PL.add("csound");
        PL.add("csound document");
        PL.add("csound score");
        PL.add("csv");
        PL.add("cuda");
        PL.add("cweb");
        PL.add("cycript");
        PL.add("cython");
        PL.add("d");
        PL.add("d-objdump");
        PL.add("darcs patch");
        PL.add("dart");
        PL.add("desktop");
        PL.add("diff");
        PL.add("digital command language");
        PL.add("dm");
        PL.add("dns zone");
        PL.add("dockerfile");
        PL.add("dogescript");
        PL.add("dtrace");
        PL.add("dylan");
        PL.add("e");
        PL.add("eagle");
        PL.add("ebnf");
        PL.add("ec");
        PL.add("ecere projects");
        PL.add("ecl");
        PL.add("eclipse");
        PL.add("edn");
        PL.add("eiffel");
        PL.add("ejs");
        PL.add("elixir");
        PL.add("elm");
        PL.add("emacs lisp");
        PL.add("emberscript");
        PL.add("eq");
        PL.add("erlang");
        PL.add("f#");
        PL.add("factor");
        PL.add("fancy");
        PL.add("fantom");
        PL.add("filebench wml");
        PL.add("filterscript");
        PL.add("fish");
        PL.add("flux");
        PL.add("formatted");
        PL.add("forth");
        PL.add("fortran");
        PL.add("freemarker");
        PL.add("frege");
        PL.add("g-code");
        PL.add("game maker language");
        PL.add("gams");
        PL.add("gap");
        PL.add("gcc machine description");
        PL.add("gdb");
        PL.add("gdscript");
        PL.add("genie");
        PL.add("genshi");
        PL.add("gentoo ebuild");
        PL.add("gentoo eclass");
        PL.add("gettext catalog");
        PL.add("gherkin");
        PL.add("glsl");
        PL.add("glyph");
        PL.add("gn");
        PL.add("gnuplot");
        PL.add("golo");
        PL.add("gosu");
        PL.add("grace");
        PL.add("gradle");
        PL.add("grammatical framework");
        PL.add("graph modeling language");
        PL.add("graphql");
        PL.add("graphviz (dot)");
        PL.add("groovy");
        PL.add("groovy server pages");
        PL.add("hack");
        PL.add("haml");
        PL.add("handlebars");
        PL.add("harbour");
        PL.add("haxe");
        PL.add("hcl");
        PL.add("hlsl");
        PL.add("html+django");
        PL.add("html+ecr");
        PL.add("html+eex");
        PL.add("html+erb");
        PL.add("html+php");
        PL.add("http");
        PL.add("hy");
        PL.add("hyphy");
        PL.add("idl");
        PL.add("idris");
        PL.add("igor pro");
        PL.add("inform 7");
        PL.add("ini");
        PL.add("inno setup");
        PL.add("io");
        PL.add("ioke");
        PL.add("irc log");
        PL.add("isabelle");
        PL.add("isabelle root");
        PL.add("j");
        PL.add("jasmin");
        PL.add("java server pages");
        PL.add("jflex");
        PL.add("jison");
        PL.add("jison lex");
        PL.add("jolie");
        PL.add("json");
        PL.add("json5");
        PL.add("jsoniq");
        PL.add("jsonld");
        PL.add("jsx");
        PL.add("julia");
        PL.add("jupyter notebook");
        PL.add("kicad");
        PL.add("kit");
        PL.add("kotlin");
        PL.add("krl");
        PL.add("labview");
        PL.add("lasso");
        PL.add("latte");
        PL.add("lean");
        PL.add("less");
        PL.add("lex");
        PL.add("lfe");
        PL.add("lilypond");
        PL.add("limbo");
        PL.add("linker script");
        PL.add("linux kernel module");
        PL.add("liquid");
        PL.add("literate agda");
        PL.add("literate coffeescript");
        PL.add("literate haskell");
        PL.add("livescript");
        PL.add("llvm");
        PL.add("logos");
        PL.add("logtalk");
        PL.add("lolcode");
        PL.add("lookml");
        PL.add("loomscript");
        PL.add("lsl");
        PL.add("m");
        PL.add("m4");
        PL.add("m4sugar");
        PL.add("makefile");
        PL.add("mako");
        PL.add("markdown");
        PL.add("marko");
        PL.add("mask");
        PL.add("mathematica");
        PL.add("maven pom");
        PL.add("max");
        PL.add("maxscript");
        PL.add("mediawiki");
        PL.add("mercury");
        PL.add("meson");
        PL.add("metal");
        PL.add("minid");
        PL.add("mirah");
        PL.add("modelica");
        PL.add("modula-2");
        PL.add("module management system");
        PL.add("monkey");
        PL.add("moocode");
        PL.add("moonscript");
        PL.add("mql4");
        PL.add("mql5");
        PL.add("mtml");
        PL.add("muf");
        PL.add("mupad");
        PL.add("myghty");
        PL.add("ncl");
        PL.add("nemerle");
        PL.add("nesc");
        PL.add("netlinx");
        PL.add("netlinx+erb");
        PL.add("netlogo");
        PL.add("newlisp");
        PL.add("nginx");
        PL.add("nim");
        PL.add("ninja");
        PL.add("nit");
        PL.add("nix");
        PL.add("nl");
        PL.add("nsis");
        PL.add("nu");
        PL.add("numpy");
        PL.add("objdump");
        PL.add("objective-c++");
        PL.add("objective-j");
        PL.add("ocaml");
        PL.add("omgrofl");
        PL.add("ooc");
        PL.add("opa");
        PL.add("opal");
        PL.add("opencl");
        PL.add("openedge abl");
        PL.add("openrc runscript");
        PL.add("openscad");
        PL.add("opentype feature file");
        PL.add("org");
        PL.add("ox");
        PL.add("oxygene");
        PL.add("oz");
        PL.add("p4");
        PL.add("pan");
        PL.add("papyrus");
        PL.add("parrot");
        PL.add("parrot assembly");
        PL.add("parrot internal representation");
        PL.add("pascal");
        PL.add("pawn");
        PL.add("pep8");
        PL.add("perl6");
        PL.add("pic");
        PL.add("pickle");
        PL.add("picolisp");
        PL.add("piglatin");
        PL.add("pike");
        PL.add("plpgsql");
        PL.add("plsql");
        PL.add("pod");
        PL.add("pogoscript");
        PL.add("pony");
        PL.add("postscript");
        PL.add("pov-ray sdl");
        PL.add("powerbuilder");
        PL.add("powershell");
        PL.add("processing");
        PL.add("prolog");
        PL.add("propeller spin");
        PL.add("protocol buffer");
        PL.add("public key");
        PL.add("pug");
        PL.add("puppet");
        PL.add("pure data");
        PL.add("purebasic");
        PL.add("purescript");
        PL.add("python console");
        PL.add("python traceback");
        PL.add("qmake");
        PL.add("qml");
        PL.add("racket");
        PL.add("ragel");
        PL.add("raml");
        PL.add("rascal");
        PL.add("raw token data");
        PL.add("rdoc");
        PL.add("realbasic");
        PL.add("reason");
        PL.add("rebol");
        PL.add("red");
        PL.add("redcode");
        PL.add("regular expression");
        PL.add("ren&#39;py");
        PL.add("renderscript");
        PL.add("restructuredtext");
        PL.add("rexx");
        PL.add("rhtml");
        PL.add("rmarkdown");
        PL.add("robotframework");
        PL.add("roff");
        PL.add("rouge");
        PL.add("rpm spec");
        PL.add("runoff");
        PL.add("rust");
        PL.add("sage");
        PL.add("saltstack");
        PL.add("sas");
        PL.add("sass");
        PL.add("scaml");
        PL.add("scheme");
        PL.add("scilab");
        PL.add("scss");
        PL.add("self");
        PL.add("shaderlab");
        PL.add("shellsession");
        PL.add("shen");
        PL.add("slash");
        PL.add("slim");
        PL.add("smali");
        PL.add("smalltalk");
        PL.add("smarty");
        PL.add("smt");
        PL.add("sourcepawn");
        PL.add("sparql");
        PL.add("spline font database");
        PL.add("sqf");
        PL.add("sql");
        PL.add("sqlpl");
        PL.add("squirrel");
        PL.add("srecode template");
        PL.add("stan");
        PL.add("standard ml");
        PL.add("stata");
        PL.add("ston");
        PL.add("stylus");
        PL.add("sublime text config");
        PL.add("subrip text");
        PL.add("supercollider");
        PL.add("svg");
        PL.add("systemverilog");
        PL.add("tcl");
        PL.add("tcsh");
        PL.add("tea");
        PL.add("terra");
        PL.add("text");
        PL.add("textile");
        PL.add("thrift");
        PL.add("ti program");
        PL.add("tla");
        PL.add("toml");
        PL.add("turing");
        PL.add("turtle");
        PL.add("twig");
        PL.add("txl");
        PL.add("typescript");
        PL.add("unified parallel c");
        PL.add("unity3d asset");
        PL.add("unix assembly");
        PL.add("uno");
        PL.add("unrealscript");
        PL.add("urweb");
        PL.add("vala");
        PL.add("vcl");
        PL.add("verilog");
        PL.add("vhdl");
        PL.add("visual basic");
        PL.add("volt");
        PL.add("vue");
        PL.add("wavefront material");
        PL.add("wavefront object");
        PL.add("web ontology language");
        PL.add("webidl");
        PL.add("wisp");
        PL.add("world of warcraft addon data");
        PL.add("x10");
        PL.add("xbase");
        PL.add("xc");
        PL.add("xcompose");
        PL.add("xml");
        PL.add("xojo");
        PL.add("xpages");
        PL.add("xproc");
        PL.add("xquery");
        PL.add("xs");
        PL.add("xslt");
        PL.add("xtend");
        PL.add("yacc");
        PL.add("yaml");
        PL.add("yang");
        PL.add("zephir");
        PL.add("zimpl");
    }
}
