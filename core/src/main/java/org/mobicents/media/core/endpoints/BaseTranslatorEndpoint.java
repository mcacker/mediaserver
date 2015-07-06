package org.mobicents.media.core.endpoints;

import org.mobicents.media.server.impl.rtp.relay.RtpTranslator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * Creates a generic endpoint that relies on a RTP Translator to forward packets
 * to all identified sources in the call.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class BaseTranslatorEndpoint extends AbstractEndpoint {

	private final RtpTranslator translator;

	public BaseTranslatorEndpoint(String localName) {
		super(localName);
		this.translator = new RtpTranslator();
	}

	@Override
	public Connection createConnection(ConnectionType type, Boolean isLocal)
			throws ResourceUnavailableException {
		Connection connection = super.createConnection(type, isLocal);

		if (ConnectionType.RTP.equals(type)) {
			// TODO: register remote connection to the list of receivers
		}

		return connection;
	}

	@Override
	public void deleteConnection(Connection connection,
			ConnectionType connectionType) {
		super.deleteConnection(connection, connectionType);
		if (ConnectionType.RTP.equals(connectionType)) {
			// TODO: de-register remote connection from the list of receivers
		}
	}

	@Override
	public void modeUpdated(ConnectionMode oldMode, ConnectionMode newMode) {
		int readCount = 0;
		int loopbackCount = 0;
		int writeCount = 0;

		switch (oldMode) {
		case RECV_ONLY:
			readCount--;
			break;
		case SEND_ONLY:
			writeCount--;
			break;
		case SEND_RECV:
		case CONFERENCE:
			readCount--;
			writeCount--;
			break;
		case NETWORK_LOOPBACK:
			loopbackCount--;
			break;
		default:
			break;
		}

		switch (newMode) {
		case RECV_ONLY:
			readCount++;
			break;
		case SEND_ONLY:
			writeCount++;
			break;
		case SEND_RECV:
		case CONFERENCE:
			readCount++;
			writeCount++;
			break;
		case NETWORK_LOOPBACK:
			loopbackCount++;
			break;
		default:
			break;
		}

		if (readCount != 0 || writeCount != 0 || loopbackCount != 0) {
			// something changed
			loopbackCount = this.loopbackCount.addAndGet(loopbackCount);
			readCount = this.readCount.addAndGet(readCount);
			writeCount = this.writeCount.addAndGet(writeCount);

			if (loopbackCount > 0 || readCount == 0 || writeCount == 0) {
				audioMixer.stop();
				oobMixer.stop();
			} else {
				audioMixer.start();
				oobMixer.start();
			}
		}

	}

}
