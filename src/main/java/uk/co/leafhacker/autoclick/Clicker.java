package uk.co.leafhacker.autoclick;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import uk.co.leafhacker.autoclick.mixin.AccessorMinecraftClient;

public class Clicker {

    private static final String KEY_ID = "autoclick:toggle_attack_entities";
    private static final String CATEGORY_ID = "key.autoclick";

    private FabricKeyBinding keybind;
    private boolean enabled;

    Clicker() {
        keybind = FabricKeyBinding.Builder.create(new Identifier(KEY_ID), Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY_ID).build();
        KeyBindingRegistry.INSTANCE.addCategory(CATEGORY_ID);
        KeyBindingRegistry.INSTANCE.register(keybind);

        ClientTickCallback.EVENT.register(minecraftClient -> this.tick());
    }

    void tick() {
        if (keybind != null && keybind.wasPressed()) {
            enabled = !enabled;
        }
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        if (client.world == null) return;
        if (client.currentScreen != null) return;

        // Don't click if using an item
        if (player.isUsingItem()) return;

        // Don't click if cooldown active
        if (player.getAttackCooldownProgress(0) < 1) return;

        // Don't click if nothing targeted
        if (client.crosshairTarget == null) return;
        if (!client.crosshairTarget.getType().equals(HitResult.Type.ENTITY)) return;

        ((AccessorMinecraftClient) client).runDoAttack();
    }
}
