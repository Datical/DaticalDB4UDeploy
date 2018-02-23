import com.urbancode.air.CommandHelper;

final def inputPropsFile = new File(args[0])
final def outputPropsFile = new File(args[1])

final def props = new Properties()
try {
    props.load(new FileInputStream(inputPropsFile))
}
catch (IOException e) {
    throw new RuntimeException(e)
}

final def cwd = new File('.');
final def cmdHelper = new CommandHelper(cwd);



//--------------------------------------------------------------------------------------------------
def getAbsPath(def file) {
    def tempFile = null;
    if (file != null && file != "") {
        File temporaryFile = new File(file);
        tempFile = temporaryFile.getAbsolutePath();
    }
    return tempFile;
}
//path properties
def daticalDBCmd = getAbsPath(props['daticalDBCmd']);
def daticalDBUsername = props['daticalDBUsername'];
def daticalDBPassword = props['daticalDBPassword'];
def daticalDBDriversDir = getAbsPath(props['daticalDBDriversDir']);
def daticalDBProjectDir = getAbsPath(props['daticalDBProjectDir']);
def daticalProjectName = props['daticalProjectName'];
def daticalDBPipeline = props['daticalDBPipeline'];
def daticalDBAction = "status";
def daticalDBServer = props['daticalDBServer'];
def daticalServiceUsername = props['daticalServiceUsername'];
def daticalService = props['daticalService'];

// START building the CLI args.  Start with the pointer to hammer
def cmdArgs = [daticalDBCmd]; 

//Check for Datical Service Specific Properties nd BUild the Appropriate Command Line
if (daticalService && daticalServiceUsername) {
	cmdArgs << "--daticalServer=" + daticalService;
	cmdArgs << "--daticalUsername=" + daticalServiceUsername;
}

if (daticalProjectName){
	cmdArgs << "--projectKey=" + daticalProjectName;
}

// Add driver location and project directory
cmdArgs << '--drivers';
cmdArgs << daticalDBDriversDir;
cmdArgs << '--project';
cmdArgs << daticalDBProjectDir;

if (daticalDBUsername) {
	def usernameString = daticalDBServer + ":::" + daticalDBUsername;
	cmdArgs << "-un";
	cmdArgs << usernameString;
}

if (daticalDBPassword) {
	def passwordString = daticalDBServer + ":::" + daticalDBPassword;
	cmdArgs << "-pw";
	cmdArgs << passwordString;
}

if (daticalDBPipeline) {
	cmdArgs << "--pipeline";
	cmdArgs << daticalDBPipeline;
}

cmdArgs << daticalDBAction;

//Check for service to see if we're running against the service.
//If we are status the pipeline.  If not status the environment
if(daticalService && daticalProjectName) {
	cmdArgs << daticalProjectName;
} else {
	cmdArgs << daticalDBServer;
}

def daticalDBvm = props['daticalDBvm'];
if (daticalDBvm) {
	cmdArgs << "--vm";
	cmdArgs << daticalDBvm;
}
def daticalDBvmargs = props['daticalDBvmargs'];
if (daticalDBvmargs) {
	cmdArgs << "--vmargs";
	String[] myArray = daticalDBvmargs.split();
	for ( x in myArray ) {
		cmdArgs << x;
	}
	//cmdArgs << daticalDBvmargs;
}

int exitCode = cmdHelper.runCommand("Executing Datical DB", cmdArgs);

System.exit(exitCode);
