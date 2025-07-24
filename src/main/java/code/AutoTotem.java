package mr_vokintos.autototem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoTotem {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int delay = 0;
    private int ticks = 0;
    private boolean locked = false;

    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        int totemSlot = findTotemSlot();
        int totemCount = countTotems();

        if (totemCount <= 0) {
            locked = false;
        } else if (ticks >= delay) {
            locked = true;

            ItemStack offhand = mc.player.getOffHandStack();
            if (offhand.getItem() != Items.TOTEM_OF_UNDYING && totemSlot != -1) {
                moveToOffhand(totemSlot);
            }

            ticks = 0;
        } else {
            ticks++;
        }
    }

    public void onPacket(EntityStatusS2CPacket packet) {
        if (packet.getStatus() != EntityStatuses.USE_TOTEM_OF_UNDYING) return;

        Entity entity = packet.getEntity(mc.world);
        if (entity != mc.player) return;

        ticks = 0;
    }

    public boolean isLocked() {
        return locked;
    }

    public int countTotems() {
        int count = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                count += stack.getCount();
            }
        }

        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.getItem() == Items.TOTEM_OF_UNDYING) {
            count += offhand.getCount();
        }

        return count;
    }

    private int findTotemSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }

    private void moveToOffhand(int slot) {
        if (mc.interactionManager == null) return;

        int syncId = mc.player.currentScreenHandler.syncId;
        int offhandSlot = 45;

        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, offhandSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }
    }
