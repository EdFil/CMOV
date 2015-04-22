package pt.inesc.termite.cli

public abstract class NetProfileDriver implements INetProfileDriver {
    protected NetProfile mProfile;
    protected Map mConfig;

    public NetProfileDriver(NetProfile profile, Map config) {
        mProfile = profile
        mConfig = config
    }
}
