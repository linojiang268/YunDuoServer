package frame.ydserver.vpn;

public enum VpnType {
	PPTP("PPTP", "VPN\"PPTP\"", "Point-to-Point Tunneling Protocol", PptpProfile.class);

	private String name;
	private Class<? extends VpnProfile> clazz;
	private boolean active;
	// private int descRid;
	// private int nameRid;
	private String descStr;
	private String nameStr;

	// VpnType(final String name, final int nameRid, final int descRid, final
	// Class<? extends VpnProfile> clazz) {
	// this.name = name;
	// this.nameRid = nameRid;
	// this.descRid = descRid;
	// this.clazz = clazz;
	// }

	VpnType(final String name, String nameStr, String descStr, final Class<? extends VpnProfile> clazz) {
		this.name = name;
		this.nameStr = nameStr;
		this.descStr = descStr;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public Class<? extends VpnProfile> getProfileClass() {
		return clazz;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean a) {
		this.active = a;
	}

	// public int getNameRid() {
	// return nameRid;
	// }
	//
	// public int getDescRid() {
	// return descRid;
	// }
	public String getDescStr() {
		return descStr;
	}

	public String getNameStr() {
		return nameStr;
	}

}