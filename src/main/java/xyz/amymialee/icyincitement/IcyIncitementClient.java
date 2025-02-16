package xyz.amymialee.icyincitement;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import xyz.amymialee.icyincitement.entity.IceBallEntityRenderer;

@Environment(EnvType.CLIENT)
public class IcyIncitementClient implements ClientModInitializer {
    public @Override void onInitializeClient() {
        EntityRendererRegistry.register(IcyIncitement.ICEBALL, IceBallEntityRenderer::new);
    }
}