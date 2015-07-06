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
package org.mobicents.media.server.impl.rtp.relay;

import java.util.ArrayList;
import java.util.List;

import org.mobicents.media.server.impl.rtp.RtpChannel;
import org.mobicents.media.server.impl.rtp.RtpPacket;

/**
 * An RTP translator connects two or more transport-level clouds by forwarding
 * RTP packets with their SSRC identifier intact.
 * 
 * <p>
 * This makes it possible for receivers to identify individual sources even
 * though packets from all the sources pass through the same translator and
 * carry the translator's network source address.
 * </p>
 * 
 * <p>
 * Some kinds of translators will pass through the data untouched, but others
 * MAY change the encoding of the data and thus the RTP data payload type and
 * timestamp. If multiple data packets are re-encoded into one, or vice versa, a
 * translator MUST assign new sequence numbers to the outgoing packets.<br>
 * Losses in the incoming packet stream may induce corresponding gaps in the
 * outgoing sequence numbers.
 * </p>
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * @see <a href="http://tools.ietf.org/html/rfc3550#section-7">RFC 3550</a>
 */
public class RtpTranslator {

	private final List<RtpChannel> receivers;

	public RtpTranslator() {
		this.receivers = new ArrayList<RtpChannel>(5);
	}

	/**
	 * Register an RTP channel to forward packets to.
	 * 
	 * @param channel
	 *            The channel to forward packets to.
	 */
	public void registerChannel(RtpChannel channel) {
		synchronized (this.receivers) {
			this.receivers.add(channel);
		}
	}

	/**
	 * Deregister an RTP channel from the list of receivers.
	 * 
	 * @param channel
	 *            The channel to stop forwarding packets to.
	 */
	public void deregisterChannel(RtpChannel channel) {
		synchronized (this.receivers) {
			this.receivers.remove(receivers);
		}
	}

	/**
	 * Forwards a packets across the list of registered RTP channels.<br>
	 * The packet will be translated according to the configuration of the
	 * remote channel (transcoding, etc).
	 * 
	 * @param packet
	 *            The packet to be forwarded to remote peers.
	 */
	public void translate(RtpPacket packet) {
		synchronized (receivers) {
			for (RtpChannel receiver : receivers) {
				// TODO: transcoding if needed
				receiver.getTransmitter().forward(packet);
			}
		}
	}

}
