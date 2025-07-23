package mr_vokintos.autototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem implements ClientModInitializer {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final float healthThreshold = 10.0f;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (mc.player == null || mc.world == null) return;

            float currentHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            if (currentHealth <= healthThreshold) {
                equipTotemIfNeeded();
            }
        });
    }

    private void equipTotemIfNeeded() {
        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.getItem() == Items.TOTEM_OF_UNDYING) return;

        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                swapToOffhand(i);
                break;
            }
        }
    }

    private void swapToOffhand(int invSlot) {
        // Convert inventory slot to GUI slot ID
        int guiSlotId = 0;

        if (invSlot >= 0 && invSlot < 9) {
            guiSlotId = 36 + invSlot; // Hotbar slots
        } else if (invSlot >= 9 && invSlot < 36) {
            guiSlotId = invSlot - 9; // Inventory slots
        } else {
            return; // Invalid slot
        }

        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                guiSlotId,
                40,
                SlotActionType.SWAP,
                mc.player
        );
    }
}
