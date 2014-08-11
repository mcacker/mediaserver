package org.mobicents.media.server.io.network.channel;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Represents a channel where data can be exchanged
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public interface Channel {

	/**
	 * Receive data through the channel
	 * 
	 * @throws IOException
	 */
	void receive() throws IOException;

	/**
	 * Send data through the channel
	 * 
	 * @throws IOException
	 */
	void send() throws IOException;

	/**
	 * Gets whether the channel has pending data to be written
	 * 
	 * @return whether the channel has pending data
	 */
	boolean hasPendingData();

	/**
	 * Gets whether channel is connected or not.
	 * 
	 * @return <code>true</code> if the channel is connected. Otherwise, returns
	 *         <code>false</code>.
	 */
	boolean isConnected();

	/**
	 * Connects the channel
	 * 
	 * @param address
	 *            The address of the remote peer to connect to
	 * @throws IOException
	 */
	void connect(SocketAddress address) throws IOException;

	/**
	 * Disconnect the channel
	 * 
	 * @throws IOException
	 */
	void disconnect() throws IOException;

	/**
	 * Close the channel
	 */
	void close();

}