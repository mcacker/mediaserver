package org.mobicents.media.core.ice;

import java.net.InetAddress;

import org.mobicents.media.core.ice.candidate.IceCandidate;

/**
 * A candidate obtained by binding to a specific port from an IP address on the
 * host.
 * <p>
 * This includes IP addresses on physical interfaces and logical ones, such as
 * ones obtained through Virtual Private Networks (VPNs) and Realm Specific IP
 * (RSIP) [<a href="http://tools.ietf.org/html/rfc3102">RFC3102</a>] (which
 * lives at the operating system level).
 * </p>
 * 
 * @author Henrique Rosa
 * 
 */
public class HostCandidate extends IceCandidate {

	private static final long serialVersionUID = 1321731383535837192L;

	public HostCandidate(InetAddress address, int port) {
		super(address, port, CandidateType.HOST);
		// A host candidate is also said to have a base, equal to itself.
		this.base = this;
	}

	public HostCandidate(String hostname, int port) {
		super(hostname, port, CandidateType.HOST);
		// A host candidate is also said to have a base, equal to itself.
		this.base = this;
	}
}