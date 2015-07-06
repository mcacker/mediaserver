package org.mobicents.media.core.endpoints;

import java.util.concurrent.atomic.AtomicInteger;

import org.mobicents.media.server.component.audio.AudioMixer;
import org.mobicents.media.server.component.audio.MixerComponent;
import org.mobicents.media.server.component.oob.OOBMixer;
import org.mobicents.media.server.concurrent.ConcurrentMap;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionType;
import org.mobicents.media.server.spi.RelayType;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.memory.Frame;

/**
 * 
 * @author hrosa
 *
 */
public class MixerEndpoint extends AbstractEndpoint {

	private final AudioMixer mixer;
	protected OOBMixer oobMixer;

	private final ConcurrentMap<MixerComponent> mixerComponents;

	private AtomicInteger loopbackCount = new AtomicInteger(0);
	private AtomicInteger readCount = new AtomicInteger(0);
	private AtomicInteger writeCount = new AtomicInteger(0);
	
	public MixerEndpoint(String localName) {
		super(localName, RelayType.MIXER);
		this.mixer = new AudioMixer(super.getScheduler());
		this.mixerComponents = new ConcurrentMap<MixerComponent>(6);
	}
	
	@Override
	public Connection createConnection(ConnectionType type, Boolean isLocal)
			throws ResourceUnavailableException {
		// Create connection
		Connection connection = super.createConnection(type, isLocal);

		// Create mixer component for the connection
		MixerComponent mixerComponent = new MixerComponent(connection.getId());
		mixerComponent.addAudioInput(connection.getAudioInput());
		mixerComponent.addAudioOutput(connection.getAudioOutput());
		mixerComponent.addOOBInput(connection.getDtfmInput());
		mixerComponent.addOOBOutput(connection.getDtmfOutput());
		this.mixerComponents.put(connection.getId(), mixerComponent);
		
		return connection;
	}
	
	@Override
	public void deleteConnection(Connection connection, ConnectionType connectionType) {
		super.deleteConnection(connection, connectionType);
		MixerComponent mixerComponent = this.mixerComponents.remove(connection.getId());
		mixer.release(mixerComponent.getAudioComponent());
		oobMixer.release(mixerComponent.getOOBComponent());
	}

	@Override
	public void modeUpdated(ConnectionMode oldMode, ConnectionMode newMode) {
		int readCount = 0;
		int loopbackCount = 0;
		int writeCount = 0;
		
		switch (oldMode) {
		case RECV_ONLY:
			readCount -= 1;
			break;
		case SEND_ONLY:
			writeCount -= 1;
			break;
		case SEND_RECV:
		case CONFERENCE:
			readCount -= 1;
			writeCount -= 1;
			break;
		case NETWORK_LOOPBACK:
			loopbackCount -= 1;
			break;
		default:
			// XXX handle default case
			break;
		}

		switch (newMode) {
		case RECV_ONLY:
			readCount += 1;
			break;
		case SEND_ONLY:
			writeCount += 1;
			break;
		case SEND_RECV:
		case CONFERENCE:
			readCount += 1;
			writeCount += 1;
			break;
		case NETWORK_LOOPBACK:
			loopbackCount += 1;
			break;
		default:
			// XXX handle default case
			break;
		}

		if (readCount != 0 || writeCount != 0 || loopbackCount != 0) {
			// something changed
			loopbackCount = this.loopbackCount.addAndGet(loopbackCount);
			readCount = this.readCount.addAndGet(readCount);
			writeCount = this.writeCount.addAndGet(writeCount);

			if (loopbackCount > 0 || readCount == 0 || writeCount == 0) {
				mixer.stop();
				oobMixer.stop();
			} else {
				mixer.start();
				oobMixer.start();
			}
		}
	}

	@Override
	public void processFrame(Connection sender, Frame frame) {
		MixerComponent mixerComponent = this.mixerComponents.get(sender.getId());
		mixerComponent.getAudioComponent().offer(new int[0]);
	}

}
