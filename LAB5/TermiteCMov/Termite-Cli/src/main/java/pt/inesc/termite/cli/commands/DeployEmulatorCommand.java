package pt.inesc.termite.cli.commands;


import pt.inesc.termite.cli.Command;
import pt.inesc.termite.cli.ConnectorDriver;
import pt.inesc.termite.cli.Context;

public class DeployEmulatorCommand extends Command {

    public DeployEmulatorCommand(String name, String abrv) {
        super(name,abrv);
    }

    public DeployEmulatorCommand() {
        super("deployemulator","demu");
    }

    public boolean executeCommand(Context context, String [] args) {

        assert context != null && args != null;

        if (args.length > 1) {
            printError("Wrong number of input arguments.");
            printHelp();
            return false;
        }

        if (context.mCurrentBackend == null) {
            printError("No backend is currently active.");
            return false;
        }

        ConnectorDriver ct = context.mCurrentBackend.getConnectorTarget();
        assert ct != null;

        try {
            if(args.length == 2) {
                String veid = args[1];
                String eid = ct.deployEmulator(veid);
                context.mCurrentEmulation.getVirtualIDs().put(veid, eid);
            }
            else {
                ct.deployEmulator();
            }
        } catch(Exception e) {
            printError("Could not deploy emulator.");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public void printHelp() {
//        System.out.println("Syntax: deployemulator|demu [<vid>]");
        System.out.println("Syntax: deployemulator|demu");
    }
}
