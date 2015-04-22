package pt.inesc.termite.cli.commands;


import pt.inesc.termite.cli.AddressSet;
import pt.inesc.termite.cli.Command;
import pt.inesc.termite.cli.ConnectorDriver;
import pt.inesc.termite.cli.Context;
import pt.inesc.termite.cli.NetProfile;

public class AssignAddrCommand extends Command {

    public AssignAddrCommand(String name, String abrv) {
        super(name,abrv);
    }

    public AssignAddrCommand() {
        super("assignaddr","-");
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

        AddressSet addressSet;
        String eid = context.mCurrentEmulation.getVirtualIDs().containsKey(args[1]) ?
                context.mCurrentEmulation.getVirtualIDs().get(args[1]) : args[1];
        try {
               addressSet = ct.assignAddressSet(eid);
        } catch(Exception e) {
            printError("Could not assign addresses.");
            System.out.println(e.getMessage());
            return false;
        }

        context.mCurrentEmulation.getAssignedAddresses().put(eid,addressSet);

        return true;
    }

    public void printHelp() {
        System.out.println("Syntax: assignaddr <emu-id>");
    }
}