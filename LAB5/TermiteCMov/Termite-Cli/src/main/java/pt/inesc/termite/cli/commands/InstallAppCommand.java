package pt.inesc.termite.cli.commands;


import pt.inesc.termite.cli.Application;
import pt.inesc.termite.cli.Command;
import pt.inesc.termite.cli.ConnectorDriver;
import pt.inesc.termite.cli.Context;
import pt.inesc.termite.cli.Network;

public class InstallAppCommand extends Command {

    public InstallAppCommand(String name, String abrv) {
        super(name,abrv);
    }

    public InstallAppCommand() {
        super("installapp","-");
    }

    @SuppressWarnings("unchecked")
    public boolean executeCommand(Context context, String [] args) {

        assert context != null && args != null;

        if (args.length != 2) {
            printError("Wrong number of input arguments.");
            printHelp();
            return false;
        }

        if (context.mCurrentEmulation == null) {
            printError("No emulation is currently ongoing.");
            return false;
        }

        ConnectorDriver ct = context.mCurrentBackend.getConnectorTarget();
        assert ct != null;

        String appId = context.mCurrentEmulation.getExperiment().getApplication();
        Application app = context.mConfigManager.getApplications().get(appId);
        assert app != null;
        String apkPath = "" + context.mConfigManager.getTermitePath() + "/../" +
                app.getRPath() + "/" + app.getApk();
        String eid = context.mCurrentEmulation.getVirtualIDs().containsKey(args[1]) ?
                context.mCurrentEmulation.getVirtualIDs().get(args[1]) : args[1];

        try {
            ct.installApp(eid, apkPath);
        } catch(Exception e) {
            printError("Could not install application on emulator '" + eid + "'.");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public void printHelp() {
        System.out.println("Syntax: installapp <emu-id>");
    }
}