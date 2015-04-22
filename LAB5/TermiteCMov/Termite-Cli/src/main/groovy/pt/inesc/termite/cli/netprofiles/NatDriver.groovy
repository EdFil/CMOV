package pt.inesc.termite.cli.netprofiles

import pt.inesc.termite.cli.NetProfile
import pt.inesc.termite.cli.NetProfileDriver
import pt.inesc.termite.cli.exceptions.ConnectorTargetException

public class NatDriver extends NetProfileDriver {

    private String mUser
    private String mProxy
    private String mSubnet

    public NatDriver(NetProfile profile, Map config) {
        super(profile, config)
        // TODO - use NetProfileTargetException instead of Connector Target Exception.
        if(config == null) {
            throw new ConnectorTargetException("Must provide configuration paramenters.")
        }
        if(config['proxy-user'] == null) {
            throw new ConnectorTargetException("Must define 'proxy-user' config paramenter.")
        }
        if(config['proxy-server'] == null) {
            throw new ConnectorTargetException("Must define 'proxy-server' config paramenter")
        }
        if(config['target-subnet'] == null) {
            throw new ConnectorTargetException("Must define 'target-subnet' config paramenter.")
        }
        mUser = config['proxy-user']
        mProxy = config['proxy-server']
        mSubnet = config['target-subnet']

        try{ "sshuttle -h".execute() }
        catch(Exception ignored) { throw new ConnectorTargetException("sshuttle could not be found.") }
    }

    @Override
    void setup() {
        """pkill --pidfile /tmp/sshuttle-${mUser}.pid""".execute().waitFor()
        println "Connecting to NAT proxy: ${mUser}@${mProxy}"
        Process p = """sshuttle --daemon --pidfile /tmp/sshuttle-${mUser}.pid -r
                       | ${mUser}@${mProxy} ${mSubnet}""".stripMargin().execute()
        StringBuilder sout = new StringBuilder()
        StringBuilder serr = new StringBuilder()
        p.consumeProcessOutput(sout, serr)
        p.waitFor()
        if(serr.toString().length() > 0) {
            throw new ConnectorTargetException("Error using NAT netprofile: " + serr.toString())
        }
        println "Connected."
    }

    @Override
    void setdown() {
    }
}
