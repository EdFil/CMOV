package pt.inesc.termite.cli.commands;


import pt.inesc.termite.cli.AddressSet;
import pt.inesc.termite.cli.Command;
import pt.inesc.termite.cli.ConnectorDriver;
import pt.inesc.termite.cli.Context;

public class UnassignAddrCommand extends Command {

    public UnassignAddrCommand(String name, String abrv) {
        super(name,abrv);
    }

    public UnassignAddrCommand() {
        super("unassignaddr","-");
    }

    @SuppressWarnings("unchecked")
    public boolean executeCommand(Context context, String [] args) {

        assert context != null && args != null;

        if (args.length != 2) {
            printError("Wrong input arguments.");
            return false;
        }

        if (context.mCurrentEmulation == null) {
            printError("No emulation is currently active.");
            return false;
        }

        ConnectorDriver ct = context.mCurrentBackend.getConnectorTarget();
        assert ct != null;
        String eid = context.mCurrentEmulation.getVirtualIDs().containsKey(args[1]) ?
                context.mCurrentEmulation.getVirtualIDs().get(args[1]) : args[1];

        try {
            ct.unassignAddressSet(eid);
        } catch(Exception e) {
            printError("Could not unassign addresses.");
            System.out.println(e.getMessage());
            return false;
        }
        context.mCurrentEmulation.getAssignedAddresses().remove(eid);

        return true;
    }

    public void printHelp() {
        System.out.println("Syntax: unassignaddr <emu-id>");
    }
}