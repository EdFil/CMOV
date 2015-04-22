package pt.inesc.termite.cli.commands;


import pt.inesc.termite.cli.Command;
import pt.inesc.termite.cli.ConnectorDriver;
import pt.inesc.termite.cli.Context;
import pt.inesc.termite.cli.Network;

public class KillEmulatorCommand extends Command {

    public KillEmulatorCommand(String name, String abrv) {
        super(name,abrv);
    }

    public KillEmulatorCommand() {
        super("killemulator","kemu");
    }

    public boolean executeCommand(Context context, String [] args) {

        assert context != null && args != null;

        if (args.length != 2) {
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
        String eid = context.mCurrentEmulation.getVirtualIDs().containsKey(args[1]) ?
                context.mCurrentEmulation.getVirtualIDs().get(args[1]) : args[1];

        try {
            ct.killEmulator(eid);
        } catch(Exception e) {
            printError("Could not kill emulator '" + eid + "'.");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public void printHelp() {
        System.out.println("Syntax: killemulator|kemu <emulator-id>");
    }
}
