package pt.inesc.termite.cli.netprofiles

import pt.inesc.termite.cli.NetProfile
import pt.inesc.termite.cli.NetProfileDriver

class DirectDriver extends NetProfileDriver {

    DirectDriver(NetProfile profile, Map config) {
        super(profile, config)
    }

    @Override
    void setup() { }

    @Override
    void setdown() { }
}
