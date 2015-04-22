package pt.inesc.termite.cli;

abstract public class ConnectorDriver implements IConnectorDriver {

    protected Connector mConnector;
    protected Map mConfig;

    public ConnectorDriver(Connector connector, Map config) {
        mConnector = connector
        mConfig = config
    }
}
