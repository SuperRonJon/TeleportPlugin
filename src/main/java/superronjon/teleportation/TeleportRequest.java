package superronjon.teleportation;

import org.bukkit.entity.Player;

public class TeleportRequest {
	private Player requester;
	private Player target;

	public TeleportRequest(Player target, Player requester) {
		this.target = target;
		this.requester = requester;
	}

	public Player getRequester() {
		return requester;
	}

	public Player getTarget() {
		return target;
	}
}
