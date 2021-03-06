/*
 * Copyright (c) 2010 SimpleServer authors (see CONTRIBUTORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package simpleserver.command;

import static simpleserver.lang.Translations.t;
import simpleserver.Color;
import simpleserver.Player;
import simpleserver.Server;

public class KickCommand extends OnlinePlayerArgCommand implements ServerCommand {
  public KickCommand() {
    super("kick PLAYER [REASON]", "Kick the named player from the server");
  }

  protected boolean allowed(Player player, Player target) {
    if (target.getGroupId() >= player.getGroupId()) {
      player.addTMessage(Color.RED, "You cannot kick players that are in your group or higher!");
      return false;
    }
    return true;
  }

  @Override
  protected void executeWithTarget(Player player, String message, Player target) {
    if (allowed(player, target)) {
      String reason = kick(player.getServer(), message, target);
      player.getServer().adminLog("Admin " + player.getName() + " kicked player:\t " + target.getName() + "\t(" + reason + ")");
    }
  }

  @Override
  protected void executeWithTarget(Server server, String message, Player target, CommandFeedback feedback) {
    String reason = kick(server, message, target);
    server.adminLog("Console kicked player:\t " + target.getName() + "\t(" + reason + ")");
    feedback.send("Kicked player %s", target.getName());
  }

  private String kick(Server server, String message, Player target) {
    String reason = extractArgument(message, 1);
    if (reason == null) {
      reason = t("Kicked by admin.");
    }
    target.kick(reason);
    String msg = t("Player %s has been kicked! (%s)", target.getName(), reason);
    server.runCommand("say", msg);
    return reason;
  }

  @Override
  protected String noTargetSpecified() {
    return "No player or reason specified.";
  }
}
