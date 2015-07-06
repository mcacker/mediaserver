/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */
package org.mobicents.media.server.component.audio;

import org.mobicents.media.server.component.oob.OOBComponent;
import org.mobicents.media.server.component.oob.OOBInput;
import org.mobicents.media.server.component.oob.OOBOutput;

/**
 * Encapsulates the mixer components of a given connection.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class MixerComponent {
	
	private final int connectionId;
	private final AudioComponent audioComponent;
	private final OOBComponent ooBComponent;

	public MixerComponent(int connectionId) {
		this.connectionId = connectionId;
		this.audioComponent = new AudioComponent(connectionId);
		this.ooBComponent = new OOBComponent(connectionId);
	}

	public int getConnectionId() {
		return connectionId;
	}

	public AudioComponent getAudioComponent() {
		return audioComponent;
	}
	
	public void addAudioInput(AudioInput input) {
		this.audioComponent.addInput(input);
	}

	public void addAudioOutput(AudioOutput output) {
		this.audioComponent.addOutput(output);
	}

	public OOBComponent getOOBComponent() {
		return ooBComponent;
	}
	
	public void addOOBInput(OOBInput input) {
		this.ooBComponent.addInput(input);
	}

	public void addAudioOutput(OOBOutput output) {
		this.ooBComponent.addOutput(output);
	}
}
