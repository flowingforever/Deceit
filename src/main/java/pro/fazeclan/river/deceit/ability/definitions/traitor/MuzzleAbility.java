package pro.fazeclan.river.deceit.ability.definitions.traitor;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

import java.util.concurrent.ThreadLocalRandom;

public class MuzzleAbility extends Ability {

    public MuzzleAbility(Deceit plugin) {
        super(plugin, "muzzle");
    }

    @EventHandler
    private void onMuzzleAbility(PlayerInteractAtEntityEvent event) {
        var user = event.getPlayer();
        if (!(event.getRightClicked() instanceof Player victim)) return;
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) return;
        var item = user.getInventory().getItemInMainHand();
        if (!hasAbility(item)) return;
        if (user.hasCooldown(item)) return;
        user.setCooldown(item, getProperty("cooldown", 5) * 20);
        var loc = victim.getLocation().clone();
        var role = Deceit.getInstance().getRoleManager().getRole("traitor");
        var mm = MiniMessage.miniMessage();
        int duration = getProperty("duration", 10);
        item.setAmount(item.getAmount() - 1);
        if (getProperty("instant", true)) {
            user.sendMessage(mm.deserialize(
                    role.getPrefix() + " " + NicknameUtil.getNickname(victim) + "<reset> is now muzzled! No one can hear them for " + duration + " seconds!"
            ));
            victim.addPotionEffect(new PotionEffect(
                    PotionEffectType.UNLUCK,
                    duration * 20,
                    0,
                    false,
                    false,
                    false
            ));
        } else {
            int minimumWaitTime = Math.max(getProperty("minimum-wait-time", 3), 0);
            int maximumWaitTime = Math.clamp(getProperty("maximum-wait-time", 10), minimumWaitTime, 30);
            long muzzleWaitTime = ThreadLocalRandom.current().nextLong(
                    minimumWaitTime * 20L,
                    maximumWaitTime * 20L
            );


            user.sendMessage(mm.deserialize(
                    role.getPrefix() + " " + NicknameUtil.getNickname(victim) + "<reset> will be muzzled in " + muzzleWaitTime / 20 + " seconds!"
            ));
            Bukkit.getScheduler().runTaskLater(
                    getPlugin(),
                    () -> {
                        if (!loc.isWorldLoaded()) {
                            return;
                        }

                        user.sendMessage(mm.deserialize(
                                role.getPrefix() + " " + NicknameUtil.getNickname(victim) + "<reset> is now muzzled! No one can hear them for " + duration + " seconds!"
                        ));
                        victim.addPotionEffect(new PotionEffect(
                                PotionEffectType.UNLUCK,
                                duration * 20,
                                0,
                                false,
                                false,
                                false
                        ));
                    },
                    muzzleWaitTime
            );
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.SLIME_BALL.createItemStack();
    }

}
