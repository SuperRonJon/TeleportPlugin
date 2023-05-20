package superronjon.teleportation;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Teleportation extends JavaPlugin {
	private Map<String, TeleportRequest> activeRequests;

	@Override
	public void onEnable() {
		activeRequests = new HashMap<>();
	}

	@Override
	public void onDisable() {
		activeRequests.clear();
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player) {
			Player requester = (Player) sender;
			if(label.equals("tele")) {
				if(args.length == 0) {
					requester.sendMessage("You must select a target");
					return false;
				}
				if(args.length > 1) {
					requester.sendMessage("You must select only one target");
					return false;
				}
				String targetName = args[0];
				Player target = Bukkit.getPlayer(targetName);
				if(target == null) {
					requester.sendMessage("Unable to find player " + targetName);
					return false;
				}

				if(sendTeleportRequest(target, requester)) {
					requester.sendMessage("Request has been sent to " + targetName);
				}
				else {
					requester.sendMessage("Unable to send request. " + targetName + " may have a request already pending.");
				}
			}
			else if(label.equalsIgnoreCase("Y")) {
				acceptTeleportRequest(requester);
			}
			else if(label.equalsIgnoreCase("N")) {
				rejectTeleportRequest(requester);
			}
		}
		return true;
	}

	private boolean sendTeleportRequest(Player target, Player requester) {
		String targetName = ((TextComponent) target.displayName()).content();
		String requesterName = ((TextComponent) requester.displayName()).content();

		if (activeRequests.containsKey(targetName)) {
			return false;
		}

		TeleportRequest request = new TeleportRequest(target, requester);
		activeRequests.put(targetName, request);
		target.sendMessage(requesterName + " is requesting to teleport to you. /Y to accept or /N to reject");
		return true;
	}

	private void acceptTeleportRequest(Player target) {
		String targetName = ((TextComponent) target.displayName()).content();

		if(activeRequests.containsKey(targetName)) {
			Player requester = activeRequests.get(targetName).getRequester();
			if(requester != null) {
				requester.sendMessage("Teleporting you to " + targetName);
				requester.teleport(target.getLocation());
			}
			else {
				target.sendMessage("Unable to find requester");
			}
			activeRequests.remove(targetName);
		}
		else {
			target.sendMessage("You do not have an active teleport request");
		}
	}

	private void rejectTeleportRequest(Player target) {
		String targetName = ((TextComponent) target.displayName()).content();

		if(activeRequests.containsKey(targetName)){
			Player requester = activeRequests.get(targetName).getRequester();
			String requesterName = ((TextComponent) requester.displayName()).content();
			target.sendMessage("Rejecting teleport request from " + requesterName);
			requester.sendMessage(targetName + " has rejected your teleport request");
			activeRequests.remove(targetName);
		}
		else {
			target.sendMessage("You do not have an active teleport request");
		}
	}
}
